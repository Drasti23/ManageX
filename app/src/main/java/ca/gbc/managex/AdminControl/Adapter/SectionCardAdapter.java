package ca.gbc.managex.AdminControl.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.gbc.managex.AdminControl.ManageItemActivity;
import ca.gbc.managex.R;

public class SectionCardAdapter extends RecyclerView.Adapter<SectionCardAdapter.ItemViewHolder>{
    private ArrayList<String> sectionList;
    private Context context;

    public SectionCardAdapter(ArrayList<String> sectionList, Context context) {
        this.sectionList = sectionList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_card_desing,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
    String name = sectionList.get(position);
    holder.sectionName.setText(name);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ManageItemActivity.class);
            intent.putExtra("sectionName",name);
            context.startActivity(intent);
        }
    });
    holder.edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

        }
    });
    holder.delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    });

    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView sectionName;
        ImageView delete,edit;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionName = itemView.findViewById(R.id.tvSectionNameCard);
            delete = itemView.findViewById(R.id.sectionDelete);
            edit = itemView.findViewById(R.id.sectionNameEdit);
        }
    }
}
