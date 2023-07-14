package com.finalproject.allabouthomeuser.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.models.Item;
import com.finalproject.allabouthomeuser.models.ItemAdapter;
import com.finalproject.allabouthomeuser.models.Lamp;
import com.finalproject.allabouthomeuser.models.LampAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class all extends Fragment {
    private List<Item> itemList;
    private RecyclerView itemRecyclerView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private CollectionReference allProductsRef = db.collection("allproducts");
    private CollectionReference allLampsRef = db.collection("alllamps");
    private ItemAdapter itemAdapter;


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.all, container, false);

            itemList = new ArrayList<>();

            itemRecyclerView = view.findViewById(R.id.itemRecyclerView);
            itemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            itemAdapter = new ItemAdapter(getActivity(), itemList);
            itemRecyclerView.setAdapter(itemAdapter);
        getAllProducts();
        getAllLamps();
            return view;
        }
    private void getAllProducts() {
        allProductsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String itemUid = document.getId();
                    int quantity = document.getLong("quantity").intValue();
                    if (quantity > 0) {

                        // Retrieve the data for each product
                        String name = document.getString("name");
                        String description = document.getString("description");
                        int price = document.getLong("price").intValue();
                        String adminName = document.getString("adminName");
                        String adminuid = document.getString("adminUID");


                        String imageURL = document.getString("imageURL");

                        if (imageURL != null && !imageURL.isEmpty()) {

                            StorageReference imageRef = storage.getReferenceFromUrl(imageURL);
                            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {


                                Item item = new Item(itemUid, adminuid, name, description, price, adminName, quantity, imageURL); // Pass imageURL, not bmp


                                itemList.add(item);


                                itemAdapter.notifyDataSetChanged();
                            }).addOnFailureListener(exception -> {

                            });
                        } else {

                        }
                    }
                }
            } else {

            }
        });
    }

    // Function to get all lamps
    private void getAllLamps() {
        allLampsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String itemUid = document.getId();
                    int quantity = document.getLong("quantity").intValue();
                    if (quantity > 0) {
                        // Retrieve the data for each lamp
                        String name = document.getString("name");
                        String description = document.getString("description");
                        int price = document.getLong("price").intValue();
                        String adminName = document.getString("adminName");
                        String adminuid = document.getString("adminUID");
                        int shade = Math.toIntExact(document.getLong("shade"));
                        double watt = document.getLong("wattage").intValue();


                        String imageURL = document.getString("imageURL");


                        Item item = new Lamp(itemUid, adminuid, name, description, price, adminName, quantity, imageURL, watt, shade);


                        itemList.add(item);


                        itemAdapter.notifyDataSetChanged();
                    }
                }
            } else {

            }
        });
    }


}

