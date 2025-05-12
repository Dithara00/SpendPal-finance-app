package com.example.spendpal;

public class ExpenseModel {
    private String id;
    private String title;
    private double amount;

    public ExpenseModel() {
        // Default constructor required for Firestore
    }

    public ExpenseModel(String id, String title, double amount) {
        this.id = id;
        this.title = title;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
