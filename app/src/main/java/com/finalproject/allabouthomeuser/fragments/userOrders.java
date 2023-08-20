package com.finalproject.allabouthomeuser.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.models.Item;
import com.finalproject.allabouthomeuser.models.Order;
import com.finalproject.allabouthomeuser.models.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class userOrders extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userorder, container, false);

        RecyclerView recyclerViewUserOrders = view.findViewById(R.id.recyclerViewItems);
        recyclerViewUserOrders.setLayoutManager(new LinearLayoutManager(getActivity()));

        OrderAdapter orderAdapter = new OrderAdapter(new ArrayList<>());
        recyclerViewUserOrders.setAdapter(orderAdapter);

        fetchUserOrders(orderAdapter);

        return view;
    }

    private void fetchUserOrders(OrderAdapter orderAdapter) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users").document(userId).collection("orders")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error fetching user orders: " + error.getMessage());
                        return;
                    }

                    List<Order> userOrders = new ArrayList<>();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Map<String, Object> orderData = document.getData();

                        Order order = new Order();
                        order.setStatus((Boolean) orderData.get("delivered"));
                        order.setStorename((String) orderData.get("storename"));

                        List<Item> items = new ArrayList<>();
                        List<Map<String, Object>> itemsDataList = (List<Map<String, Object>>) orderData.get("items");
                        if (itemsDataList != null) {
                            for (Map<String, Object> itemData : itemsDataList) {
                                Item item = new Item();
                                item.setName((String) itemData.get("name"));
                                item.setPrice(Math.toIntExact((long) itemData.get("price")));
                                item.setQuantity(Math.toIntExact((long) itemData.get("quantity")));
                                String imageUrl = (String) itemData.get("image");
                                item.setImage(imageUrl);
                                items.add(item);
                            }
                        }

                        order.setItems(items);
                        userOrders.add(order);
                    }

                    orderAdapter.setOrders(userOrders);
                    orderAdapter.notifyDataSetChanged();
                });
    }
}
