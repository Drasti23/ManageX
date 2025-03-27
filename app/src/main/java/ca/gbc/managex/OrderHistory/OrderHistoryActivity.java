package ca.gbc.managex.OrderHistory;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import ca.gbc.managex.AdminControl.AdminControlActivity;
import ca.gbc.managex.AdminControl.ItemInfoActivity;
import ca.gbc.managex.AdminControl.ManageItemActivity;
import ca.gbc.managex.MainActivity;
import ca.gbc.managex.OrderHistory.Adapters.OrdersAdapter;
import ca.gbc.managex.POS.OrderBill;
import ca.gbc.managex.POS.OrderItem;
import ca.gbc.managex.Payment.Adapters.PaymentItemAdapter;
import ca.gbc.managex.Payment.PaymentActivity;
import ca.gbc.managex.R;
import ca.gbc.managex.databinding.ActivityOrderHistoryBinding;

public class OrderHistoryActivity extends AppCompatActivity {

    ActivityOrderHistoryBinding binding;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ArrayList<OrderBill> openOrdersList = new ArrayList<>();
    ArrayList<OrderBill> closeOrdersList = new ArrayList<>();
    ArrayList<OrderItem> orderItems = new ArrayList<>();

    PaymentItemAdapter itemAdapter;
    OrdersAdapter openOrderAdapter;
    OrdersAdapter closedOrderAdapter;
    private OrderBill selectedOpenOrder = null;

    String selectedTab="open";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reference = reference.child("Users").child(user.getUid()).child("OrderHistory");

        ImageView back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize RecyclerView
        binding.rvOrders.setHasFixedSize(true);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvItems.setLayoutManager(new LinearLayoutManager(this));

            binding.bottomNavigationView.setSelectedItemId(R.id.openOrder);
            binding.bottomNavigationView.setSelectedItemId(R.id.closeOrder);

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.openOrder) {
                    showOpenOrders();
                    selectedTab="open";
                    binding.btnPayNow.setVisibility(VISIBLE);
                    return true;
                } else if(item.getItemId() == R.id.closeOrder) {
                    showClosedOrders();
                    selectedTab="closed";
                    binding.btnPayNow.setVisibility(INVISIBLE);
                    return true;
                }
                else{
                    return false;
                }}
        });
        getOrdersFromDatabase();
        showOpenOrders(); // by default

        binding.searchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            filterOrders(charSequence.toString(),selectedTab);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOpenOrder != null) {
                    Intent i = new Intent(OrderHistoryActivity.this, PaymentActivity.class);
                    i.putExtra("type", selectedOpenOrder.isDinInOrder() ? "dineIn" : "takeout");
                    i.putExtra("date", selectedOpenOrder.getDate());
                    i.putExtra("orderId", selectedOpenOrder.getOrderId());
                    i.putExtra("orderTaker", selectedOpenOrder.getOrderTaker());
                    startActivity(i);
                    finish(); // Optional: finish this activity if not needed in back stack
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Please select a open order to proceed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void getOrdersFromDatabase() {
        openOrdersList.clear();
        closeOrdersList.clear();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (String type : new String[]{"dineIn", "takeout"}) {
                        DataSnapshot openSnapshot = dateSnapshot.child(type).child("open");
                        if (openSnapshot.exists()) {
                            for (DataSnapshot orderIdSnapshot : openSnapshot.getChildren()) {
                                OrderBill order = orderIdSnapshot.getValue(OrderBill.class);
                                if (order != null) {
                                    openOrdersList.add(order);
                                }
                            }
                        }
                        DataSnapshot closedSnapshot = dateSnapshot.child(type).child("closed");
                        if (closedSnapshot.exists()) {
                            for (DataSnapshot orderIdSnapshot : closedSnapshot.getChildren()) {
                                OrderBill order = orderIdSnapshot.getValue(OrderBill.class);
                                if (order != null) {
                                    closeOrdersList.add(order);
                                }
                            }
                        }
                    }
                }


                sortOrdersByDateDescending(closeOrdersList);
                sortOrdersByDateDescending(openOrdersList);
                Collections.reverse(openOrdersList);
                Collections.reverse(closeOrdersList);
                showOpenOrders();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this,"Database : " + error.getMessage(),Toast.LENGTH_SHORT).show();
                System.out.println("Database : " + error.getMessage());
            }
        });
    }

    private void showOpenOrders() {
        openOrderAdapter = new OrdersAdapter(openOrdersList, this::showOrderDetails);
        binding.rvOrders.setAdapter(openOrderAdapter);
    }

    private void showClosedOrders() {
        selectedOpenOrder = null;

        closedOrderAdapter = new OrdersAdapter(closeOrdersList, this::showOrderDetails);
        binding.rvOrders.setAdapter(closedOrderAdapter);
    }
    private void showOrderDetails(OrderBill order) {
        orderItems = order.getPaymentInfo().getOrderItemList();
        itemAdapter = new PaymentItemAdapter(OrderHistoryActivity.this, orderItems);
        binding.rvItems.setAdapter(itemAdapter);

        binding.tvItemMOH.setText(String.valueOf(order.getPaymentInfo().getOrderItemList().size()));
        binding.tvSubtotalOH.setText(String.format("%.2f", order.getPaymentInfo().getSubTotal()));
        binding.tvDiscountOH.setText(String.format("%.2f", order.getPaymentInfo().getDiscount()));
        binding.tvTaxOH.setText(String.format("%.2f", order.getPaymentInfo().getTax()));
        binding.tvTotalOH.setText(String.format("%.2f", order.getPaymentInfo().getTotalAmountToPay()));
        binding.orderTakerOH.setText(order.getOrderTaker());
        binding.textView21.setText(order.getPaymentMethod());
        binding.textView23.setText(String.format("%.2f", order.getPaymentInfo().getAmountPayed()));
        binding.textView25.setText(String.format("%.2f", order.getPaymentInfo().getChangeGiven()));

        if ("open".equals(selectedTab)) {
            selectedOpenOrder = order;
            binding.btnPayNow.setVisibility(VISIBLE);
        }
    }



    private void sortOrdersByDateDescending(ArrayList<OrderBill> orderList) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd:MM:yyyy", Locale.getDefault());

        Collections.sort(orderList, (o1, o2) -> {
            try {
                Date dateTime1 = sdf.parse(o1.getOrderTimeAndDate());
                Date dateTime2 = sdf.parse(o2.getOrderTimeAndDate());
                return dateTime2.compareTo(dateTime1); // Newest first
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    private void filterOrders(String query, String type) {
        ArrayList<OrderBill> sourceList = type.equals("open") ? openOrdersList : closeOrdersList;
        ArrayList<OrderBill> filteredList = new ArrayList<>();

        for (OrderBill order : sourceList) {
            String orderId = order.getOrderId() != null ? order.getOrderId() : "";
            String method = order.getPaymentMethod() != null ? order.getPaymentMethod() : "";
            String subTotal = String.valueOf(order.getPaymentInfo() != null ? order.getPaymentInfo().getSubTotal() : "");
            String timeAndDate = order.getOrderTimeAndDate() != null ? order.getOrderTimeAndDate() : "";

            if (orderId.toLowerCase().contains(query.toLowerCase()) ||
                    method.toLowerCase().contains(query.toLowerCase()) ||
                    subTotal.toLowerCase().contains(query.toLowerCase()) ||
                    timeAndDate.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(order);
            }
        }

        if (type.equals("open") && openOrderAdapter != null) {
            openOrderAdapter.updateList(filteredList);
        } else if (type.equals("closed") && closedOrderAdapter != null) {
            closedOrderAdapter.updateList(filteredList);
        }
    }


}
