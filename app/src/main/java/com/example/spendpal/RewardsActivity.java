package com.example.spendpal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RewardsActivity extends AppCompatActivity {

    private TextView starsEarnedText, starProgressText, pointsTextView, tasksCompletedTextView, goalsCompletedTextView;
    private ProgressBar starProgressBar;
    private LinearLayout starLayout;
    private Button backBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private static final int MAX_STARS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards); // Make sure your XML is named correctly

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI elements
        starsEarnedText = findViewById(R.id.starsEarnedText);
        starProgressText = findViewById(R.id.starProgressText);
        pointsTextView = findViewById(R.id.pointsTextView);
        tasksCompletedTextView = findViewById(R.id.tasksCompletedTextView);
        goalsCompletedTextView = findViewById(R.id.goalsCompletedTextView);
        starProgressBar = findViewById(R.id.starProgressBar);
        starLayout = findViewById(R.id.starLayout);
        backBtn = findViewById(R.id.backBtn);

        if (currentUser != null) {
            loadUserRewards(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Back button action
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void loadUserRewards(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long starsEarned = documentSnapshot.getLong("starsEarned");
                        Long totalPoints = documentSnapshot.getLong("totalPoints");
                        Long tasksCompleted = documentSnapshot.getLong("tasksCompleted");
                        Long goalsCompleted = documentSnapshot.getLong("goalsCompleted");

                        int stars = starsEarned != null ? starsEarned.intValue() : 0;
                        int points = totalPoints != null ? totalPoints.intValue() : 0;
                        int tasks = tasksCompleted != null ? tasksCompleted.intValue() : 0;
                        int goals = goalsCompleted != null ? goalsCompleted.intValue() : 0;

                        updateUI(stars, points, tasks, goals);
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RewardsActivity", "Error fetching user data", e);
                    Toast.makeText(this, "Error loading rewards", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI(int stars, int points, int tasks, int goals) {
        starsEarnedText.setText("You earned " + stars + " stars");
        starProgressText.setText("★ " + stars + " / " + MAX_STARS);
        starProgressBar.setMax(MAX_STARS);
        starProgressBar.setProgress(stars);

        pointsTextView.setText("Total Points: " + points);
        tasksCompletedTextView.setText("Tasks Completed: " + tasks);
        goalsCompletedTextView.setText("Goals Completed: " + goals);

        drawStars(stars);
    }

    private void drawStars(int count) {
        starLayout.removeAllViews(); // Clear previous icons

        Drawable starDrawable = ContextCompat.getDrawable(this, android.R.drawable.btn_star_big_on);

        for (int i = 0; i < count; i++) {
            ImageView star = new ImageView(this);
            star.setImageDrawable(starDrawable);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60, 60);
            params.setMargins(8, 0, 8, 0);
            star.setLayoutParams(params);
            starLayout.addView(star);
        }
    }
}
