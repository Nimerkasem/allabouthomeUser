package com.finalproject.allabouthomeuser.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.models.Item;
import com.finalproject.allabouthomeuser.models.ItemAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class homeFragment extends Fragment implements View.OnClickListener {
    private Button addToCart;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private CollectionReference allProductsRef = db.collection("allproducts");
    private CollectionReference allLampsRef = db.collection("alllamps");
    private List<Item> itemList; // List to store the items
    private RecyclerView itemRecyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private ItemAdapter itemAdapter;
    private Button btnLightCalc ;
    TextView itemName, itemImage, itemDescription, itemPrice, itemAdmin, itemQuantity, itemWatt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        itemList = new ArrayList<>(); // Initialize the list


        itemRecyclerView = view.findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemAdapter = new ItemAdapter(getActivity(), itemList); // Update this line
        itemRecyclerView.setAdapter(itemAdapter);

        btnLightCalc = view.findViewById(R.id.btnLightCalc);

        btnLightCalc.setOnClickListener(this);

        getAllProducts();
        getAllLamps();

        return view;
    }



    public void openLightCalcFragment() {
        hideProductViews(); // Hide the product-related views

        Fragment lightCalcFragment = new LightCalcFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, lightCalcFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void hideProductViews() {
        itemRecyclerView.setVisibility(View.GONE);
        btnLightCalc.setVisibility(View.GONE);
    }

    private void getAllProducts() {
        allProductsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    // Retrieve the data for each product
                    String name = document.getString("name");
                    String description = document.getString("description");
                    String price = document.getString("price");
                    String adminName = document.getString("adminName");
                    String quantity = document.getString("quantity");
                    String imageURL = document.getString("imageURL");

                    if (imageURL != null && !imageURL.isEmpty()) {
                        // Retrieve the image from storage
                        StorageReference imageRef = storage.getReferenceFromUrl(imageURL);
                        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                            // Use the image bytes as needed
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            // Create a new Item object with the retrieved data
                            Item item = new Item(name, description, price, adminName, quantity, imageURL); // Pass imageURL, not bmp

                            // Add the item to the list
                            itemList.add(item);

                            // Notify the adapter that the data set has changed
                            itemAdapter.notifyDataSetChanged();
                        }).addOnFailureListener(exception -> {
                            // Handle any errors that occurred while retrieving the image
                        });
                    } else {
                        // Handle the case where imageURL is null or empty
                    }
                }
            } else {
                // Handle errors that occurred while fetching the products
            }
        });
    }

    // Function to get all lamps
    private void getAllLamps() {
        allLampsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    // Retrieve the data for each lamp
                    String name = document.getString("name");
                    String description = document.getString("description");
                    String price = document.getString("price");
                    String adminName = document.getString("adminName");
                    String quantity = document.getString("quantity");
                    String imageURL = document.getString("imageURL");  // image is a string URL now

                    // Create a new Item object with the retrieved data
                    Item item = new Item(name, description, price, adminName, quantity, imageURL); // use imageURL here

                    // Add the item to the list
                    itemList.add(item);

                    // Notify the adapter that the data set has changed
                    itemAdapter.notifyDataSetChanged();
                }
            } else {
                // Handle errors that occurred while fetching the lamps
            }
        });
    }



    @Override
    public void onClick(View v) {
        if (v == btnLightCalc)
            openLightCalcFragment();

    }

//    private void addToCart() {
//        String userId = auth.getCurrentUser().getUid();
//        String saveCurrDate, saveCurrTime;
//        Calendar calDate = Calendar.getInstance();
//        SimpleDateFormat currDate = new SimpleDateFormat("MM/dd/yyyy");
//        saveCurrDate = currDate.format(calDate.getTime());
//        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss a");
//        saveCurrTime = currDate.format(calDate.getTime());
//
//        final HashMap<String, Object> cartMap = new HashMap<>();
//        cartMap.put("Time", saveCurrTime);
//        cartMap.put("Date", saveCurrDate);
//        cartMap.put("itemName", itemName.getText().toString());
//        cartMap.put("itemPrice", itemPrice.getText().toString());
//
//
//        db.collection("Users").document(userId).collection("cart").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        Toast.makeText(getActivity(), "Added to cart", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//    }
}
