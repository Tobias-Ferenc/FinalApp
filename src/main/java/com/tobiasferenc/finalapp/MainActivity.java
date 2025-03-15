package com.tobiasferenc.finalapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class MainActivity extends AppCompatActivity {

    EditText usernameS, passwordS;
    ImageView PFPS;

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
    }

    public void Register(View view) {
        usernameS = findViewById(R.id.username);
        passwordS = findViewById(R.id.PASSWORD);

        String username = usernameS.getText().toString();
        String password = passwordS.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            CharSequence text = "obě hodnoty musí být vyplněny";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        } else {
            // Uložení šifrovaných dat pomocí MyPreferencesManager
            preferencesManager.saveUserData(username, password);

            CharSequence text = "Uživatel registrován";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
    }
    public void showSavedData(View view) {
        // Získání instance MyPreferencesManager a načtení dat
        MyPreferencesManager preferencesManager = new MyPreferencesManager();
        preferencesManager.initEncryptedPreferences(this); // Předáme kontext

        // Načteme uživatelské údaje
        String username = preferencesManager.getUsername();
        String password = preferencesManager.getPassword();

        // Vytvoříme Toast, který vypíše hodnoty
        String message = "Username: " + username + "\nPassword: " + password;

        // Zobrazíme Toast s těmito hodnotami
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
