import java.io.IOException;

public class CloseJavaFXApp {
    public static void main(String[] args) {
        try {
            // Get the list of running Java processes
            Process process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq java.exe\" /FO CSV");
            
            // Read the output
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
            System.out.println("\nDo you want to kill all Java processes? (y/n)");
            int response = System.in.read();
            
            if (response == 'y' || response == 'Y') {
                // Kill all Java processes
                Runtime.getRuntime().exec("taskkill /F /IM java.exe");
                System.out.println("All Java processes have been terminated.");
            } else {
                System.out.println("Operation cancelled.");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
