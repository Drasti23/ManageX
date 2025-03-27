package ca.gbc.managex.POS;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.MainActivity;
import ca.gbc.managex.POS.Adapters.ItemGridAdapter;
import ca.gbc.managex.POS.Adapters.OrderBillAdapter;
import ca.gbc.managex.POS.Adapters.SectionAdapter;
import ca.gbc.managex.Payment.PaymentActivity;
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
    private TextView totalItemEdited,subTotal,tax,totalAmount,server;
    public static String orderBillId="";
    public static int orderNumberOfDay=1;
    public static String currentDate;
    public static String currentTimeAndDate;

    private Button cancelOrder, takeout,dineIn;
    private static PaymentInfo paymentInfo;
    public String orderType;
    ConstraintLayout constraintLayout;
    ImageView back;

    public interface OrderCountCallBack{
        void onCountRetrieved(int count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_posactivity);

        rvSectionList = findViewById(R.id.rvSectionList);
        gridLayoutItems = findViewById(R.id.gridLayoutItems);
        rvBill = findViewById(R.id.rvBill);
        totalAmount = findViewById(R.id.tvTotal);
        totalItemEdited = findViewById(R.id.tvDiscount);
        subTotal = findViewById(R.id.tvSubTotal);
        tax = findViewById(R.id.tvPOSTax);
        cancelOrder = findViewById(R.id.btnCancelOrder);
        takeout = findViewById(R.id.btnTakeOut);
        dineIn = findViewById(R.id.btnDineIn);
        constraintLayout = findViewById(R.id.biillCL);
        back = findViewById(R.id.backButton);
        server = findViewById(R.id.tvOrderTaker);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainPOSActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        Intent i = getIntent();
        String serverName = i.getStringExtra("employeeName");
        server.setText(serverName);



        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
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
        orderBillAdapter = new OrderBillAdapter(orderList,MainPOSActivity.this);
        rvBill.setAdapter(orderBillAdapter);
        currentDate = getCurrentDate();
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderList.clear();
                calculateBill();
                orderBillAdapter.notifyDataSetChanged();
            }
        });
        takeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateBill();
                currentTimeAndDate = getCurrentDataAndTime();
                OrderBill billObject = new OrderBill(orderBillId,orderNumberOfDay, currentTimeAndDate,false,paymentInfo,currentDate);
                billObject.setOrderTaker(serverName);
                billObject.setDate(currentDate);
                saveOrderBillInDatabase(billObject,"takeout",serverName);

                //saveCurrentOrder("takeout",billObject.getOrderId());

            }
        });
        dineIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateBill();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPOSActivity.this);
                builder.setTitle("Choose table number");
                final EditText input = new EditText(MainPOSActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setHint("Table number");
                input.setSingleLine(true);
                builder.setView(input);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Intent
                        int tableNumber = Integer.parseInt(input.getText().toString());
                        currentTimeAndDate = getCurrentDataAndTime();
                        OrderBill billObject = new OrderBill(orderBillId,orderNumberOfDay, currentTimeAndDate,false,true,tableNumber,paymentInfo,currentDate);
                        billObject.setOrderTaker(serverName);
                        billObject.setDate(currentDate);
                        saveOrderBillInDatabase(billObject,"dineIn",serverName);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setCancelable(true);
                builder.show();

            }
        });

    }
    private void saveOrderBillInDatabase(OrderBill billObject, String type,String orderTaker) {
        getNumberOfOrderForDayFromDatabase(type, new OrderCountCallBack() {
            @Override
            public void onCountRetrieved(int count) {
                String orderId =count+ "-"+type +"-"+ currentDate;

                billObject.setOrderId(orderId);
                billObject.setOrderNumberOfTheDay(count);
                billObject.setOpen(true);
                billObject.setDate(currentDate);

                databaseRef.child("OrderHistory").child(currentDate).child(type).child("open").child(billObject.getOrderId()).setValue(billObject)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainPOSActivity.this, "Order saved to database", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainPOSActivity.this, PaymentActivity.class);
                                intent.putExtra("type",type);
                                intent.putExtra("date",currentDate);
                                intent.putExtra("orderId",billObject.getOrderId());
                                intent.putExtra("orderTaker",orderTaker);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainPOSActivity.this, "There is a problem saving Order", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    private String getCurrentDataAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd MMM yyyy",Locale.getDefault());
        return sdf.format(new Date());
    }

    private void loadSectionsFromDatabase() {
        databaseRef.child("Menu").addValueEventListener(new ValueEventListener() {
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
        if(constraintLayout.getVisibility()==INVISIBLE){
            constraintLayout.setVisibility(VISIBLE);
        }

        double subTotalAmount = 0;
        int totalEditedItems = 0;
        int totalItem=0;
        for(OrderItem orderItem : orderList){
            double itemTotal = orderItem.getSize().getPrice() * orderItem.getQuantity();
            totalItem = totalItem + orderItem.getQuantity();
            subTotalAmount += itemTotal;
            if(!orderItem.getNote().isEmpty() || orderItem.getCustomized()){
                totalEditedItems++;
            }
        }

        double taxAmount = subTotalAmount * 0.13;
        double totalAmountToPay = subTotalAmount + taxAmount;
        totalItemEdited.setText("" + totalEditedItems);
        subTotal.setText(String.format("$%.2f", subTotalAmount));
        tax.setText(String.format("$%.2f", taxAmount));
        totalAmount.setText(String.format("$%.2f", totalAmountToPay));
        paymentInfo = new PaymentInfo(orderList,totalEditedItems,subTotalAmount,taxAmount,totalAmountToPay,totalItem);

    }

    private void getNumberOfOrderForDayFromDatabase(String type, OrderCountCallBack callBack) {
        DatabaseReference openOrdersRef = databaseRef.child("OrderHistory").child(currentDate).child(type).child("open");
        DatabaseReference closedOrdersRef = databaseRef.child("OrderHistory").child(currentDate).child(type).child("closed");

        final int[] count = {0, 0};
        final boolean[] isFetched = {false, false};

        openOrdersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count[0] = (int) snapshot.getChildrenCount(); // Count all orders
                isFetched[0] = true;
                checkAndReturnFinalCount(callBack, count, isFetched);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch open order count: " + error.getMessage());
                isFetched[0] = true;
                checkAndReturnFinalCount(callBack, count, isFetched);
            }
        });

        closedOrdersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count[1] = (int) snapshot.getChildrenCount(); // Count all closed orders
                isFetched[1] = true;
                checkAndReturnFinalCount(callBack, count, isFetched);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch closed order count: " + error.getMessage());
                isFetched[1] = true;
                checkAndReturnFinalCount(callBack, count, isFetched);
            }
        });
    }

    private void checkAndReturnFinalCount(OrderCountCallBack callBack, int[] count, boolean[] isFetched) {
        if (isFetched[0] && isFetched[1]) { // Ensure both counts are fetched
            int finalCount = count[0] + count[1] + 1; // Sum of all orders to generate a new unique order number
            callBack.onCountRetrieved(finalCount);
        }
    }

    /*public void saveCurrentOrder(String type,String id){
        SharedPreferences sharedPreferences = getSharedPreferences("CurrentOrder",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("orderId",id);
        editor.putString("orderType",type);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("CurrentOrder",MODE_PRIVATE);
        orderType = sharedPreferences.getString("orderType","");
        orderBillId = sharedPreferences.getString("orderId","");
    }*/

}
