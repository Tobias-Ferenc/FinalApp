package com.tobiasferenc.finalapp.ui;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tobiasferenc.finalapp.R;
import com.tobiasferenc.finalapp.data.dao.UserDao;
import com.tobiasferenc.finalapp.data.database.AppDatabase;


public class MainActivity extends AppCompatActivity {



    AppDatabase db;
    UserDao userDao;


    // Přidej instanci pro práci s EncryptedSharedPreferences
    private MyPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializuj MyPreferencesManager
        preferencesManager = new MyPreferencesManager();
        preferencesManager.initEncryptedPreferences(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = AppDatabase.getInstance(this);
        userDao = db.userDao();
    }

public void switchRegister(View v) {
    Intent intent = new Intent(MainActivity.this, Register.class);
    startActivity(intent);
}
    public void switchProfile(View v) {
        Intent intent = new Intent(MainActivity.this, Profile.class);
        startActivity(intent);
    }

    public void switchWishlist(View view) {
        Intent intent = new Intent(this, WishlistActivity.class);
        startActivity(intent);
    }
}
