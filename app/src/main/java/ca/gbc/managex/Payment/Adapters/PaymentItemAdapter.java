package ca.gbc.managex.Payment.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.gbc.managex.POS.Adapters.OrderBillAdapter;
import ca.gbc.managex.POS.OrderItem;
import ca.gbc.managex.POS.PaymentInfo;
import ca.gbc.managex.R;

public class PaymentItemAdapter extends RecyclerView.Adapter<PaymentItemAdapter.CardHolder>{
    private Context context;
    private ArrayList<OrderItem> orderList;

    public PaymentItemAdapter(Context context, ArrayList<OrderItem> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_item_card, parent, false);
        return new PaymentItemAdapter.CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        OrderItem orderItem = orderList.get(position);
        holder.itemName.setText(orderItem.getQuantity() + "x " + orderItem.getItem().getName() + " - " + orderItem.getSize().getSize());
        holder.itemPrice.setText("$" + orderItem.getSize().getPrice() * orderItem.getQuantity());

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class CardHolder extends RecyclerView.ViewHolder{
        TextView itemName,itemPrice;

        public CardHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tvPaymentItemName);
            itemPrice = itemView.findViewById(R.id.tvPaymentItemPrice);


        }
    }
}
