package ca.gbc.managex.Payment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAssignedNumbers;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.gbc.managex.POS.MainPOSActivity;
import ca.gbc.managex.POS.OrderBill;
import ca.gbc.managex.POS.OrderItem;
import ca.gbc.managex.POS.PaymentInfo;
import ca.gbc.managex.Payment.Adapters.PaymentItemAdapter;
import ca.gbc.managex.R;
import ca.gbc.managex.TimeAndDate;
import ca.gbc.managex.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {
    private String currentDate;
    private String orderId;
    private String orderType;
    private OrderBill orderObj = new OrderBill();
    String paymentTimeAndDate;
    PaymentInfo paymentObj = new PaymentInfo();
    ArrayList<OrderItem> orderItems = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ActivityPaymentBinding binding;
    PaymentItemAdapter adapter;
    TextView changeToGive;
    double amountEntered=0.0;
    StringBuilder inputBuffer = new StringBuilder();
    String orderTaker;


    boolean noTax = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupDigitButtonListeners();
        changeToGive = findViewById(R.id.tvChange);
        Intent i = getIntent();
        orderId = i.getStringExtra("orderId");
        orderType = i.getStringExtra("type");
        currentDate = i.getStringExtra("date");
        orderTaker = i.getStringExtra("orderTaker");
        reference = reference.child("Users").child(user.getUid()).child("OrderHistory").child(currentDate).child(orderType);
        binding.rvPayment.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
        adapter = new PaymentItemAdapter(PaymentActivity.this, orderItems);
        binding.rvPayment.setAdapter(adapter);
        getOrderItemsFromDatabase();
        getPaymentBillObjectFromDatabase();
        getOrderBillObjectFromFirebase();




        binding.btnDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                builder.setTitle("Manager Code");

                final EditText input = new EditText(PaymentActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("Submit", (dialog, which) -> {
                    String enteredCode = input.getText().toString();

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference credRef = FirebaseDatabase.getInstance().getReference("Users")
                            .child(uid).child("credentials");

                    credRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String managerCodeFromDB = snapshot.child("managerCode").getValue(String.class);
                            if (managerCodeFromDB == null) managerCodeFromDB = "1000"; // fallback

                            if (enteredCode.equals(managerCodeFromDB)) {
                                AlertDialog.Builder typeDialog = new AlertDialog.Builder(PaymentActivity.this);
                                typeDialog.setTitle("Select Discount Type");

                                String[] discountTypes = {"Percent", "Amount"};
                                typeDialog.setItems(discountTypes, (dialogInterface, whichType) -> {
                                    if (whichType == 0) {
                                        askDiscountValue(true);
                                    } else {
                                        askDiscountValue(false);
                                    }
                                });

                                typeDialog.show();
                            } else {
                                Toast.makeText(PaymentActivity.this, "Incorrect Manager Code", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(PaymentActivity.this, "Error accessing credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });



        binding.btnClearAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountEntered = 0.0;
                binding.tvEnteredAmount.setText("");
                inputBuffer.setLength(0); // clears the buffer
                binding.tvEnteredAmount.setText("");
                amountEntered = 0.0;

            }
        });
        binding.btnPaymentLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PaymentActivity.this, MainPOSActivity.class);
                i.putExtra("employeeName",orderTaker);
                startActivity(i);
                finish();
            }
        });
        binding.btnExactCash.setOnClickListener(v -> {
            amountEntered = paymentObj.getTotalAmountToPay();
            orderObj.setPaymentMethod("Cash");
            paymentObj.setAmountPayed(amountEntered);
            orderObj.setPaymentInfo(paymentObj);
            moveOrderFromOpenToClose();
        });

        binding.btnCard.setOnClickListener(v -> {
            orderObj.setPaymentMethod("Card");
            amountEntered = paymentObj.getTotalAmountToPay();
            paymentObj.setAmountPayed(amountEntered);
            orderObj.setPaymentInfo(paymentObj);
            moveOrderFromOpenToClose();
        });

        binding.btnEnterAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amountEntered <= 0.0) {
                    Toast.makeText(PaymentActivity.this, "Enter an amount first", Toast.LENGTH_SHORT).show();
                    return;
                }

                double entered = amountEntered;
                double due = paymentObj.getTotalAmountToPay();

                Log.d("DEBUG", "Entered: " + entered + " | Due: " + due);

                // Reset UI before logic
                binding.tvChange.setVisibility(GONE);
                binding.tvEnteredAmount.setText("");
                amountEntered = 0.0;

                if (entered >= due) {
                    // Overpaid or exact
                    double change = entered - due;

                    paymentObj.setAmountPayed(entered);
                    paymentObj.setChangeGiven(change);
                    if (change > 0) {
                        showChangeDialog(change);
                    }

                    if(change==0){
                        binding.tvTotalDue.setText("Payment Done!");
                        orderObj.setPaymentMethod("Cash");
                        moveOrderFromOpenToClose();
                    }


                } else {
                    // Underpayment
                    double remaining = due - entered;

                    paymentObj.setAmountPayed(entered);
                    paymentObj.setTotalAmountToPay(remaining);
                    binding.infor.setVisibility(GONE);

                    binding.tvTotalDue.setText(String.format("Remaining: $%.2f", remaining));
                    amountEntered = 0.0;
                    binding.tvEnteredAmount.setText("");
                    inputBuffer.setLength(0); // clears the buffer
                    binding.tvEnteredAmount.setText("");
                    amountEntered = 0.0;

                }
            }
        });

        binding.btnNoTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!noTax){
                    paymentObj.setTotalAmountToPay(paymentObj.getSubTotal());
                    paymentObj.setTax(0);
                    binding.tvTotalDue.setText(""+paymentObj.getTotalAmountToPay());
                    binding.tvTotal.setText(paymentObj.getTotalAmountToPay()+"");
                    binding.tvPOSTax.setText("Tax removed.(0)");
                    noTax = true;
                }
                else{
                    double tax = paymentObj.getSubTotal()*0.13;
                    double newDue = paymentObj.getSubTotal() + tax;
                    paymentObj.setTotalAmountToPay(newDue);
                    paymentObj.setTax(tax);
                    binding.tvTotalDue.setText(""+paymentObj.getTotalAmountToPay());
                    binding.tvTotal.setText(paymentObj.getTotalAmountToPay()+"");
                    binding.tvPOSTax.setText(tax+"");
                    noTax = false;
                }

            }
        });


    }
    private void setupDigitButtonListeners() {
        View.OnClickListener digitClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String digit = ((Button) v).getText().toString();

                // Prevent "." as first character
                if (digit.equals(".") && inputBuffer.length() == 0) {
                    return;
                }

                // Prevent more than one "."
                if (digit.equals(".") && inputBuffer.toString().contains(".")) {
                    return;
                }

                // Append digit or dot
                inputBuffer.append(digit);

                String currentAmountStr = inputBuffer.toString();
                binding.tvEnteredAmount.setText(currentAmountStr);

                try {
                    amountEntered = Double.parseDouble(currentAmountStr);
                    Log.d("DEBUG", "Parsed amountEntered = " + amountEntered);
                } catch (NumberFormatException e) {
                    amountEntered = 0.0;
                    Log.e("DEBUG", "Failed to parse input: " + currentAmountStr);
                }
            }
        };

        binding.btn0.setOnClickListener(digitClickListener);
        binding.btn1.setOnClickListener(digitClickListener);
        binding.btn2.setOnClickListener(digitClickListener);
        binding.btn3.setOnClickListener(digitClickListener);
        binding.btn4.setOnClickListener(digitClickListener);
        binding.btn5.setOnClickListener(digitClickListener);
        binding.btn6.setOnClickListener(digitClickListener);
        binding.btn7.setOnClickListener(digitClickListener);
        binding.btn8.setOnClickListener(digitClickListener);
        binding.btn9.setOnClickListener(digitClickListener);
        binding.btnDot.setOnClickListener(digitClickListener);
    }
    private void showChangeDialog(double changeAmount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change, null);
        builder.setView(dialogView);

        TextView tvChangeAmount = dialogView.findViewById(R.id.tvChangeAmount);
        Button btnClearPayment = dialogView.findViewById(R.id.btnClearPayment);

        tvChangeAmount.setText(String.format("Give change of $%.2f", changeAmount));

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false); // prevent accidental dismiss
        dialog.show();

        btnClearPayment.setOnClickListener(v -> {
            dialog.dismiss();
            binding.tvTotalDue.setText("Payment Done!");
            orderObj.setPaymentMethod("Cash");
            paymentObj.setChangeGiven(changeAmount);
            moveOrderFromOpenToClose();
        });
    }


    public void getPaymentBillObjectFromDatabase() {
        reference.child("open").child(orderId).child("paymentInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double totalAmount = snapshot.child("totalAmountToPay").getValue(Double.class);
                Double tax = snapshot.child("tax").getValue(Double.class);
                Double subTotal = snapshot.child("subTotal").getValue(Double.class);
                paymentObj = snapshot.getValue(PaymentInfo.class);

                // Avoid crash if any value is null
                if (totalAmount == null) totalAmount = 0.0;
                if (tax == null) tax = 0.0;
                if (subTotal == null) subTotal = 0.0;

                // Set values to the object
                paymentObj.setTotalAmountToPay(totalAmount);
                paymentObj.setTax(tax);
                paymentObj.setSubTotal(subTotal);

                // Update UI
                binding.tvTotalDue.setText(String.format("%.2f", totalAmount));
                binding.tvPOSTax.setText(String.format("%.2f", tax));
                binding.tvSubTotal.setText(String.format("%.2f", subTotal));
                binding.tvTotal.setText(String.format("%.2f", totalAmount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }



    public void getOrderItemsFromDatabase() {
        orderItems.clear();
        reference.child("open").child(orderId).child("paymentInfo").child("orderItemList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot child : snapshot.getChildren()){
                        OrderItem item = child.getValue(OrderItem.class);
                        orderItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    paymentObj.setOrderItemList(orderItems);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Firebase Error: " + error.getMessage());
            }
        });
    }
    public void getOrderBillObjectFromFirebase(){
        reference.child("open").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String orderId = snapshot.child("orderId").getValue(String.class);
                    int orderNumberOfTheDay = snapshot.child("orderNumberOfTheDay").getValue(Integer.class);
                    String orderTimeAndDate = snapshot.child("orderTimeAndDate").getValue(String.class);
                    boolean dinInOrder = snapshot.child("dinInOrder").getValue(Boolean.class);
                    int tableNumber = snapshot.child("tableNumber").getValue(Integer.class);
                    String date = snapshot.child("date") .getValue(String.class);
                    orderObj.setOrderId(orderId);
                    orderObj.setOrderNumberOfTheDay(orderNumberOfTheDay);
                    orderObj.setOpen(true);
                    orderObj.setOrderTimeAndDate(orderTimeAndDate);
                    orderObj.setDinInOrder(dinInOrder);
                    orderObj.setPaymentStatus(false);
                    orderObj.setTableNumber(tableNumber);
                    orderObj.setPaymentTimeAndDate("");
                    orderObj.setPaymentInfo(paymentObj);
                    orderObj.setOrderTaker(orderTaker);
                    orderObj.setDate(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentActivity.this,"Database Error: "+error.getMessage(),Toast.LENGTH_SHORT).show();
                System.out.println("Database Error: "+error.getMessage());
            }
        });
    }

    private void askDiscountValue(boolean isPercent) {
        AlertDialog.Builder discountDialog = new AlertDialog.Builder(binding.getRoot().getContext());
        discountDialog.setTitle(isPercent ? "Enter Discount Percentage" : "Enter Discount Amount");

        final EditText discountInput = new EditText(binding.getRoot().getContext());
        discountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        discountDialog.setView(discountInput);

        discountDialog.setPositiveButton("Apply", (dialogInterface, i) -> {
            String inputStr = discountInput.getText().toString();
            if (!inputStr.isEmpty()) {
                double discountValue = Double.parseDouble(inputStr);
                double subTotal = paymentObj.getSubTotal();
                double newSubTotal = 0;
                double discountAmount=0;

                if (isPercent) {
                    discountAmount = (discountValue / 100.0) * subTotal;
                    newSubTotal = subTotal - discountAmount;
                } else {
                    discountAmount = discountValue;
                    newSubTotal = subTotal - discountAmount;
                }

                if (newSubTotal < 0) newSubTotal = 0;

                // Update values
                paymentObj.setSubTotal(newSubTotal);
                double newTotal = newSubTotal + paymentObj.getTax();
                paymentObj.setTotalAmountToPay(newTotal);
                // Update UI
                paymentObj.setDiscount(discountAmount);

                binding.tvDiscount.setText(String.format("%.2f", discountAmount));
                binding.tvSubTotal.setText(String.format("%.2f", paymentObj.getSubTotal()));
                binding.tvTotal.setText(String.format("%.2f", paymentObj.getTotalAmountToPay()));
                binding.tvTotalDue.setText(String.format("%.2f", paymentObj.getTotalAmountToPay()));
            }
        });

        discountDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        discountDialog.show();
    }
    public void moveOrderFromOpenToClose(){
        binding.tvEnteredAmount.setText(amountEntered + ""); // (Optional display)
        binding.tvTotalDue.setText("Payment Done.");


        orderObj.setPaymentStatus(true);
        orderObj.setOpen(false);

        paymentTimeAndDate = TimeAndDate.getCurrentDataAndTime();
        orderObj.setPaymentTimeAndDate(paymentTimeAndDate);

        orderObj.setPaymentInfo(paymentObj);
        reference.child("closed").child(orderId).setValue(orderObj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PaymentActivity.this,"Payment Done!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        reference.child("open").child(orderId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Intent i = new Intent(PaymentActivity.this, MainPOSActivity.class);
                i.putExtra("employeeName",orderTaker);
                startActivity(i);
                finish();
            }
        });



    }
}