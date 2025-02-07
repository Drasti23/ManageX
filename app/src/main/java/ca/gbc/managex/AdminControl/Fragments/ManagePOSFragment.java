package ca.gbc.managex.AdminControl.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.gbc.managex.AdminControl.Adapter.SectionCardAdapter;
import ca.gbc.managex.AdminControl.Dialogs.AddSectionDialog;
import ca.gbc.managex.R;
public class ManagePOSFragment extends Fragment {
    RecyclerView recyclerView;
    SectionCardAdapter adapter;
    ArrayList<String> sectionsList = new ArrayList<>();;
    FloatingActionButton fab;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("Users").child(user.getUid());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_p_o_s,container,false);
        recyclerView = view.findViewById(R.id.sectionRecyclerView);
        adapter = new SectionCardAdapter(sectionsList,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fab = view.findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSectionDialog.display(getChildFragmentManager());
            }
        });


        return view;
    }

    public void getSectionList() {
        sectionsList.clear();
        reference.child("Menu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sectionsList.clear(); // Clear the list before adding new items

                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String sectionName = child.getValue(String.class);
                        sectionsList.add(sectionName);
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Sections: " + sectionsList.toString(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getContext(), "No sections found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load sections", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        getSectionList();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSectionList();
    }
}