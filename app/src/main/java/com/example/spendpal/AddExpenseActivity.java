package com.example.spendpal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText incomeTitle, incomeAmount;  // Reused IDs from XML
    private Button addExpenseBtn, backBtn;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense); // your XML layout

        // Initialize views
        incomeTitle = findViewById(R.id.expenseTitle);
        incomeAmount = findViewById(R.id.expenseAmount);
        addExpenseBtn = findViewById(R.id.btnNew);
        backBtn = findViewById(R.id.backBtn);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Button click to add expense
        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();
            }
        });

        // Back button click
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Go back
            }
        });
    }

    private void addExpense() {
        String title = incomeTitle.getText().toString().trim();
        String amountStr = incomeAmount.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            incomeTitle.setError("Title is required!");
            return;
        }
        if (amountStr.isEmpty()) {
            incomeAmount.setError("Amount is required!");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                incomeAmount.setError("Amount must be greater than 0!");
                return;
            }
        } catch (NumberFormatException e) {
            incomeAmount.setError("Invalid amount!");
            return;
        }

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        Expense expense = new Expense(title, amount, userId);

        db.collection("expenses")
                .add(expense)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddExpenseActivity.this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
                    incomeTitle.setText("");
                    incomeAmount.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddExpenseActivity.this, "Error adding expense", Toast.LENGTH_SHORT).show();
                });
    }
}
