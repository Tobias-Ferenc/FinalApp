package com.tobiasferenc.finalapp.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class MyPreferencesManager {

    private SharedPreferences sharedPreferences;

    // Inicializace EncryptedSharedPreferences
    public void initEncryptedPreferences(Context context) {
        try {
            // Vytvoření MasterKey pro šifrování
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            // Inicializace EncryptedSharedPreferences bez potřeby MODu
            sharedPreferences = EncryptedSharedPreferences.create(
                    "UserData",                // Název souboru pro šifrované preference
                    masterKeyAlias,            // Master key alias pro šifrování
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,  // Schéma pro šifrování klíčů
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM); // Schéma pro šifrování hodnot// Kontext


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Uložení uživatelských dat do šifrovaných preferencí
    public void saveUserData(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply(); // nebo editor.commit();
    }

    // Načítání uživatelských dat z šifrovaných preferencí
    public String getUsername() {
        return sharedPreferences.getString("username", "defaultUsername");
    }

    public String getPassword() {
        return sharedPreferences.getString("password", "defaultPassword");
    }
}