package com.example.spendpal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditExpensesActivity extends AppCompatActivity {

    private EditText etTitle, etAmount;
    private Button btnUpdate;
    private FirebaseFirestore db;
    private String expenseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_expense);

        etTitle = findViewById(R.id.etEditTitle);
        etAmount = findViewById(R.id.etEditAmount);
        btnUpdate = findViewById(R.id.btnUpdateExpense);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        expenseId = intent.getStringExtra("expenseId");
        String title = intent.getStringExtra("title");
        double amount = intent.getDoubleExtra("amount", 0.0);

        etTitle.setText(title);
        etAmount.setText(String.valueOf(amount));

        btnUpdate.setOnClickListener(view -> {
            String updatedTitle = etTitle.getText().toString().trim();
            String updatedAmountStr = etAmount.getText().toString().trim();

            if (updatedTitle.isEmpty() || updatedAmountStr.isEmpty()) {
                Toast.makeText(this, "Please enter valid data", Toast.LENGTH_SHORT).show();
                return;
            }

            double updatedAmount = Double.parseDouble(updatedAmountStr);

            db.collection("expenses")
                    .document(expenseId)
                    .update("title", updatedTitle, "amount", updatedAmount)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
        });
    }
}
