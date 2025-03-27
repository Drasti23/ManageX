package ca.gbc.managex.AdminControl.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.gbc.managex.AdminControl.Classes.ItemSize;
import ca.gbc.managex.R;

public class SizePriceAdapter extends RecyclerView.Adapter<SizePriceAdapter.ViewHolder> {

    private List<ItemSize> sizePriceList;
    private Context context;

    public SizePriceAdapter(List<ItemSize> sizePriceList, Context context) {
        this.sizePriceList = sizePriceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_size_price, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemSize item = sizePriceList.get(position);
        holder.etSize.setText(item.getSize());
        holder.etPrice.setText(String.valueOf(item.getPrice()));

        holder.btnRemove.setOnClickListener(v -> {
            sizePriceList.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return sizePriceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText etSize, etPrice;
        Button btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etSize = itemView.findViewById(R.id.etSize);
            etPrice = itemView.findViewById(R.id.etPrice);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
