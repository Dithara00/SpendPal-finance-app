package com.example.spendpal;

public class Income {
    private String title;
    private double amount;
    private String userId;

    // Empty constructor required by Firestore
    public Income() {}

    // Constructor to initialize Income objects
    public Income(String title, double amount, String userId) {
        this.title = title;
        this.amount = amount;
        this.userId = userId;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Getter for amount
    public double getAmount() {
        return amount;
    }

    // Getter for userId
    public String getUserId() {
        return userId;
    }

    // Optional: You can also add setters if you need to modify these fields later
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
