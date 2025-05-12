package com.example.spendpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;

public class MainActivity extends AppCompatActivity {

    private BarChart barChart;
    private ProgressBar savingsProgressBar, expensesProgressBar;
    private TextView savingsPercentText, expensesPercentText;
    private Button btnIncome, btnExpense, btnToday, btnGoals, logoutBtn;
    private ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Set progress values (for demonstration)
        int savingsPercent = 60;
        int expensesPercent = 40;
        savingsProgressBar.setProgress(savingsPercent);
        expensesProgressBar.setProgress(expensesPercent);
        savingsPercentText.setText(savingsPercent + "%");
        expensesPercentText.setText(expensesPercent + "%");

        // Income button click
        btnIncome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, IncomePageActivity.class);
            startActivity(intent);
        });

        // Expense button click
        btnExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExpensePageActivity.class);
            startActivity(intent);
        });

        // Today button click
        btnToday.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Goals button click
        btnGoals.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Logout button click
        logoutBtn.setOnClickListener(v -> {
            // Optional: Add logout logic here (Firebase sign-out, sharedPref clear etc.)
            finishAffinity(); // Closes all activities and exits the app
        });

        // Profile icon click (optional behavior)
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // (Optional) Setup Bar Chart
        setupBarChart();
    }

    private void setupBarChart() {
        // You can populate the bar chart with actual data here
        // For now, you can leave it empty or use a dummy dataset
    }
}
