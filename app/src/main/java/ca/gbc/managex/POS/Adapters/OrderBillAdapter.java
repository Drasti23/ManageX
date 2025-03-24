package ca.gbc.managex.POS.Adapters;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.POS.MainPOSActivity;
import ca.gbc.managex.POS.OrderItem;
import ca.gbc.managex.R;

public class OrderBillAdapter extends RecyclerView.Adapter<OrderBillAdapter.OrderViewHolder> {

    private ArrayList<OrderItem> orderList;
    private Context context;
    String itemNote = "";

    public OrderBillAdapter(ArrayList<OrderItem> orderList,Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderItem orderItem = orderList.get(position);
        holder.text1.setText(orderItem.getQuantity() + "x " + orderItem.getItem().getName() + " - " + orderItem.getSize().getSize());
        holder.text2.setText("$" + orderItem.getSize().getPrice() * orderItem.getQuantity());
        holder.edit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Item edit note.");
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String enteredNote = input.getText().toString();
                                itemNote = enteredNote;
                                holder.textNote.setText(enteredNote);
                                holder.textNote.setVisibility(VISIBLE);
                                orderItem.setNote(enteredNote);
                                ((MainPOSActivity) context).calculateBill();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
                return false;
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                orderList.remove(position);
                // Notify that the item at the position was removed
                notifyItemRemoved(position);
                notifyDataSetChanged();
                ((MainPOSActivity) context).calculateBill();
                return false;
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.greenDot.setVisibility(VISIBLE);
                orderItem.setCustomized(true);
                ((MainPOSActivity) context).calculateBill();

            }
        });
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                orderItem.increaseQuantity();
                holder.text1.setText(orderItem.getQuantity() + "x " + orderItem.getItem().getName() + " - " + orderItem.getSize().getSize());
                holder.text2.setText("$" + orderItem.getSize().getPrice() * orderItem.getQuantity());
                ((MainPOSActivity) context).calculateBill();

            }
        });
        holder.add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Enter Quantity");

                // Create an EditText for user input
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setHint("Enter quantity");
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredQuantity = input.getText().toString().trim();
                        if (!enteredQuantity.isEmpty()) {
                            int newQuantity = Integer.parseInt(enteredQuantity);

                            if (newQuantity > 0) {
                                orderItem.setQuantity(newQuantity);
                                holder.text1.setText(orderItem.getQuantity() + "x " + orderItem.getItem().getName() + " - " + orderItem.getSize().getSize());
                                holder.text2.setText("$" + orderItem.getSize().getPrice() * orderItem.getQuantity());
                                ((MainPOSActivity) context).calculateBill();
                            } else {
                                Toast.makeText(context, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true; // Indicating that the long-click event is handled
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if quantity is 1, meaning the item is about to be deleted
                if (orderItem.getQuantity() == 1) {
                    // Remove the item from the list
                    orderList.remove(position);
                    // Notify that the item at the position was removed
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                    // If the list is empty after removing the item, notify the adapter
                    if (orderList.isEmpty()) {
                        notifyDataSetChanged(); // To ensure RecyclerView is refreshed
                    }

                    // Recalculate the bill after item removal
                    ((MainPOSActivity) context).calculateBill();
                } else {
                    // If the quantity is greater than 1, just decrease the quantity
                    orderItem.decreaseQuantity();
                    holder.text1.setText(orderItem.getQuantity() + "x " + orderItem.getItem().getName() + " - " + orderItem.getSize().getSize());
                    holder.text2.setText("$" + orderItem.getSize().getPrice() * orderItem.getQuantity());
                    ((MainPOSActivity) context).calculateBill();
                }
            }
        });


        holder.greenDot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.greenDot.setVisibility(INVISIBLE);
                orderItem.setCustomized(false);
                ((MainPOSActivity) context).calculateBill();

                return false;
            }
        });
        holder.textNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.textNote.setVisibility(GONE);
                orderItem.setCustomized(false);
                orderItem.setNote("");
                ((MainPOSActivity) context).calculateBill();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2,textNote;
        ImageView add,delete,edit,greenDot;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.orderItemName);
            text2 = itemView.findViewById(R.id.orderItemPrice);
            textNote = itemView.findViewById(R.id.tvItemNote);
            add = itemView.findViewById(R.id.ivAddOrderItem);
            delete = itemView.findViewById(R.id.ivDeleteOrderItem);
            edit = itemView.findViewById(R.id.ivEditOrderItemNote);
            greenDot = itemView.findViewById(R.id.ivGreenDot);

        }
    }
}
