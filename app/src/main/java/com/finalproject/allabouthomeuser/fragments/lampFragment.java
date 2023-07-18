package com.finalproject.allabouthomeuser.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.models.Item;
import com.finalproject.allabouthomeuser.models.Lamp;
import com.finalproject.allabouthomeuser.models.LampAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class lampFragment extends Fragment {
    private RecyclerView lampRecyclerView;
    private LampAdapter lampAdapter;
    private List<Lamp> lampList;
    private FirebaseFirestore db;


    public lampFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lamp, container, false);


        lampRecyclerView = view.findViewById(R.id.lampRecyclerView);
        lampList = new ArrayList<>();
        lampAdapter = new LampAdapter(lampList, getActivity());
        lampRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampRecyclerView.setAdapter(lampAdapter);

        db = FirebaseFirestore.getInstance();

        getAllLamps();




        return view;
    }

    private void getAllLamps() {
        Query allLampsRef = db.collection("alllamps").whereGreaterThan("quantity", 0);
        allLampsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                lampList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    String itemUid = document.getId();
                    int quantity = document.getLong("quantity").intValue();
                    String name = document.getString("name");
                    String description = document.getString("description");
                    int price = document.getLong("price").intValue();
                    String adminName = document.getString("adminName");
                    String adminuid = document.getString("adminUID");
                    int shade = Math.toIntExact(document.getLong("shade"));
                    double watt = document.getLong("wattage").intValue();
                    String imageURL = document.getString("imageURL");
                    ArrayList<String> categories = (ArrayList<String>) document.get("categories");


                    Lamp lamp = new Lamp(categories,itemUid, adminuid, name, description, price, adminName, quantity, imageURL, watt, shade);



                    lampList.add(lamp);
                }
                lampAdapter.notifyDataSetChanged();
            } else {
                // Handle the error
            }
        });
    }


}
