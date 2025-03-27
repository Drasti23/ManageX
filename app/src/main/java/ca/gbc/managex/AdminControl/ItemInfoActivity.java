package ca.gbc.managex.AdminControl;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.gbc.managex.AdminControl.Adapter.SizePriceAdapter;
import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.AdminControl.Classes.ItemSize;
import ca.gbc.managex.R;
import ca.gbc.managex.AdminControl.ManageItemActivity;

public class ItemInfoActivity extends AppCompatActivity {
    String itemName,sectionName;
    Item currentItem;
    ArrayList<ItemSize> itemSizeList = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
    SizePriceAdapter adapter;
    RecyclerView recyclerView;
    Button save,delete,addSize;
    EditText tvItemName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ItemInfoActivity.this, ManageItemActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        tvItemName = findViewById(R.id.tvItemName);
        recyclerView = findViewById(R.id.rvSizePriceList);
        save = findViewById(R.id.btnSave);
        delete=findViewById(R.id.btnDelete);
        addSize = findViewById(R.id.btnAddSizeEdit);

        itemName = getIntent().getStringExtra("itemName");
        sectionName= getIntent().getStringExtra("sectionName");
        tvItemName.setText(itemName);
        adapter = new SizePriceAdapter(itemSizeList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getCurrentItemFromDatabase();
        addSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save existing input before adding a new size
                ArrayList<ItemSize> tempList = new ArrayList<>();

                for (int i = 0; i < itemSizeList.size(); i++) {
                    View v = recyclerView.getChildAt(i);
                    if (v != null) {
                        EditText etSize = v.findViewById(R.id.etSize);
                        EditText etPrice = v.findViewById(R.id.etPrice);

                        String size = etSize.getText().toString().trim();
                        String priceStr = etPrice.getText().toString().trim();

                        if (!size.isEmpty() && !priceStr.isEmpty()) {
                            double price = Double.parseDouble(priceStr);
                            tempList.add(new ItemSize(size, price));
                        }
                    }
                }

                // Add new empty item
                tempList.add(new ItemSize("", 0));

                // Update the list and notify adapter
                itemSizeList.clear();
                itemSizeList.addAll(tempList);
                adapter.notifyDataSetChanged();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ItemInfoActivity.this);
                builder.setTitle("Delete Item")
                        .setMessage("The all the data of the item will be deleted.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            deleteItemFromDatabase(itemName);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.cancel();

                            }
                        });
                builder.show();

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvItemName.getText().toString().isEmpty()){
                    Toast.makeText(ItemInfoActivity.this, "Enter item name", Toast.LENGTH_SHORT).show();
                }
                else {
                    ArrayList<ItemSize> finalSizePriceList = new ArrayList<>();
                    for (int i = 0; i < itemSizeList.size(); i++) {
                        View v = recyclerView.getChildAt(i);
                        EditText etSize = v.findViewById(R.id.etSize);
                        EditText etPrice = v.findViewById(R.id.etPrice);
                        String size = etSize.getText().toString().trim();
                        String price = etPrice.getText().toString().trim();

                        if (size.isEmpty() || price.isEmpty()) {
                            Toast.makeText(ItemInfoActivity.this, "Size and price cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        double priceD = Double.parseDouble(price);
                        finalSizePriceList.add(new ItemSize(size, priceD));
                    }
                    String updatedItemName = tvItemName.getText().toString().trim();
                    if (finalSizePriceList.size() > 0) {
                        Item newItem = new Item(updatedItemName, finalSizePriceList);
                        updateItemToDatabase(newItem);
                    } else {
                        Toast.makeText(ItemInfoActivity.this, "Item should have at least one size", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void deleteItemFromDatabase(String itemNameToDelete){
        reference.child("Menu").child(sectionName).child(itemNameToDelete).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ItemInfoActivity.this,"Item delete",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCurrentItemFromDatabase(){
        itemSizeList.clear();
        reference.child("Menu").child(sectionName).child(itemName).child("itemSizeList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                for(DataSnapshot child : snapshot.getChildren()){
                    ItemSize itemSize = child.getValue(ItemSize.class);
                    itemSizeList.add(itemSize);
                }
                adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ItemInfoActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateItemToDatabase(Item item) {
        DatabaseReference itemRef = reference.child("Menu").child(sectionName).child(item.getName());

        if (!itemName.equals(item.getName())) {
            // Item name has changed → Delete old item and create a new one
            reference.child("Menu").child(sectionName).child(itemName).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Add new item to the database
                    itemRef.setValue(item).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            Toast.makeText(ItemInfoActivity.this, "Item updated.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ItemInfoActivity.this, "Failed to save new item.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ItemInfoActivity.this, "Failed to delete old item.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Item name is the same → Just update it
            itemRef.setValue(item).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ItemInfoActivity.this, "Item updated.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ItemInfoActivity.this, "There is an issue.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}