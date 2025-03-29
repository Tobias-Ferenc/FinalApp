package com.tobiasferenc.finalapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.tobiasferenc.finalapp.R;
import com.tobiasferenc.finalapp.data.database.AppDatabase;
import com.tobiasferenc.finalapp.data.dao.WishlistDao;
import com.tobiasferenc.finalapp.data.entities.WishListItem;

import java.util.List;

public class WishlistActivity extends AppCompatActivity {
    private EditText itemNameInput;
    private ListView wishlistListView;
    private WishlistDao wishlistDao;
    private String currentUser = "uzivatelskejmeno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        itemNameInput = findViewById(R.id.itemNameInput);
        wishlistListView = findViewById(R.id.wishlistListView);
        Button addItemButton = findViewById(R.id.addItemButton);

        wishlistDao = AppDatabase.getInstance(this).wishlistDao();

        addItemButton.setOnClickListener(v -> addItemToWishlist());

        wishlistListView.setOnItemClickListener((parent, view, position, id) -> {
            WishListItem item = (WishListItem) parent.getItemAtPosition(position);
            showOptionsDialog(item);
        });

        loadWishlist();
    }

    private void addItemToWishlist() {
        String itemName = itemNameInput.getText().toString().trim();
        if (itemName.isEmpty()) {
            Toast.makeText(this, "Zadejte název položky!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            WishListItem newItem = new WishListItem(itemName, currentUser);
            wishlistDao.insertItem(newItem);

            runOnUiThread(() -> {
                itemNameInput.setText("");
                loadWishlist();
                Toast.makeText(this, "Přidáno do wishlistu!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void loadWishlist() {
        new Thread(() -> {
            List<WishListItem> items = wishlistDao.getUserWishlist(currentUser);

            runOnUiThread(() -> {
                ArrayAdapter<WishListItem> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, items) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = view.findViewById(android.R.id.text1);
                        textView.setText(items.get(position).getItemName()); // Zobrazí název položky
                        return view;
                    }
                };
                wishlistListView.setAdapter(adapter);
            });
        }).start();
    }

    private void showOptionsDialog(WishListItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Možnosti");

        String[] options = {"Upravit", "Smazat", "Detail"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showEditDialog(item);
            } else if (which == 1) {
                deleteWishItem(item.id);
            } else if (which == 2) {
                showWishDetail(item.id);
            }
        });

        builder.show();
    }

    private void showEditDialog(WishListItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upravit přání");

        final EditText input = new EditText(this);
        input.setText(item.itemName);
        builder.setView(input);

        builder.setPositiveButton("Uložit", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                editWishItem(item.id, newName);
            }
        });

        builder.setNegativeButton("Zrušit", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void editWishItem(int itemId, String newName) {
        new Thread(() -> {
            wishlistDao.updateItemName(itemId, newName);
            runOnUiThread(() -> {
                loadWishlist();
                Toast.makeText(this, "Položka upravena!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    public void deleteWishItem(int itemId) {
        new Thread(() -> {
            wishlistDao.deleteItem(itemId);
            runOnUiThread(() -> {
                loadWishlist();
                Toast.makeText(this, "Položka smazána!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    public void showWishDetail(int itemId) {
        new Thread(() -> {
            WishListItem item = wishlistDao.getItemById(itemId);
            runOnUiThread(() -> {
                if (item != null) {
                    String message = "Položka: " + item.itemName + "\nVlastník: " + item.ownerUsername;
                    if (item.takenBy != null) message += "\nVzal si: " + item.takenBy;
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Položka nenalezena!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}