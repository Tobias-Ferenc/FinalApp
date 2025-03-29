package com.tobiasferenc.finalapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tobiasferenc.finalapp.data.entities.WishListItem;

import java.util.List;

@Dao
public interface WishlistDao {
    @Insert
    void insertItem(WishListItem item);

    @Query("SELECT * FROM wishlist WHERE ownerUsername = :username")
    List<WishListItem> getUserWishlist(String username);

    @Query("SELECT * FROM wishlist WHERE takenBy IS NOT NULL")
    List<WishListItem> getTakenItems();

    @Query("SELECT * FROM wishlist WHERE id = :itemId")
    WishListItem getItemById(int itemId); // Získání detailu položky

    @Query("UPDATE wishlist SET itemName = :newName WHERE id = :itemId")
    void updateItemName(int itemId, String newName);

    @Query("DELETE FROM wishlist WHERE id = :itemId")
    void deleteItem(int itemId); // Mazání položky
}
