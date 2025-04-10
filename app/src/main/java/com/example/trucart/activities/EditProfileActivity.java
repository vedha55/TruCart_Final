package com.example.trucart.activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trucart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnBack = findViewById(R.id.btnBack);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            editEmail.setText(currentUser.getEmail());
            if (currentUser.getDisplayName() != null) {
                editName.setText(currentUser.getDisplayName());
            }
        }

        btnUpdate.setOnClickListener(view -> updateProfile());

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
            finish();
        });
    }

    private void updateProfile() {
        String newName = editName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();

        if (!newName.isEmpty()) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (!newEmail.isEmpty() && !newEmail.equals(currentUser.getEmail())) {
            currentUser.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (!newPassword.isEmpty() && newPassword.length() >= 6) {
            currentUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
