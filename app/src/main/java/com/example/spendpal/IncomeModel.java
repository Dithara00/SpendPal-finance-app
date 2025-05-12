package com.example.spendpal;

public class IncomeModel {

    private String title;
    private double amount;
    private String id;

    // Required empty constructor for Firebase
    public IncomeModel() {}

    // Constructor with parameters to initialize IncomeModel
    public IncomeModel(String id, String title, double amount) {
        this.id = id;
        this.title = title;
        this.amount = amount;
    }

    // Getter method for the id
    public String getId() {
        return id;
    }

    // Getter method for the title
    public String getTitle() {
        return title;
    }

    // Getter method for the amount
    public double getAmount() {
        return amount;
    }

    // Setter method for the id
    public void setId(String id) {
        this.id = id;
    }

    // Setter method for the title
    public void setTitle(String title) {
        this.title = title;
    }

    // Setter method for the amount
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
