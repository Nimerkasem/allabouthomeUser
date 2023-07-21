package com.finalproject.allabouthomeuser.models;

import static android.content.ContentValues.TAG;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.finalproject.allabouthomeuser.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private FirebaseAuth mAuth;
    private Context context;

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
        Glide.with(holder.itemView.getContext()).load(item.getImage()).into(holder.itemImage);
        holder.itemName.setText(item.getName());
        holder.itemDescription.setText(item.getDescription());
        holder.itemPrice.setText(String.valueOf(item.getPrice())+"₪");
        holder.itemAdmin.setText(item.getAdminName());
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mAuth.getCurrentUser().getUid();
                addToCart(userId, item);
            }
        });
    }

    public void setItems(List<Item> items) {
        itemList = items;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;
        TextView itemDescription;
        TextView itemPrice;
        TextView itemAdmin;
        Button addToCart;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemAdmin = itemView.findViewById(R.id.itemAdmin);
            addToCart = itemView.findViewById(R.id.addtocart);
        }
    }

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void addToCart(String userId, Item product) {
        DocumentReference cartItemRef = db.collection("Users")
                .document(userId)
                .collection("cart")
                .document(product.getUid());
        cartItemRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int quantity = document.getLong("quantity").intValue();
                    db.collection("allproducts").document(product.getUid()).get().addOnCompleteListener(secondTask -> {
                        if (secondTask.isSuccessful()) {
                            DocumentSnapshot secondDocument = secondTask.getResult();
                            if (secondDocument.exists()) {
                                int maxQuantity = secondDocument.getLong("quantity").intValue();
                                if (quantity < maxQuantity) {
                                    int newQuantity = quantity + 1;
                                    cartItemRef.update("quantity", newQuantity)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                updateAdminCart(userId, product.getAdminuid(), product.getPrice());
                                                showToast("Item added to cart successfully");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w(TAG, "Error updating document", e);
                                            });
                                } else {
                                    showToast("cant afford this quantity");
                                }
                            }
                        }
                    });
                } else {
                    product.setQuantity(1);
                    cartItemRef.set(product)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                updateAdminCart(userId, product.getAdminuid(), product.getPrice());
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

    public static void updateAdminCart(String userId, String adminId, double totalPrice) {
        double commissionAmount = totalPrice * 0.03;
        double totalPriceAfterCommission = totalPrice - commissionAmount;

        db.collection("Users").document(userId)
                .collection("adminCart")
                .document(adminId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> adminCartData = document.getData();
                            if (adminCartData != null) {
                                HashMap<String, Double> adminCart = new HashMap<>();
                                for (Map.Entry<String, Object> entry : adminCartData.entrySet()) {
                                    String key = entry.getKey();
                                    Object value = entry.getValue();
                                    if (value instanceof Double) {
                                        adminCart.put(key, (Double) value);
                                    } else if (value instanceof Long) {
                                        adminCart.put(key, ((Long) value).doubleValue());
                                    } else if (value instanceof Integer) {
                                        adminCart.put(key, ((Integer) value).doubleValue());
                                    }
                                }

                                if (adminCart.containsKey(adminId)) {
                                    Double existingPrice = adminCart.get(adminId);
                                    double newPrice = existingPrice + totalPriceAfterCommission;
                                    adminCart.put(adminId, newPrice);
                                } else {
                                    adminCart.put(adminId, totalPriceAfterCommission);
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
                            HashMap<String, Double> adminCart = new HashMap<>();
                            adminCart.put(adminId, totalPriceAfterCommission);

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