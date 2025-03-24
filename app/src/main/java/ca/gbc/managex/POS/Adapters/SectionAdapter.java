package ca.gbc.managex.POS.Adapters;

import static ca.gbc.managex.R.drawable.box_background;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.POS.Section;
import ca.gbc.managex.R;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    private ArrayList<Section> sectionList;
    private OnSectionClickListener listener;

    public interface OnSectionClickListener {
        void onSectionClick(ArrayList<Item> items);
    }

    public SectionAdapter(ArrayList<Section> sectionList, OnSectionClickListener listener) {
        this.sectionList = sectionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        Section section = sectionList.get(position);
        holder.textView.setText(section.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSectionClick(section.getItems());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

