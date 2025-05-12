package com.example.spendpal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class IncomePageActivity extends AppCompatActivity {

    private CustomPieChartView pieChartView;
    private FirebaseFirestore db;
    private List<Float> incomeValues = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private Random random = new Random();

    private RecyclerView recyclerView;
    private IncomeAdapter incomeAdapter;
    private List<IncomeModel> incomeList = new ArrayList<>();

    private Button btnBackDashboard, btnAddIncome, btnToSavings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.income_page);

        pieChartView = findViewById(R.id.customPieChart);
        recyclerView = findViewById(R.id.incomeRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        incomeAdapter = new IncomeAdapter(incomeList, new IncomeAdapter.OnIncomeItemClickListener() {
            @Override
            public void onEditClick(IncomeModel income, int position) {
                Intent intent = new Intent(IncomePageActivity.this, EditIncomeActivity.class);
                intent.putExtra("incomeId", income.getId());  // Pass incomeId
                intent.putExtra("title", income.getTitle());
                intent.putExtra("amount", income.getAmount());
                startActivity(intent);

            }

            @Override
            public void onDeleteClick(IncomeModel income, int position) {
                deleteIncomeFromFirestore(income.getTitle());
            }
        });

        recyclerView.setAdapter(incomeAdapter);

        btnBackDashboard = findViewById(R.id.backBtn);
        btnAddIncome = findViewById(R.id.btnAddIncome);
        btnToSavings = findViewById(R.id.btnSavings);

        btnBackDashboard.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        btnAddIncome.setOnClickListener(v -> startActivityForResult(new Intent(this, AddIncomeActivity.class), 1));
        btnToSavings.setOnClickListener(v -> startActivity(new Intent(this, SavingsPageActivity.class)));

        loadIncomeData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIncomeData();
    }

    private void loadIncomeData() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("incomes")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Failed to load income data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    incomeList.clear();
                    incomeValues.clear();
                    colors.clear();
                    titles.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String docId = doc.getId(); // Get the document ID
                        String title = doc.getString("title");
                        double amount = doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0;

                        if (title != null && !title.isEmpty()) {
                            incomeList.add(new IncomeModel(docId, title, amount));  // Use docId here
                            incomeValues.add((float) amount);
                            colors.add(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                            titles.add(title);
                        }
                    }

                    incomeAdapter.notifyDataSetChanged();
                    pieChartView.setData(incomeValues, colors, titles);
                });
    }


    private void deleteIncomeFromFirestore(String title) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("incomes")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                    Toast.makeText(this, "Income deleted", Toast.LENGTH_SHORT).show();
                    loadIncomeData();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadIncomeData();
        }
    }
}
