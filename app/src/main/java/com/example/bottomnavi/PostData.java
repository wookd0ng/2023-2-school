package com.example.bottomnavi;

public class PostData {
    private String text;
    private String imageUrl;

    public PostData() {
        // Default constructor required for Firebase
    }

    public PostData(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}