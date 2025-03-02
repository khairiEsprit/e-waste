import os
import time
import ollama
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import Chroma
from langchain_community.embeddings import OllamaEmbeddings
from langchain_core.documents import Document  

# Define paths
TEXT_PATH = "Data.txt"
CHROMA_DB_PATH = "./chroma_db"
QUESTION_FILE = "question.txt"
RESPONSE_FILE = "response.txt"

# Load text content from file
def load_text(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        return f.read()

# Initialize Ollama embeddings
embeddings = OllamaEmbeddings(model="nomic-embed-text:latest")

# Load or create vector store
if not os.path.exists(CHROMA_DB_PATH):
    print("Creating new vector store...")
    text_content = load_text(TEXT_PATH)
    text_splitter = RecursiveCharacterTextSplitter(chunk_size=500, chunk_overlap=50)
    splits = text_splitter.split_text(text_content)
    documents = [Document(page_content=line, metadata={"name": line.split("travaille")[0].strip()}) 
                 for line in text_content.split("\n\n")]
    vectorstore = Chroma.from_documents(documents=documents, embedding=embeddings, persist_directory=CHROMA_DB_PATH)
    vectorstore.persist()
else:
    vectorstore = Chroma(persist_directory=CHROMA_DB_PATH, embedding_function=embeddings)

# Create the retriever
retriever = vectorstore.as_retriever(search_kwargs={"k": 1})

# Format retrieved documents
def format_docs(docs):
    return "\n\n".join(doc.page_content for doc in docs)

def ollama_llm(question, context):
    formatted_prompt = (
        "Your name is FediBot. "
        "You are a smart assistant that extracts information from employee records. "
        "Give me only the exact response, and format the answer clearly. "
        "If the answer is not in the provided context, say 'Based on my information, I can't respond'.\n\n"
        f"Context: {context}\n\n"
        f"Question: {question}"
    )
    response = ollama.chat(model='qwen:0.5b', messages=[{'role': 'user', 'content': formatted_prompt}])
    answer = response['message']['content']
    if 'Based on my information, I can\'t respond' not in answer:
        answer = f"Date de fin du contrat: {answer.strip()}"
    return answer

# RAG chain
def rag_chain(question):
    retrieved_docs = retriever.invoke(question)
    formatted_context = format_docs(retrieved_docs)
    return ollama_llm(question, formatted_context)

# Main loop to monitor question.txt and write response to response.txt
def main():
    while True:
        if os.path.exists(QUESTION_FILE):
            try:
                with open(QUESTION_FILE, 'r', encoding='utf-8') as f:
                    question = f.read().strip()
                
                if question:
                    print(f"Processing question: {question}")
                    response = rag_chain(question)
                    
                    # Write response to response.txt
                    with open(RESPONSE_FILE, 'w', encoding='utf-8') as f:
                        f.write(response)
                    
                    # Remove question.txt after processing
                    os.remove(QUESTION_FILE)
            except Exception as e:
                print(f"Error: {e}")
                with open(RESPONSE_FILE, 'w', encoding='utf-8') as f:
                    f.write(f"Erreur: {e}")
                os.remove(QUESTION_FILE)
        
        time.sleep(1)  # Check every second to avoid high CPU usage

if __name__ == "__main__":
    main()