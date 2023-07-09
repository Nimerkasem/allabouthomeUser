package com.finalproject.allabouthomeuser.models;

import static android.content.ContentValues.TAG;

import static com.finalproject.allabouthomeuser.models.ItemAdapter.updateAdminCart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalproject.allabouthomeuser.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private Context context;
    private List<myCart> list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyCartAdapter(Context context, List<myCart> list) {
        this.context = context;
        this.list = list;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycart_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartAdapter.ViewHolder holder, int position) {
        myCart item = list.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(String.valueOf(item.getPrice()));
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.desc.setText(item.getDescription());
        mAuth = FirebaseAuth.getInstance();
        Glide.with(context).load(item.getImage()).into(holder.image);

        holder.increaseQuantityButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.quantity.getText().toString());
            quantity++;
            holder.quantity.setText(String.valueOf(quantity));
            item.setQuantity(quantity);

            updateCartItem(item);
        });

        holder.decreaseQuantityButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.quantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                holder.quantity.setText(String.valueOf(quantity));
                item.setQuantity(quantity);

                updateCartItem(item);
            }
        });

        holder.remove.setOnClickListener(v -> {
            list.remove(position);
            notifyDataSetChanged();

            removeCartItem(item);
        });

        holder.addButton.setOnClickListener(v -> {
            if (item instanceof Lamp) {
                Lamp lamp = (Lamp) item;
                String lampUid = lamp.getUid();

                String userId = mAuth.getCurrentUser().getUid();

                db.collection("Users").document(userId).collection("cart").document(lampUid)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String lampName = documentSnapshot.getString("name");
                                double wattage = documentSnapshot.getDouble("watt");
                                int shade = documentSnapshot.getLong("shade").intValue();

                                Map<String, Object> lampData = new HashMap<>();
                                lampData.put("uid", lampUid);
                                lampData.put("name", lampName);
                                lampData.put("wattage", wattage);
                                lampData.put("shade", shade);

                                db.collection("Users").document(userId).collection("roomlamps")
                                        .add(lampData)
                                        .addOnSuccessListener(documentReference -> {
                                            showMessage("Lamp added to roomlamps.");
                                        })
                                        .addOnFailureListener(e -> {
                                            showMessage("Failed to add lamp to roomlamps: " + e.getMessage());
                                        });
                            } else {
                                showMessage("Lamp not found in the user's cart.");
                            }
                        })
                        .addOnFailureListener(e -> {
                            showMessage("Error retrieving lamp details: " + e.getMessage());
                        });
            } else {
                showMessage("Selected item is not a lamp.");
            }
        });
    }
        @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, price, quantity;
        ImageView image;
        Button increaseQuantityButton, decreaseQuantityButton, remove, addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewProductName);
            desc = itemView.findViewById(R.id.textViewProductDescription);
            price = itemView.findViewById(R.id.textViewProductPrice);
            quantity = itemView.findViewById(R.id.textViewProductQuantity);
            image = itemView.findViewById(R.id.imageViewProductImage);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
            remove = itemView.findViewById(R.id.remove);
            addButton = itemView.findViewById(R.id.add);
        }
    }
    private void updateCartItem(myCart item) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference cartItemRef = db.collection("Users")
                .document(userId)
                .collection("cart")
                .document(item.getUid());

        cartItemRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        myCart cartItem = documentSnapshot.toObject(myCart.class);
                        if (cartItem != null) {
                            int originalQuantity = cartItem.getQuantity();
                            int newQuantity = item.getQuantity();
                            int price = item.getPrice();
                            int quantityDiff = newQuantity - originalQuantity;
                            int totalDiff = price * quantityDiff;

                            cartItemRef.update("quantity", item.getQuantity())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Cart item quantity successfully updated!");

                                        // Update the admin cart based on the quantity difference
                                        updateAdminCart(userId, item.getAdminuid(), totalDiff);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error updating cart item quantity", e);
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error retrieving cart item", e);
                });
    }


    private void removeCartItem(myCart item) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference cartItemRef = db.collection("Users")
                .document(userId)
                .collection("cart")
                .document(item.getUid());

        cartItemRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cart item successfully removed!");
                    int quantity = item.getQuantity();
                    int price = item.getPrice();
                    int total = quantity * price;
                    updateAdminCart(userId, item.getAdminuid(), -total);

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error removing cart item", e);
                });
    }



//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public View addButton;
//        TextView name, desc, price, quantity;
//        ImageView image;
//        Button increaseQuantityButton, decreaseQuantityButton, remove;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            name = itemView.findViewById(R.id.textViewProductName);
//            desc = itemView.findViewById(R.id.textViewProductDescription);
//            price = itemView.findViewById(R.id.textViewProductPrice);
//            quantity = itemView.findViewById(R.id.textViewProductQuantity);
//            image = itemView.findViewById(R.id.imageViewProductImage);
//            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
//            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
//            remove = itemView.findViewById(R.id.remove);
//
//
//                addButton = itemView.findViewById(R.id.add);
//            }
        private void showMessage(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        }

