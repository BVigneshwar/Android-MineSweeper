package com.example.minisweeper;

public class FeedbackHandler {
    private String name;
    private String comments;
    private int rating;

    public FeedbackHandler(String name, String comments, int rating) {
        this.name = name;
        this.comments = comments;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getComments() {
        return comments;
    }

    public int getRating() {
        return rating;
    }
}
