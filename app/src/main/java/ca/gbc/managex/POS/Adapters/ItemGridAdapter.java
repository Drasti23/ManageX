package ca.gbc.managex.POS.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;
import java.util.List;

import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.AdminControl.Classes.ItemSize;
import ca.gbc.managex.R;

public class ItemGridAdapter {
    private Context context;
    private GridLayout gridLayout;
    private List<Item> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemSelected(Item item, ItemSize size);
    }

    public ItemGridAdapter(Context context, GridLayout gridLayout, List<Item> itemList, OnItemClickListener listener) {
        this.context = context;
        this.gridLayout = gridLayout;
        this.itemList = itemList;
        this.listener = listener;
        loadGrid();
    }

    private void loadGrid() {
        gridLayout.removeAllViews();
        int columns = 2; // Adjust based on UI
        gridLayout.setColumnCount(columns);

        for (Item item : itemList) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.grid_item_layout, gridLayout, false);
            TextView itemName = itemView.findViewById(R.id.tvItemName);
            Spinner spinnerSize = itemView.findViewById(R.id.spinnerSize);

            itemName.setText(item.getName());

            // Convert ItemSize list to a String list for Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
            adapter.add("-Select-");
            for (ItemSize size : item.getItemSizeList()) {
                adapter.add(size.getSize() + " - $" + size.getPrice());
            }
            spinnerSize.setAdapter(adapter);

            // Handle selection event
            spinnerSize.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) { // Ignore "-Select-" option
                        if (listener != null) {
                            listener.onItemSelected(item, item.getItemSizeList().get(position - 1));
                        }
                        // Reset selection to "-Select-" so user can select the same option again
                        spinnerSize.setSelection(0);
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    // Do nothing
                }
            });


            gridLayout.addView(itemView);
        }
    }
}
