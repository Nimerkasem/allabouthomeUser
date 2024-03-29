package com.finalproject.allabouthomeuser.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.activities.firebase;
import com.finalproject.allabouthomeuser.models.Item;
import com.finalproject.allabouthomeuser.models.ItemAdapter;
import com.finalproject.allabouthomeuser.models.Lamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class categories extends Fragment {
    private List<Item> itemList;
    private RecyclerView itemRecyclerView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private CollectionReference allProductsRef = db.collection("allproducts");
    private CollectionReference allLampsRef = db.collection("alllamps");
    private ItemAdapter itemAdapter;


    private Spinner categorySpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all, container, false);

        itemList = new ArrayList<>();
        itemRecyclerView = view.findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemAdapter = new ItemAdapter(itemList, requireContext(), true, true, true);
        itemRecyclerView.setAdapter(itemAdapter);

        categorySpinner = view.findViewById(R.id.categorySpinner);

        List<String> availableCategories = Arrays.asList("Bathroom", "Bedroom", "Home Office", "Kitchen", "Laundry Room", "Living Room");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, availableCategories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = availableCategories.get(position);
                filterItemsByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        getAllProducts();
        getAllLamps();
        return view;
    }

    private void filterItemsByCategory(String selectedCategory) {
        List<Item> filteredItems = new ArrayList<>();
        for (Item item : itemList) {
            ArrayList<String> categories = item.categories;
            if (categories.contains(selectedCategory)) {
                filteredItems.add(item);
            }
        }
        itemAdapter.setItems(filteredItems);
        itemAdapter.notifyDataSetChanged();
    }

    private void getAllProducts() {
        allProductsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    try{
                    String itemUid = document.getId();
                    int quantity = document.getLong("quantity").intValue();
                    if (quantity > 0) {

                        String name = document.getString("name");
                        String description = document.getString("description");
                        int price = document.getLong("price").intValue();
                        String adminName = document.getString("adminName");
                        String adminuid = document.getString("adminUID");


                        String imageURL = document.getString("imageURL");

                        if (imageURL != null && !imageURL.isEmpty()) {

                            StorageReference imageRef = storage.getReferenceFromUrl(imageURL);
                            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {

                                ArrayList<String> categories = (ArrayList<String>) document.get("categories");


                                Item item = new Item(categories,itemUid, adminuid, name, description, price, adminName, quantity, imageURL);
                                firebase firebaseInstance = new firebase();
                                CompletableFuture<Boolean> adminActiveFuture = firebaseInstance.adminactive(item.adminuid);


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    adminActiveFuture.thenAccept(isActive -> {
                                        if (isActive) {
                                            itemList.add(item);
                                            itemAdapter.notifyDataSetChanged();
                                        }
                                    }).exceptionally(ex -> {
                                        // Handle exception if fetching admin status fails
                                        return null;
                                    });
                                }

                            }).addOnFailureListener(exception -> {
                                // Handle failure to retrieve image
                            });
                        }
                    }
                } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }
        });
    }






    private void getAllLamps() {
        allLampsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    try{
                    String itemUid = document.getId();
                    int quantity = document.getLong("quantity").intValue();
                    if (quantity > 0) {
                        String name = document.getString("name");
                        String description = document.getString("description");
                        int price = document.getLong("price").intValue();
                        String adminName = document.getString("adminName");
                        String adminuid = document.getString("adminUID");
                        int shade = Math.toIntExact(document.getLong("shade"));
                        double watt = document.getDouble("wattage");

                        String imageURL = document.getString("imageURL");
                        ArrayList<String> categories = (ArrayList<String>) document.get("categories");

                        Lamp lamp = new Lamp(categories, itemUid, adminuid, name, description, price, adminName, quantity, imageURL, watt, shade);
                        firebase firebaseInstance = new firebase();

                        CompletableFuture<Boolean> adminActiveFuture =firebaseInstance.adminactive(lamp.adminuid);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            adminActiveFuture.thenAccept(isActive -> {
                                if (isActive) {
                                    itemList.add(lamp);
                                    itemAdapter.notifyDataSetChanged();
                                }
                            }).exceptionally(ex -> {
                                // Handle exception if fetching admin status fails
                                return null;
                            });
                        }

                    }
                } catch (Exception e) {
                        // Handle any exception that occurs during item processing
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}

