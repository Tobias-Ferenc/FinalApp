package com.tobiasferenc.finalapp.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {
    private EditText itemNameInput;
    private ListView wishlistListView;
    private WishlistDao wishlistDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        itemNameInput = findViewById(R.id.itemNameInput);
        wishlistListView = findViewById(R.id.wishlistListView);
        Button addItemButton = findViewById(R.id.addItemButton);
        Button backToMainButton = findViewById(R.id.backToMainButton);

        wishlistListView.setOnItemClickListener((parent, view, position, id) -> {
            WishListItem item = (WishListItem) parent.getItemAtPosition(position);
            showOptionsDialog(item); // Otevření dialogu s možnostmi
        });

        wishlistDao = AppDatabase.getInstance(this).wishlistDao();

        addItemButton.setOnClickListener(v -> addItemToWishlist());
        backToMainButton.setOnClickListener(v -> maindyk());

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUser = prefs.getString("username", null); // Vrátí null místo prázdného stringu
        if (currentUser == null) {
            Toast.makeText(this, "Chyba: uživatel není přihlášen!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Přihlášen jako: " + currentUser, Toast.LENGTH_LONG).show();
        }


        loadWishlist();
    }

    private String currentUser;
    private void addItemToWishlist() {
        String itemName = itemNameInput.getText().toString().trim();
        if (itemName.isEmpty()) {
            Toast.makeText(this, "Zadejte název položky!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Získání aktuálního data + třeba měsíc jako výchozí platnost
        LocalDate dueDate = LocalDate.now().plusMonths(1);
        String formattedDueDate = dueDate.toString(); // Uložení jako String (YYYY-MM-DD)

        new Thread(() -> {
            WishListItem newItem = new WishListItem(itemName, currentUser, formattedDueDate);
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
            List<WishListItem> items = wishlistDao.getAllWishlistItems();

            runOnUiThread(() -> {
                ArrayAdapter<WishListItem> adapter = new ArrayAdapter<WishListItem>(this,
                        R.layout.wishlist_item, R.id.itemName, items) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wishlist_item, parent, false);
                        }

                        TextView itemName = convertView.findViewById(R.id.itemName);
                        TextView itemOwner = convertView.findViewById(R.id.itemOwner);
                        Button takeButton = convertView.findViewById(R.id.takeButton);
                        Button editDueDateButton = convertView.findViewById(R.id.editDueDateButton);  // New button to edit due date

                        WishListItem item = getItem(position);
                        if (item != null) {
                            itemName.setText(item.getItemName());
                            itemOwner.setText("Přidal: " + item.getOwnerUsername());

                            // Set due date (or placeholder text if null)
                            TextView dueDateText = convertView.findViewById(R.id.dueDateText);
                            if (item.getDueDate() != null) {
                                dueDateText.setText("Do: " + item.getDueDate());
                            } else {
                                dueDateText.setText("Bez termínu");
                            }

                            // If the item is taken, disable the take button
                            if (item.takenBy != null) {
                                takeButton.setEnabled(!item.takenBy.equals(currentUser)); // Disable if already taken
                                takeButton.setText("VZATO: " + item.takenBy);
                                if (item.takenBy.equals(currentUser)) {
                                    takeButton.setText("VRÁTIT");
                                }
                            } else {
                                takeButton.setEnabled(true);
                                takeButton.setText("VZÍT");
                            }

                            // Click listener for the take button
                            takeButton.setOnClickListener(v -> {
                                if (item.takenBy == null) {
                                    takeItem(item.id); // Uživatel si vezme položku
                                } else if (item.takenBy.equals(currentUser)) {
                                    returnItem(item.id); // Uživatel vrátí položku
                                }
                            });

                            // Click listener for the due date edit button
                            editDueDateButton.setOnClickListener(v -> showDatePicker(item.id));  // Opens the date picker to set the due date
                        }

                        return convertView;
                    }
                };
                wishlistListView.setAdapter(adapter);
            });
        }).start();
    }


    private void showDatePicker(int itemId) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    updateDueDate(itemId, selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDueDate(int itemId, String newDate) {
        new Thread(() -> {
            wishlistDao.updateDueDate(itemId, newDate);
            runOnUiThread(() -> {
                Toast.makeText(this, "Datum změněno!", Toast.LENGTH_SHORT).show();
                loadWishlist();
            });
        }).start();
    }
    // Tato metoda zajišťuje, že položku si může vzít jen přihlášený uživatel a ne vlastní
    private void takeItem(int itemId) {
        new Thread(() -> {
            // Zabránit uživatelům vzít vlastní položku
            if (currentUser != null && !currentUser.isEmpty()) {
                // Zajistíme, že uživatel si nemůže vzít svou vlastní položku
                WishListItem item = wishlistDao.getItemById(itemId);
                if (item != null && !item.ownerUsername.equals(currentUser)) {
                    wishlistDao.takeItem(itemId, currentUser); // Nastaví, že položka byla vzata
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Položka vzata!", Toast.LENGTH_SHORT).show();
                        loadWishlist(); // Znovu načteme seznam
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Nemůžeš si vzít svou vlastní položku.", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    // Tato metoda umožňuje uživatelům vrátit položku
    private void returnItem(int itemId) {
        new Thread(() -> {
            if (currentUser != null && !currentUser.isEmpty()) {
                wishlistDao.returnItem(itemId, currentUser); // Vrátí položku
                runOnUiThread(() -> {
                    Toast.makeText(this, "Položka vrácena!", Toast.LENGTH_SHORT).show();
                    loadWishlist(); // Znovu načteme seznam
                });
            }
        }).start();
    }

    private void showOptionsDialog(WishListItem item) {
        Log.e("WishlistActivity", "showOptionsDialog called");

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


    public void maindyk() {
        Intent intent = new Intent(WishlistActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Zavře tuto aktivitu
    }


}