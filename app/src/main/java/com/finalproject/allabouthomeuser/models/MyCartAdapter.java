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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalproject.allabouthomeuser.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private Context context;
    private List<myCart> list;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyCartAdapter(Context context, List<myCart> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycart_item, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull MyCartAdapter.ViewHolder holder, int position) {
        myCart item = list.get(holder.getAdapterPosition());
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        holder.quantity.setText(item.getQuantity());
        holder.desc.setText(item.getDescription());
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        Glide.with(context).load(item.getImage()).into(holder.image);

        holder.increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(holder.quantity.getText().toString());
                quantity++;
                holder.quantity.setText(String.valueOf(quantity));
                item.setQuantity(Long.valueOf(String.valueOf(quantity)));

                updateCartItem(item);
            }
        });

        holder.decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(holder.quantity.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    holder.quantity.setText(String.valueOf(quantity));
                    item.setQuantity(Long.valueOf(String.valueOf(quantity)));

                    updateCartItem(item);
                }
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    list.remove(position);
                    notifyDataSetChanged();

                    removeCartItem(item);
                }
            }
        });
    }

    private void updateCartItem(myCart item) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference cartItemRef = db.collection("Users")
                .document(userId)
                .collection("cart")
                .document(item.getName());

        cartItemRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        myCart cartItem = documentSnapshot.toObject(myCart.class);
                        if (cartItem != null) {
                            int originalQuantity = Integer.parseInt(cartItem.getQuantity());
                            int newQuantity = Integer.parseInt(item.getQuantity());
                            int price = Integer.parseInt(item.getPrice());
                            int quantityDiff = newQuantity - originalQuantity;
                            int totalDiff = price * quantityDiff;

                            cartItemRef.update("quantity", item.getQuantityAsLong())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Cart item quantity successfully updated!");

                                        // Update the admin cart based on the quantity difference
                                        updateAdminCart(userId, item.getAdminName(), String.valueOf(totalDiff));
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
                .document(item.getName());

        cartItemRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cart item successfully removed!");
                    int quantity = Integer.parseInt(item.getQuantity());
                    int price = Integer.parseInt(item.getPrice());
                    int total = quantity * price;
                    updateAdminCart(userId, item.getAdminName(), String.valueOf(-total));

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error removing cart item", e);
                });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, price, quantity;
        ImageView image;
        Button increaseQuantityButton, decreaseQuantityButton, remove;

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
        }
    }
}
