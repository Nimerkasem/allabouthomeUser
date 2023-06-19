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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private CollectionReference allProductsRef = db.collection("allproducts");
    private CollectionReference allLampsRef = db.collection("alllamps");
    private List<Item> itemList;
    private RecyclerView itemRecyclerView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ItemAdapter itemAdapter;
    private Button btnLightCalc;
    private Button btnShowProducts;

    private boolean isProductListVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        itemList = new ArrayList<>();

        itemRecyclerView = view.findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemAdapter = new ItemAdapter(getActivity(), itemList);
        itemRecyclerView.setAdapter(itemAdapter);

        btnLightCalc = view.findViewById(R.id.btnLightCalc);
        btnShowProducts = view.findViewById(R.id.btnShowProducts);

        btnLightCalc.setOnClickListener(this);
        btnShowProducts.setOnClickListener(this);

        // Initially, hide the product list
        hideProductList();

        getAllProducts();
        getAllLamps();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnLightCalc) {
            openLightCalcFragment();
            hideButtons();
        } else if (v == btnShowProducts) {
            toggleProductListVisibility();
            hideButtons();
        }
    }

    private void hideButtons() {
        btnLightCalc.setVisibility(View.GONE);
        btnShowProducts.setVisibility(View.GONE);
    }

    private void toggleProductListVisibility() {
        if (isProductListVisible) {
            hideProductList();
        } else {
            showProductList();
        }
    }

    private void hideProductList() {
        itemRecyclerView.setVisibility(View.GONE);
        isProductListVisible = false;
    }

    private void showProductList() {
        itemRecyclerView.setVisibility(View.VISIBLE);
        isProductListVisible = true;
    }

    public void openLightCalcFragment() {
        hideProductViews();

        Fragment lightCalcFragment = new LightCalcFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, lightCalcFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void hideProductViews() {
        itemRecyclerView.setVisibility(View.GONE);
        btnShowProducts.setVisibility(View.GONE);
    }

    private void getAllProducts() {
        allProductsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {

                    int quantity = document.getLong("quantity").intValue();
                    if (quantity > 0) {

                        // Retrieve the data for each product
                        String name = document.getString("name");
                        String description = document.getString("description");
                        int price = document.getLong("price").intValue();
                        String adminName = document.getString("adminName");

                        String imageURL = document.getString("imageURL");

                        if (imageURL != null && !imageURL.isEmpty()) {

                            StorageReference imageRef = storage.getReferenceFromUrl(imageURL);
                            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {

                             //   Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


                                Item item = new Item(name, description, price, adminName, quantity, imageURL); // Pass imageURL, not bmp


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
                    int quantity = document.getLong("quantity").intValue();
                    if (quantity > 0) {
                        // Retrieve the data for each lamp
                        String name = document.getString("name");
                        String description = document.getString("description");
                        int price = document.getLong("price").intValue();
                        String adminName = document.getString("adminName");


                        String imageURL = document.getString("imageURL");


                        Item item = new Item(name, description, price, adminName, quantity, imageURL);


                        itemList.add(item);


                        itemAdapter.notifyDataSetChanged();
                    }
                }
            } else {

            }
        });
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
