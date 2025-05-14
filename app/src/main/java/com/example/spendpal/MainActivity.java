package com.example.spendpal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BarChart barChart;
    private ProgressBar savingsProgressBar, expensesProgressBar;
    private TextView savingsPercentText, expensesPercentText;
    private Button btnIncome, btnExpense, btnToday, btnGoals, logoutBtn;
    private ImageView profileIcon, rewardsIcon;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private double totalIncome = 0;
    private double totalExpense = 0;
    private double savingsGoal = 10000.0; // Example goal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure this XML matches yours

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        barChart = findViewById(R.id.barChart);
        savingsProgressBar = findViewById(R.id.savingsProgressBar);
        expensesProgressBar = findViewById(R.id.expensesProgressBar);
        savingsPercentText = findViewById(R.id.savingsPercentText);
        expensesPercentText = findViewById(R.id.expensesPercentText);
        btnIncome = findViewById(R.id.btnIncome);
        btnExpense = findViewById(R.id.btnExpense);
        btnToday = findViewById(R.id.btnToday);
        btnGoals = findViewById(R.id.btnGoals);
        logoutBtn = findViewById(R.id.logoutBtn);
        profileIcon = findViewById(R.id.profileIcon);
        rewardsIcon = findViewById(R.id.rewardsIcon);

        // Button listeners
        btnIncome.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, IncomePageActivity.class)));
        btnExpense.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExpensePageActivity.class)));
        btnToday.setOnClickListener(v -> recreate());
        btnGoals.setOnClickListener(v -> recreate());
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            finishAffinity();
        });

        profileIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        rewardsIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RewardsActivity.class)));

        // Load financial data
        loadFinancialData();
    }

    private void loadFinancialData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String userId = currentUser.getUid();

        // Fetch income
        db.collection("incomes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        totalIncome = 0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Double amount = doc.getDouble("amount");
                            if (amount != null) totalIncome += amount;
                        }
                        fetchExpenses(userId);
                    } else {
                        Log.e("MainActivity", "Failed to fetch incomes.");
                    }
                });
    }

    private void fetchExpenses(String userId) {
        db.collection("expenses")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        totalExpense = 0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Double amount = doc.getDouble("amount");
                            if (amount != null) totalExpense += amount;
                        }
                        updateUI();
                    } else {
                        Log.e("MainActivity", "Failed to fetch expenses.");
                    }
                });
    }

    private void updateUI() {
        double savings = totalIncome - totalExpense;
        double total = savings + totalExpense;

        int savingsPercent = total > 0 ? (int) ((savings / total) * 100) : 0;
        int expensesPercent = total > 0 ? (int) ((totalExpense / total) * 100) : 0;

        savingsProgressBar.setProgress(savingsPercent);
        expensesProgressBar.setProgress(expensesPercent);

        savingsPercentText.setText(savingsPercent + "%");
        expensesPercentText.setText(expensesPercent + "%");

        showBarChart(totalIncome, totalExpense, savings);
    }

    private void showBarChart(double income, double expense, double savings) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (float) income));
        entries.add(new BarEntry(1f, (float) expense));
        entries.add(new BarEntry(2f, (float) savings));

        BarDataSet dataSet = new BarDataSet(entries, "Financial Overview");
        dataSet.setColors(Color.GREEN, Color.RED, Color.BLUE);
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int) value) {
                    case 0:
                        return "Income";
                    case 1:
                        return "Expenses";
                    case 2:
                        return "Savings";
                    default:
                        return "";
                }
            }
        });

        barChart.animateY(1000);
        barChart.invalidate(); // Refresh
    }
}
