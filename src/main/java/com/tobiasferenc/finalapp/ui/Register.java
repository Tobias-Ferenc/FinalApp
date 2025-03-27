package com.tobiasferenc.finalapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tobiasferenc.finalapp.R;
import com.tobiasferenc.finalapp.data.dao.UserDao;
import com.tobiasferenc.finalapp.data.database.AppDatabase;
import com.tobiasferenc.finalapp.data.entities.User;

public class Register extends AppCompatActivity {
    EditText usernameS, passwordS;
    ImageView PFPS;

    AppDatabase db;
    UserDao userDao;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = AppDatabase.getInstance(this);
        userDao = db.userDao();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                String imagePath = getRealPathFromURI(Register.this,selectedImageUri);
                saveImagePathToDatabase(imagePath);
            }
        }
    }
    private String getRealPathFromURI(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (uri.getAuthority().equals("com.android.providers.media.documents")) {
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};

                filePath = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }

        return filePath;
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    private void saveImagePathToDatabase(String imagePath) {
        AppDatabase db = AppDatabase.getInstance(this);
        UserDao userDao = db.userDao();

        new Thread(() -> {
            userDao.updateUserProfilePicture("uzivatelskejmeno", imagePath);
        }).start();
    }
    public void RegisterUser(View view) {
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

        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
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
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}