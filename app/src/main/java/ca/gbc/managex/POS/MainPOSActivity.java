package ca.gbc.managex.POS;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.AdminControl.Classes.ItemSize;
import ca.gbc.managex.POS.Adapters.ItemGridAdapter;
import ca.gbc.managex.POS.Adapters.OrderBillAdapter;
import ca.gbc.managex.POS.Adapters.SectionAdapter;
import ca.gbc.managex.R;

public class MainPOSActivity extends AppCompatActivity {

    private RecyclerView rvSectionList, rvBill;
    private GridLayout gridLayoutItems;
    private ArrayList<Section> sectionList = new ArrayList<>();
    private ArrayList<OrderItem> orderList = new ArrayList<>();
    private OrderBillAdapter orderBillAdapter;
    private SectionAdapter sectionAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String userId;
    private TextView totalItemEdited,subTotal,tax,totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_posactivity);

        rvSectionList = findViewById(R.id.rvSectionList);
        gridLayoutItems = findViewById(R.id.gridLayoutItems);
        rvBill = findViewById(R.id.rvBill);
        totalAmount = findViewById(R.id.tvTotal);
        totalItemEdited = findViewById(R.id.tvItemEdited);
        subTotal = findViewById(R.id.tvSubTotal);
        tax = findViewById(R.id.tvPOSTax);


        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Menu");
            loadSectionsFromDatabase();
        } else {
            Log.e("MainPOSActivity", "User not logged in!");
        }

        // Setup Section RecyclerView
        rvSectionList.setLayoutManager(new LinearLayoutManager(this));
        sectionAdapter = new SectionAdapter(sectionList, this::loadItemsInGrid);
        rvSectionList.setAdapter(sectionAdapter);

        // Setup Order RecyclerView
        rvBill.setLayoutManager(new LinearLayoutManager(this));
        orderBillAdapter = new OrderBillAdapter(orderList,this);
        rvBill.setAdapter(orderBillAdapter);
    }

    private void loadSectionsFromDatabase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sectionList.clear();

                for (DataSnapshot sectionSnapshot : dataSnapshot.getChildren()) {
                    String sectionName = sectionSnapshot.getKey();
                    ArrayList<Item> items = new ArrayList<>();

                    for (DataSnapshot itemSnapshot : sectionSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null) {
                            items.add(item);
                        }
                    }

                    sectionList.add(new Section(sectionName, items));
                }

                sectionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load menu: " + databaseError.getMessage());
            }
        });
    }

    private void loadItemsInGrid(ArrayList<Item> items) {
        new ItemGridAdapter(this, gridLayoutItems, items, (item, size) -> {
            boolean itemExists = false;

            // Check if item with the same size already exists
            for (OrderItem orderItem : orderList) {
                if (orderItem.getItem().getName().equals(item.getName()) &&
                        orderItem.getSize().getSize().equals(size.getSize())) {
                    orderItem.increaseQuantity();
                    itemExists = true;
                    break;
                }
            }

            // If item doesn't exist, add a new entry
            if (!itemExists) {
                orderList.add(new OrderItem(item, size));
            }

            orderBillAdapter.notifyDataSetChanged();
            calculateBill();
        });
    }

    public void calculateBill(){
        double subTotalAmount = 0;
        int totalEditedItems = 0;
        for(OrderItem orderItem : orderList){
            double itemTotal = orderItem.getSize().getPrice() * orderItem.getQuantity();
            subTotalAmount += itemTotal;
            if(!orderItem.getNote().isEmpty() || orderItem.getCustomized()){
                totalEditedItems++;
            }
        }

        double taxAmount = subTotalAmount * 0.13;
        double totalAmountToPay = subTotalAmount + taxAmount;
        totalItemEdited.setText("Edited Items: " + totalEditedItems);
        subTotal.setText(String.format("$%.2f", subTotalAmount));
        tax.setText(String.format("$%.2f", taxAmount));
        totalAmount.setText(String.format("$%.2f", totalAmountToPay));
    }


}
