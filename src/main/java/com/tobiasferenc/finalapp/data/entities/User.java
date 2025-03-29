package com.tobiasferenc.finalapp.data.entities;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String password;
    public String profilePicturePath; // Přidáme cestu k obrázku

    @Ignore
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.profilePicturePath = null;
    }

    // Konstruktor pro registraci s obrázkem
    public User(String username, String password, String profilePicturePath) {
        this.username = username;
        this.password = password;
        this.profilePicturePath = profilePicturePath;
    }
}