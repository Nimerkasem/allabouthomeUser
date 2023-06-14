package com.finalproject.allabouthomeuser.models;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button; // import standard android Button
import android.widget.Toast; // import Toast

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalproject.allabouthomeuser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private FirebaseAuth mAuth;
    private Context context; // Add context field

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Use Glide to load the image from the URL
        Glide.with(holder.itemView.getContext()).load(item.getImage()).into(holder.itemImage);

        holder.itemName.setText(item.getName());
        holder.itemDescription.setText(item.getDescription());
        holder.itemPrice.setText(item.getPrice());
        holder.itemAdmin.setText(item.getAdminName());
        holder.itemQuantity.setText(item.getQuantity());
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mAuth.getCurrentUser().getUid();
                addToCart(userId, item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;
        TextView itemDescription;
        TextView itemPrice;
        TextView itemAdmin;
        TextView itemQuantity;
        Button addToCart; // use standard android Button

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemAdmin = itemView.findViewById(R.id.itemAdmin);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            addToCart = itemView.findViewById(R.id.addtocart);
        }
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void addToCart(String userId, Item product) {
        db.collection("Users").document(userId)
                .collection("cart")
                .document(product.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Item existingProduct = document.toObject(Item.class);
                            // Update quantity if needed here

                            db.collection("Users").document(userId)
                                    .collection("cart")
                                    .document(product.getName())
                                    .set(existingProduct)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        updateAdminCart(userId, product.getAdminName(), product.getPrice());
                                        showToast("Item added to cart successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error updating document", e);
                                    });
                        } else {
                            db.collection("Users").document(userId)
                                    .collection("cart")
                                    .document(product.getName())
                                    .set(product)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        updateAdminCart(userId, product.getAdminName(), product.getPrice());
                                        showToast("Item added to cart successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error writing document", e);
                                    });
                        }
                    } else {
                        Log.w(TAG, "Error getting document.", task.getException());
                    }
                });
    }

    private void updateAdminCart(String userId, String adminId, String totalPrice) {
        db.collection("Users").document(userId)
                .collection("adminCart")
                .document(adminId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Update existing admin cart
                            Map<String, Object> adminCartData = document.getData();
                            if (adminCartData != null) {
                                HashMap<String, Integer> adminCart = new HashMap<>();
                                for (Map.Entry<String, Object> entry : adminCartData.entrySet()) {
                                    String key = entry.getKey();
                                    Object value = entry.getValue();
                                    if (value instanceof Long) {
                                        adminCart.put(key, ((Long) value).intValue());
                                    } else if (value instanceof Integer) {
                                        adminCart.put(key, (Integer) value);
                                    }
                                }

                                if (adminCart.containsKey(adminId)) {
                                    // Update total price
                                    int existingPrice = adminCart.get(adminId);
                                    int totalPriceInt = Integer.parseInt(totalPrice);
                                    int newPrice = existingPrice + totalPriceInt;
                                    adminCart.put(adminId, newPrice);
                                }

                                db.collection("Users").document(userId)
                                        .collection("adminCart")
                                        .document(adminId)
                                        .set(adminCart)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Admin cart successfully updated!");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w(TAG, "Error updating admin cart", e);
                                        });
                            }
                        } else {
                            // Create new admin cart
                            HashMap<String, Integer> adminCart = new HashMap<>();
                            adminCart.put(adminId, Integer.parseInt(totalPrice));

                            db.collection("Users").document(userId)
                                    .collection("adminCart")
                                    .document(adminId)
                                    .set(adminCart)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Admin cart successfully created!");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error creating admin cart", e);
                                    });
                        }
                    } else {
                        Log.w(TAG, "Error getting admin cart.", task.getException());
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}