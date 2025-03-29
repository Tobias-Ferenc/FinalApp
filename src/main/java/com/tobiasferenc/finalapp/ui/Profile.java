package com.tobiasferenc.finalapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
    EditText usernameInput;
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
        loginButton = findViewById(R.id.login_button);
        usernameText = findViewById(R.id.username_text);
        profilePicture = findViewById(R.id.profile_picture);

        db = AppDatabase.getInstance(this);
        userDao = db.userDao();

        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String username = usernameInput.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Zadej uživatelské jméno!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            User user = userDao.getUserByUsername(username);

            runOnUiThread(() -> {
                if (user != null) {
                    usernameText.setText(user.username);
                    loadProfilePicture(user.profilePicturePath);
                } else {
                    Toast.makeText(this, "Uživatel nenalezen!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void loadProfilePicture(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            Log.d("ProfilePic", "Path: " + imagePath); // Přidej logování!

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

}