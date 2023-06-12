package com.finalproject.allabouthomeuser.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.models.myCart;
import com.finalproject.allabouthomeuser.models.MyCartAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MycartFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<myCart> cartList;
    private MyCartAdapter cartAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private TextView tvTotalPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycart, container, false);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.cartitems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(getContext(), cartList);
        recyclerView.setAdapter(cartAdapter);

        tvTotalPrice = view.findViewById(R.id.textView);

        fetchCartItems();

        return view;
    }

    private void fetchCartItems() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("Users").document(userId).collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cartList.clear();
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                myCart mycart = doc.toObject(myCart.class);
                                cartList.add(mycart);
                            }
                            cartAdapter.notifyDataSetChanged();

                            // Calculate total price and display it
                            double totalPrice = calculateTotalPrice(cartList);
                            displayTotalPrice(totalPrice);
                        }
                    }
                });
    }

    private double calculateTotalPrice(List<myCart> cartItems) {
        double totalPrice = 0;
        for (myCart item : cartItems) {
//            totalPrice += item.getPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    private void displayTotalPrice(double totalPrice) {
        String totalPriceText = "Total Price: $" + totalPrice;
        tvTotalPrice.setText(totalPriceText);
    }
}
