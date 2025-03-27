package ca.gbc.managex.AdminControl.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.AdminControl.Classes.ItemSize;
import ca.gbc.managex.AdminControl.ItemInfoActivity;
import ca.gbc.managex.R;

public class ItemCardAdapter extends RecyclerView.Adapter<ItemCardAdapter.itemViewHolder>{
    private Context context;
    private ArrayList<Item> itemList;
    private ArrayList<ItemSize> itemSizeList;
    private String sectionName;

    public ItemCardAdapter(Context context,ArrayList<Item> itemList,String sectionName){
        this.context = context;
        this.itemList = itemList;
        itemSizeList = new ArrayList<>();
        this.sectionName = sectionName;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_design,parent,false);
        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemName.setText(item.getName());

        // Display Size & Price
        StringBuilder sizePriceInfo = new StringBuilder();
        for (ItemSize sizePrice : item.getItemSizeList()) {
            sizePriceInfo.append(sizePrice.getSize()).append(" - $").append(sizePrice.getPrice()).append("  ");
        }
        holder.sizeAvailable.setText(sizePriceInfo.toString().trim());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ItemInfoActivity.class);
                i.putExtra("itemName",item.getName());
                i.putExtra("sectionName",sectionName);
                context.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class itemViewHolder extends RecyclerView.ViewHolder{

        TextView itemName;
        TextView sizeAvailable;

        public itemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tvItemNameCard);
            sizeAvailable = itemView.findViewById(R.id.tvSizeAvailable);


        }
    }
}
