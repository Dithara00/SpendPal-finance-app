package com.example.spendpal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

public class ExpensePageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<ExpenseModel> expenseList;
    private FirebaseFirestore db;
    private TextView tvHighestExpense, tvLeastExpense;
    private Button btnAdd, btnBack;
    private CustomPieChartView pieChartView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_page);

        pieChartView = findViewById(R.id.customPieChart);
        recyclerView = findViewById(R.id.expenseRecycler);
        tvHighestExpense = findViewById(R.id.tvHighestExpense);
        tvLeastExpense = findViewById(R.id.tvLeastExpense);
        btnAdd = findViewById(R.id.btnAddExpense);
        btnBack = findViewById(R.id.backBtn);

        db = FirebaseFirestore.getInstance();
        expenseList = new ArrayList<>();

        adapter = new ExpenseAdapter(expenseList, new ExpenseAdapter.OnExpenseItemClickListener() {
            @Override
            public void onEditClick(ExpenseModel expense, int position) {
                Intent intent = new Intent(ExpensePageActivity.this, EditExpensesActivity.class);
                intent.putExtra("expenseId", expense.getId());
                intent.putExtra("title", expense.getTitle());
                intent.putExtra("amount", expense.getAmount());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(ExpenseModel expense, int position) {
                db.collection("expenses").document(expense.getId()).delete();
            }

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddExpenseActivity.class)));
        btnBack.setOnClickListener(v -> finish());

        listenToExpenses();  // Real-time listener instead of one-time load
    }

    private void listenToExpenses() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        db.collection("expenses")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots == null) return;

                    expenseList.clear();
                    List<Float> expensesValues = new ArrayList<>();
                    List<Integer> colors = new ArrayList<>();
                    List<String> titles = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ExpenseModel expense = doc.toObject(ExpenseModel.class);
                        if (expense != null) {
                            expense.setId(doc.getId());
                            expenseList.add(expense);
                            expensesValues.add((float) expense.getAmount());
                            titles.add(expense.getTitle());
                            colors.add(Color.rgb(new Random().nextInt(256),
                                    new Random().nextInt(256),
                                    new Random().nextInt(256)));
                        }
                    }

                    adapter.notifyDataSetChanged();
                    pieChartView.setData(expensesValues, colors, titles);
                    pieChartView.invalidate(); // Refresh chart
                    calculateInsights();
                });
    }

    private void calculateInsights() {
        if (expenseList.isEmpty()) {
            tvHighestExpense.setText("No expense data");
            tvLeastExpense.setText("No expense data");
            return;
        }

        // Step 1: Find max and min values
        double maxAmount = Double.MIN_VALUE;
        double minAmount = Double.MAX_VALUE;

        for (ExpenseModel expense : expenseList) {
            double amount = expense.getAmount();
            if (amount > maxAmount) maxAmount = amount;
            if (amount < minAmount) minAmount = amount;
        }

        // Step 2: Collect all titles that match max and min
        List<String> highestTitles = new ArrayList<>();
        List<String> lowestTitles = new ArrayList<>();

        for (ExpenseModel expense : expenseList) {
            if (expense.getAmount() == maxAmount) {
                highestTitles.add(expense.getTitle());
            }
            if (expense.getAmount() == minAmount) {
                lowestTitles.add(expense.getTitle());
            }
        }

        // Step 3: Display all matching titles
        tvHighestExpense.setText("You had spent more money on - " + String.join(", ", highestTitles));
        tvLeastExpense.setText("You had spent less money on - " + String.join(", ", lowestTitles));
    }


    @Override
    protected void onResume() {
        super.onResume();
        // No need to reload manually because snapshotListener keeps it live
    }
}
