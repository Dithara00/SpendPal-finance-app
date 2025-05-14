package com.example.spendpal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextInputEditText editName, editEmail, editPhone, editDob;
    MaterialButton btnSave, btnBack;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    DocumentReference userRef;

    Calendar selectedCalendar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editDob = findViewById(R.id.editDob);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = firestore.collection("users").document(user.getUid());

        editEmail.setText(user.getEmail());
        editEmail.setEnabled(false);

        loadUserProfile();

        btnSave.setOnClickListener(v -> saveUserProfile());
        btnBack.setOnClickListener(v -> onBackPressed());

        // DOB field click listener
        editDob.setOnClickListener(view -> {
            showDatePicker();
        });
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editName.setText(documentSnapshot.getString("name"));
                        editPhone.setText(documentSnapshot.getString("phone"));
                        editDob.setText(documentSnapshot.getString("dob"));
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void saveUserProfile() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String dob = editDob.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editName.setError("Name is required");
            return;
        }
        if (!name.matches("^[A-Za-z ]+$")) {
            editName.setError("Only letters and spaces allowed");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("Phone number is required");
            return;
        }
        if (!phone.matches("^[0-9]{10}$")) {
            editPhone.setError("Enter a valid 10-digit number");
            return;
        }

        if (TextUtils.isEmpty(dob)) {
            editDob.setError("Date of birth is required");
            return;
        }

        Calendar today = Calendar.getInstance();
        if (selectedCalendar != null && selectedCalendar.after(today)) {
            editDob.setError("DOB cannot be in the future");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("dob", dob);
        userData.put("email", user.getEmail());

        userRef.set(userData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);
                    String formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    editDob.setText(formatted);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.show();
    }
}
