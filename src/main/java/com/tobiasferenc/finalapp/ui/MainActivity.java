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

    EditText usernameS, passwordS;
    ImageView PFPS;

    AppDatabase db;
    UserDao userDao;
    private static final int PICK_IMAGE_REQUEST = 1;

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
/*
    public void Register(View view) {
        usernameS = findViewById(R.id.username);
        passwordS = findViewById(R.id.PASSWORD);

        String username = usernameS.getText().toString();
        String password = passwordS.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vyplň všechna pole!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            userDao.insertUser(new User(username, password));

            runOnUiThread(() ->
                    Toast.makeText(this, "Uživatel " + username + " uložen!", Toast.LENGTH_SHORT).show()
            );
        }).start();
    }

    public void showSavedData(View view) {
        String username = usernameS.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "Zadej username!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            User user = userDao.getUser(username);

            runOnUiThread(() -> {
                if (user != null) {
                    String message = "Username: " + user.username + "\nPassword: " + user.password;
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Uživatel nenalezen!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }*/
public void switchRegister(View v) {
    Intent intent = new Intent(MainActivity.this, Register.class);
    startActivity(intent);
}
    public void switchProfile(View v) {
        Intent intent = new Intent(MainActivity.this, Profile.class);
        startActivity(intent);
    }
}
