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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.fragments.MyCartFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private Context context;
    private List<myCart> list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MyCartFragment fragment;

    public MyCartAdapter(Context context, List<myCart> list, MyCartFragment fragment) {
        this.context = context;
        this.list = list;
        mAuth = FirebaseAuth.getInstance();
        this.fragment = fragment;
    }


    @NonNull
    @Override
    public MyCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycart_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        myCart item = list.get(position);
        holder.name.setText(item.getName());
        holder.price.setText((item.getPrice())+"₪");
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        mAuth = FirebaseAuth.getInstance();
        Glide.with(context).load(item.getImage()).into(holder.image);

        holder.increaseQuantityButton.setOnClickListener(v -> {
            String itemUid = item.getUid();

            // Check in alllamps collection
            db.collection("alllamps").document(itemUid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int maxQuantity = document.getLong("quantity").intValue();
                        int quantity = Integer.parseInt(holder.quantity.getText().toString());

                        if (quantity <= maxQuantity) {
                            quantity++;
                            holder.quantity.setText(String.valueOf(quantity));
                            item.setQuantity(quantity);

                            updateCartItem(item);
                            fragment.updateTotalPrice();
                        } else {
                            // Quantity limit reached
                            // Show a message or perform any desired action
                        }
                    } else {
                        // Check in allproducts collection
                        db.collection("allproducts").document(itemUid).get().addOnCompleteListener(secondTask -> {
                            if (secondTask.isSuccessful()) {
                                DocumentSnapshot secondDocument = secondTask.getResult();
                                if (secondDocument.exists()) {
                                    int maxQuantity = secondDocument.getLong("quantity").intValue();
                                    int quantity = Integer.parseInt(holder.quantity.getText().toString());

                                    if (quantity <= maxQuantity) {
                                        quantity++;
                                        holder.quantity.setText(String.valueOf(quantity));
                                        item.setQuantity(quantity);

                                        updateCartItem(item);
                                        fragment.updateTotalPrice();
                                    } else {
                                        // Quantity limit reached
                                        // Show a message or perform any desired action
                                    }
                                } else {
                                    // Item not found in either collection
                                    // Handle the case where the item doesn't exist
                                }
                            } else {
                                // Handle failure
                            }
                        });
                    }
                } else {
                    // Handle failure
                }
            });
        });


        holder.decreaseQuantityButton.setOnClickListener(v -> {

            int quantity = Integer.parseInt(holder.quantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                holder.quantity.setText(String.valueOf(quantity));
                item.setQuantity(quantity);

                updateCartItem(item);
                fragment.updateTotalPrice();
            }
        });



        holder.remove.setOnClickListener(v -> {
            list.remove(position);
            notifyDataSetChanged();

            removeCartItem(item);
            fragment.updateTotalPrice();
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
                                try {
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
                                } catch (Exception e) {
                                    showMessage("Error processing lamp details: " + e.getMessage());
                                }
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
        TextView name, price, quantity;
        ImageView image;
        Button increaseQuantityButton, decreaseQuantityButton, remove, addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewProductName);
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




        private void showMessage(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        }

