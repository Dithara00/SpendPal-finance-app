package com.example.spendpal;

public class Expense {
    private String title;
    private double amount;
    private String userId;

    public Expense() {
        // Required empty constructor for Firestore
    }

    public Expense(String title, double amount, String userId) {
        this.title = title;
        this.amount = amount;
        this.userId = userId;
    }

    // Getters
    public String getTitle() { return title; }
    public double getAmount() { return amount; }
    public String getUserId() { return userId; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setUserId(String userId) { this.userId = userId; }
}
