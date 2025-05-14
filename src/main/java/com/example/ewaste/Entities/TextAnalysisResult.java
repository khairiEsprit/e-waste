package com.example.ewaste.Entities;

public class TextAnalysisResult {
    private final boolean containsBadWords;
    private final String categories;

    public TextAnalysisResult(boolean containsBadWords, String categories) {
        this.containsBadWords = containsBadWords;
        this.categories = categories;
    }

    public boolean isContainsBadWords() {
        return containsBadWords;
    }

    public String getCategories() {
        return categories;
    }
}
