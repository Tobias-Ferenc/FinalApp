package com.tobiasferenc.finalapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tobiasferenc.finalapp.R;
import com.tobiasferenc.finalapp.data.dao.UserDao;
import com.tobiasferenc.finalapp.data.database.AppDatabase;
import com.tobiasferenc.finalapp.data.entities.User;

import java.io.File;

public class Profile extends AppCompatActivity {
    EditText usernameInput, passwordInput;
    Button loginButton;
    TextView usernameText;
    ImageView profilePicture;

    AppDatabase db;
    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input); // Přidán vstup pro heslo
        loginButton = findViewById(R.id.login_button);
        usernameText = findViewById(R.id.username_text);
        profilePicture = findViewById(R.id.profile_picture);

        db = AppDatabase.getInstance(this);
        userDao = db.userDao();

        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Zadej uživatelské jméno a heslo!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            User user = userDao.getUser(username);

            runOnUiThread(() -> {
                if (user != null && user.password.equals(password)) {
                    usernameText.setText(username);
                    loadProfilePicture(user.profilePicturePath);

                    // ULOŽENÍ username DO SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", username); // loggedInUsername = jméno z přihlášení
                    editor.apply();

                    Toast.makeText(this, "Přihlášení úspěšné!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Neplatné uživatelské jméno nebo heslo!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }


    private void loadProfilePicture(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            Log.d("ProfilePic", "Path: " + imagePath);

            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profilePicture.setImageBitmap(myBitmap);
            } else {
                Toast.makeText(this, "Obrázek nenalezen na: " + imagePath, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Žádná profilovka nenalezena!", Toast.LENGTH_SHORT).show();
        }
    }
    public void signOut(View view) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("username"); // Odstranění uloženého username
        editor.apply();

        // Přechod na přihlašovací obrazovku
        Intent intent = new Intent(this, Profile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Zabrání návratu tlačítkem zpět
        startActivity(intent);

    }
    public void maindyk(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
