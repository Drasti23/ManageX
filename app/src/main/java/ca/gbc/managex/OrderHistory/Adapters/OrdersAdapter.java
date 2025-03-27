package ca.gbc.managex.OrderHistory.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.gbc.managex.AdminControl.Classes.Employee;
import ca.gbc.managex.POS.OrderBill;
import ca.gbc.managex.R;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnOrderClickListener {
        void onOrderClick(OrderBill order);
    }

    private ArrayList<OrderBill> ordersList;
    private OnOrderClickListener listener;

    public OrdersAdapter(ArrayList<OrderBill> openOrdersList, OnOrderClickListener listener) {
        this.ordersList = openOrdersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_card_view, parent, false);
        return new OrdersViewHolder(view);
    }
    public void updateList(ArrayList<OrderBill> filteredList) {
        this.ordersList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        OrderBill order = ordersList.get(position);
        holder.tvTime.setText("Time: " + order.getOrderTimeAndDate());
        holder.tvId.setText("ID: " + order.getOrderId());
        holder.tvAmount.setText("Amount: $" + order.getPaymentInfo().getTotalAmountToPay());

        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.blue_secondary));
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.tvId.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.tvTime.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
            holder.tvId.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
            holder.tvTime.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }


    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvId, tvAmount;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTimeOH);
            tvId = itemView.findViewById(R.id.tvIDOH);
            tvAmount = itemView.findViewById(R.id.tvAmountOH);
        }
    }
}
