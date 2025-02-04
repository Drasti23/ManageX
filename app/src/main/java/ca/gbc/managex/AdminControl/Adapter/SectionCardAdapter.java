package ca.gbc.managex.AdminControl.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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

    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView sectionName;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionName = itemView.findViewById(R.id.tvSectionNameCard);
        }
    }
}
