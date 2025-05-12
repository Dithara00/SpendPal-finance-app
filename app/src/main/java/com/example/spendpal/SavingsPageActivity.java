package com.example.spendpal;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavingsPageActivity extends AppCompatActivity {

    private TextView tvTotalIncome, tvTotalExpenses, tvTotalSavings;
    private TextView savingsProgressText;
    private ProgressBar savingsProgressBar;
    private BarChart barChart;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private double totalIncome = 0;
    private double totalExpenses = 0;
    private boolean incomeFetched = false;
    private boolean expensesFetched = false;

    // Set your desired savings goal
    private final double savingsGoal = 10000.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings_page);

        // Initialize views
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvTotalSavings = findViewById(R.id.tvTotalSavings);
        savingsProgressBar = findViewById(R.id.savingsProgressBar);
        savingsProgressText = findViewById(R.id.savingsProgressText);
        barChart = findViewById(R.id.barChart);
        Button backBtn = findViewById(R.id.backBtn);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to view savings", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = currentUser.getUid();

        // Fetch data
        fetchIncomeData();
        fetchExpenseData();

        // Back button
        backBtn.setOnClickListener(view -> finish());
    }

    private void fetchIncomeData() {
        db.collection("incomes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        totalIncome = 0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Double amount = doc.getDouble("amount");
                            if (amount != null) {
                                totalIncome += amount;
                            }
                        }
                        tvTotalIncome.setText("Rs." + String.format("%.2f", totalIncome));
                        incomeFetched = true;
                        checkAndDisplayChart();
                    } else {
                        Toast.makeText(this, "Failed to fetch income data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchExpenseData() {
        db.collection("expenses")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        totalExpenses = 0;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Double amount = doc.getDouble("amount");
                            if (amount != null) {
                                totalExpenses += amount;
                            }
                        }
                        tvTotalExpenses.setText("Rs." + String.format("%.2f", totalExpenses));
                        expensesFetched = true;
                        checkAndDisplayChart();
                    } else {
                        Toast.makeText(this, "Failed to fetch expense data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndDisplayChart() {
        if (incomeFetched && expensesFetched) {
            calculateAndDisplaySavings();
        }
    }

    private void calculateAndDisplaySavings() {
        double savings = totalIncome - totalExpenses;
        tvTotalSavings.setText("Rs." + String.format("%.2f", savings));
        showSavingsProgressBar(savings);
        showBarChart();
    }

    private void showSavingsProgressBar(double savings) {
        int progress = (int) ((savings / savingsGoal) * 100);
        if (progress > 100) progress = 100;

        savingsProgressBar.setProgress(progress);
        savingsProgressText.setText(progress + "% of goal saved");
    }

    private void showBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (float) totalIncome));
        entries.add(new BarEntry(1f, (float) totalExpenses));
        entries.add(new BarEntry(2f, (float) (totalIncome - totalExpenses)));

        BarDataSet dataSet = new BarDataSet(entries, "Financial Summary");
        dataSet.setColors(Color.GREEN, Color.RED, Color.BLUE);
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int) value) {
                    case 0: return "Income";
                    case 1: return "Expenses";
                    case 2: return "Savings";
                    default: return "";
                }
            }
        });

        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.animateY(1000);
        barChart.invalidate(); // Refresh the chart
    }
}
