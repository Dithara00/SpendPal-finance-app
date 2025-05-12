package com.example.spendpal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddIncomeActivity extends AppCompatActivity {

    private EditText incomeTitle, incomeAmount;
    private Button addIncomeBtn, backBtn;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_income);

        // Initialize views
        incomeTitle = findViewById(R.id.incomeTitle);
        incomeAmount = findViewById(R.id.incomeAmount);
        addIncomeBtn = findViewById(R.id.btnNew);
        backBtn = findViewById(R.id.backBtn);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set button click listener for adding income
        addIncomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIncome();
            }
        });

        // Set button click listener for back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // This will navigate to the previous activity
            }
        });
    }

    private void addIncome() {
        // Get the input values
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

        // Convert amount to a number (double)
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

        // Get the userId from Firebase Authentication
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        // Create a new income record
        Income income = new Income(title, amount, userId);

        // Add to Firestore
        db.collection("incomes")
                .add(income)
                .addOnSuccessListener(documentReference -> {
                    // Show success message
                    Toast.makeText(AddIncomeActivity.this, "Income added successfully!", Toast.LENGTH_SHORT).show();
                    // Optionally navigate back or clear fields
                    incomeTitle.setText("");
                    incomeAmount.setText("");
                })
                .addOnFailureListener(e -> {
                    // Show error message
                    Toast.makeText(AddIncomeActivity.this, "Error adding income", Toast.LENGTH_SHORT).show();
                });
    }
}
