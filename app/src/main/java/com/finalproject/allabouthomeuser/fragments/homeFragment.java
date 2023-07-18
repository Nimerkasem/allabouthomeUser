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
import com.finalproject.allabouthomeuser.R;
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

import java.util.List;

public class homeFragment extends Fragment implements View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private CollectionReference allProductsRef = db.collection("allproducts");
    private List<Item> itemList;
    private RecyclerView itemRecyclerView;
    private ItemAdapter itemAdapter;
    private Button btnLightCalc;
    private Button btnShowProducts , allproduct ;
    private Button btnLampFragment ;

    private boolean isProductListVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        itemList = new ArrayList<>();

        itemRecyclerView = view.findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemAdapter = new ItemAdapter(getActivity(), itemList);
        itemRecyclerView.setAdapter(itemAdapter);
        allproduct=view.findViewById(R.id.allproduct);
        allproduct = view.findViewById(R.id.allproduct);
        allproduct.setOnClickListener(this);
        btnLightCalc = view.findViewById(R.id.btnLightCalc);
        btnShowProducts = view.findViewById(R.id.btnShowProducts);

        btnLightCalc.setOnClickListener(this);
        btnShowProducts.setOnClickListener(this);
        btnLampFragment = view.findViewById(R.id.lampfr);
        btnLampFragment.setOnClickListener(this);

        hideProductList();

        getAllProducts();

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
        } else if (v == btnLampFragment) {
            openLampFragment();
            hideButtons();
        }
     else if (v == allproduct) {
        opencatigotiesfragment();
        hideButtons();
    }
    }

    private void opencatigotiesfragment() {
        hideProductViews();
        Fragment all = new all();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, all);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    private void hideButtons() {
        allproduct.setVisibility(View.GONE);
        btnLightCalc.setVisibility(View.GONE);
        btnShowProducts.setVisibility(View.GONE);
        btnLampFragment.setVisibility(View.GONE);
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
    private void openLampFragment() {
        hideProductViews();

        Fragment lampFragment = new lampFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, lampFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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



}