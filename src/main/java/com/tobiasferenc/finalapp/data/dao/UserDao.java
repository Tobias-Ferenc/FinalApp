package com.tobiasferenc.finalapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tobiasferenc.finalapp.data.entities.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUser(String username);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();
    @Query("UPDATE users SET profilePicturePath = :imagePath WHERE username = :username")
    void updateUserProfilePicture(String username, String imagePath);
}

