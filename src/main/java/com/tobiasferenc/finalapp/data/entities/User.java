package com.tobiasferenc.finalapp.data.entities;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String password;

    // Konstruktor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
