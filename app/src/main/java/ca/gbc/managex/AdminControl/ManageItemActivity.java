package ca.gbc.managex.AdminControl;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.gbc.managex.AdminControl.Adapter.ItemCardAdapter;
import ca.gbc.managex.AdminControl.Adapter.SizePriceAdapter;
import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.AdminControl.Classes.ItemSize;
import ca.gbc.managex.R;

public class ManageItemActivity extends AppCompatActivity {

    FloatingActionButton fab;
    ArrayList<ItemSize> sizePriceList = new ArrayList<>();
    ArrayList<Item> itemList = new ArrayList<>();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    RecyclerView recyclerView;
    ItemCardAdapter adapter;
    String sectionName, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_item);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i = getIntent();
        sectionName = i.getStringExtra("sectionName");
        userId = auth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.itemRecyclerView);
        adapter = new ItemCardAdapter(ManageItemActivity.this, itemList,sectionName);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ManageItemActivity.this));

        // Fetch Items from Firebase
        getItemListFromDatabase();

        fab = findViewById(R.id.fabAddItem);
        fab.setOnClickListener(view -> showAddItemDialog());
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_item_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText etItemName = dialogView.findViewById(R.id.etItemName);
        RecyclerView rvSizePriceList = dialogView.findViewById(R.id.rvSizePriceList);
        Button btnAddSize = dialogView.findViewById(R.id.btnAddSize);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        SizePriceAdapter adapter = new SizePriceAdapter(sizePriceList, this);
        rvSizePriceList.setLayoutManager(new LinearLayoutManager(this));
        rvSizePriceList.setAdapter(adapter);

        btnAddSize.setOnClickListener(v -> {
            // Preserve existing inputs
            for (int i = 0; i < rvSizePriceList.getChildCount(); i++) {
                View v1 = rvSizePriceList.getChildAt(i);
                if (v1 != null) {
                    EditText etSize = v1.findViewById(R.id.etSize);
                    EditText etPrice = v1.findViewById(R.id.etPrice);

                    String size = etSize.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();

                    if (!size.isEmpty() && !priceStr.isEmpty()) {
                        double price = Double.parseDouble(priceStr);

                        // Update existing entry instead of overwriting list
                        if (i < sizePriceList.size()) {
                            sizePriceList.get(i).setSize(size);
                            sizePriceList.get(i).setPrice(price);
                        }
                    }
                }
            }

            // Add a new empty size field
            sizePriceList.add(new ItemSize("", 0));

            // Notify adapter
            adapter.notifyDataSetChanged();
        });

        btnSave.setOnClickListener(v -> {
            String itemName = etItemName.getText().toString().trim();
            if (itemName.isEmpty()) {
                Toast.makeText(this, "Enter item name", Toast.LENGTH_SHORT).show();
                return;
            }
            if(sizePriceList.size() == 0){
                Toast.makeText(ManageItemActivity.this, "Item should have at least one size", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<ItemSize> finalSizePriceList = new ArrayList<>();
            for (int i = 0; i < sizePriceList.size(); i++) {
                View view = rvSizePriceList.getChildAt(i);
                EditText etSize = view.findViewById(R.id.etSize);
                EditText etPrice = view.findViewById(R.id.etPrice);

                String size = etSize.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();

                if (size.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(this, "Size and price cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price = Double.parseDouble(priceStr);
                finalSizePriceList.add(new ItemSize(size, price));
            }

            Item newItem = new Item(itemName, finalSizePriceList);
            DatabaseReference itemRef = reference.child("Users").child(userId).child("Menu").child(sectionName).child(itemName);

            itemRef.setValue(newItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                    getItemListFromDatabase(); // Fetch new items from Firebase
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to add item!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void getItemListFromDatabase() {
        itemList.clear();
        DatabaseReference itemRef = reference.child("Users").child(userId).child("Menu").child(sectionName);

        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    itemList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageItemActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
