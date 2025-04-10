package com.example.trucart.activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trucart.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });

        btnEditProfile.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
        });
    }
}


