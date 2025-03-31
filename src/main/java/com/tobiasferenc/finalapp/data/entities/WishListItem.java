package com.tobiasferenc.finalapp.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "wishlist")
public class WishListItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String itemName;
    public String ownerUsername;
    public String takenBy; // Pokud null, položka není vzatá
    public String dueDate; // Nové pole pro datum

    // Hlavní konstruktor se všemi parametry
    public WishListItem(String itemName, String ownerUsername, String takenBy, String dueDate) {
        this.itemName = itemName;
        this.ownerUsername = ownerUsername;
        this.takenBy = takenBy;
        this.dueDate = dueDate;
    }

    // Přetížený konstruktor pro nově vytvořenou položku (takenBy bude null)
    @Ignore
    public WishListItem(String itemName, String ownerUsername, String dueDate) {
        this(itemName, ownerUsername, null, dueDate);
    }

    public String getItemName() {
        return itemName;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getTakenBy() {
        return takenBy;
    }

    public String getDueDate() {
        return dueDate;
    }
}
