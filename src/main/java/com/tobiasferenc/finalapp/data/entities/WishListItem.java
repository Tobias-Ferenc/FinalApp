package com.tobiasferenc.finalapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wishlist")
public class WishListItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String itemName;
    public String ownerUsername;
    public String takenBy; // Pokud null, položka není vzatá

    public WishListItem(String itemName, String ownerUsername) {
        this.itemName = itemName;
        this.ownerUsername = ownerUsername;
        this.takenBy = null; // Nová položka není vzatá
    }
    public String getItemName() {
        return itemName;
    }
    public String getOwnerUsername() {  // Přidáme tento getter
        return ownerUsername;
    }
}