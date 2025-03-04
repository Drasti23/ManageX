package ca.gbc.managex.AdminControl;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.gbc.managex.AdminControl.Adapter.SizePriceAdapter;
import ca.gbc.managex.AdminControl.Classes.Item;
import ca.gbc.managex.AdminControl.Classes.ItemSize;
import ca.gbc.managex.R;

public class ItemInfoActivity extends AppCompatActivity {
    String itemName,sectionName;
    Item currentItem;
    ArrayList<ItemSize> itemSizeList = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
    SizePriceAdapter adapter;
    RecyclerView recyclerView;
    Button save,delete;
    TextView tvItemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvItemName = findViewById(R.id.tvItemName);
        recyclerView = findViewById(R.id.rvSizePriceList);
        save = findViewById(R.id.btnSave);
        delete=findViewById(R.id.btnDelete);

        itemName = getIntent().getStringExtra("itemName");
        sectionName= getIntent().getStringExtra("sectionName");
        tvItemName.setText(itemName);
        adapter = new SizePriceAdapter(itemSizeList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getCurrentItemFromDatabase();
    }

    public void getCurrentItemFromDatabase(){
        itemSizeList.clear();
        reference.child("Menu").child(sectionName).child(itemName).child("itemSizeList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                for(DataSnapshot child : snapshot.getChildren()){
                    ItemSize itemSize = child.getValue(ItemSize.class);
                    itemSizeList.add(itemSize);
                }
                adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ItemInfoActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}