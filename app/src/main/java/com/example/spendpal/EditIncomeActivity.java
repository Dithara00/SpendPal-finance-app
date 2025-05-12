package com.example.spendpal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditIncomeActivity extends AppCompatActivity {

    private EditText etTitle, etAmount;
    private Button btnUpdate;

    private FirebaseFirestore db;
    private String incomeId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_income);

        etTitle = findViewById(R.id.etEditTitle);
        etAmount = findViewById(R.id.etEditAmount);
        btnUpdate = findViewById(R.id.btnUpdateIncome);

        db = FirebaseFirestore.getInstance();

        // Get passed data
        incomeId = getIntent().getStringExtra("incomeId");
        String title = getIntent().getStringExtra("title");
        double amount = getIntent().getDoubleExtra("amount", 0.0);

        if (incomeId == null || title == null) {
            Toast.makeText(this, "Invalid income data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etTitle.setText(title);
        etAmount.setText(String.valueOf(amount));

        btnUpdate.setOnClickListener(v -> updateIncome());
    }

    private void updateIncome() {
        String newTitle = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Please enter both title and amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double newAmount;
        try {
            newAmount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("incomes").document(incomeId)
                .update("title", newTitle, "amount", newAmount)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Income updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}
