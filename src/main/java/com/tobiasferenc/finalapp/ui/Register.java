package com.tobiasferenc.finalapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tobiasferenc.finalapp.R;
import com.tobiasferenc.finalapp.data.dao.UserDao;
import com.tobiasferenc.finalapp.data.database.AppDatabase;
import com.tobiasferenc.finalapp.data.entities.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Register extends AppCompatActivity {
    EditText usernameS, passwordS;
    ImageView PFPS;

    AppDatabase db;
    UserDao userDao;

    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
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
        requestStoragePermission();
        loadUserProfile();
        PFPS = findViewById(R.id.PFP);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Zobraz obrázek hned
                PFPS.setImageURI(selectedImageUri);

                // Získej skutečnou cestu k obrázku
                String imagePath = getRealPathFromURI(Register.this, selectedImageUri);

                // Ulož cestu do databáze
                saveImagePathToDatabase(imagePath);
            }
        }
    }


    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires new permission to read images
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission if it's not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
            } else {
                openGallery(); // If permission is already granted, open the gallery
            }
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // For Android 10 (API 29) and above, scoped storage is used, so no need for WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission if it's not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                openGallery(); // If permission is already granted, open the gallery
            }
        } else {
            // For Android 9 and below, you need both read and write permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                // Request the permissions if not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                openGallery(); // If permission is already granted, open the gallery
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                openGallery();
            } else {
                // Permission was denied
                Toast.makeText(this, "Oprávnění zamítnuto! Bez něj nemohu načíst obrázky.", Toast.LENGTH_SHORT).show();
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

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "profile_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath(); // Tohle uložit do databáze
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImagePathToDatabase(String imagePath) {
        usernameS = findViewById(R.id.username);
        passwordS = findViewById(R.id.PASSWORD);

        //String username = usernameS.getText().toString();
        //String password = passwordS.getText().toString();
        // Získání instance databáze a DAO
        AppDatabase db = AppDatabase.getInstance(this);
        UserDao userDao = db.userDao();

        // Zkontroluj, že cesta k obrázku není null nebo prázdná
        if (imagePath != null && !imagePath.isEmpty()) {
            // Ulož cestu do databáze ve vlákně na pozadí
            new Thread(() -> {
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String username = prefs.getString("username", null); // Načte username z přihlášení
                if (username == null) {
                    Log.e("SaveImagePath", "Username není uložen v SharedPreferences!");
                    return;
                }
                userDao.updateUserProfilePicture(username, imagePath);
            }).start();
        } else {
            Log.e("SaveImagePath", "Cesta k obrázku je null nebo prázdná!");
        }
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

            // ✅ Uložení username do SharedPreferences
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username);
            editor.apply();

            runOnUiThread(() ->
                    Toast.makeText(this, "Uživatel " + username + " uložen!", Toast.LENGTH_SHORT).show()
            );

            // ✅ Přechod na hlavní stránku
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish(); // Ukončí registrační aktivitu
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
    }

    public void openGallery(View view) { openGallery(); }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void loadUserProfile() {
        new Thread(() -> {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String username = prefs.getString("username", null);
            if (username != null) {
                User user = userDao.getUser(username);
                if (user != null && user.profilePicturePath != null) {
                    runOnUiThread(() -> PFPS.setImageURI(Uri.parse(user.profilePicturePath)));
                }
            }

        }).start();
    }
}