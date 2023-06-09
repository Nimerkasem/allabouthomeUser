    package com.finalproject.allabouthomeuser.models;

    import static android.content.ContentValues.TAG;

    import android.content.Context;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.finalproject.allabouthomeuser.R;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;

    import com.rey.material.widget.Button;

    import java.util.List;

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
        private List<Item> itemList;
        private FirebaseAuth mAuth;
        public ItemAdapter(Context context, List<Item> itemList) {
            this.itemList = itemList;
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
            holder.itemImage.setImageBitmap(item.getImage());
            holder.itemName.setText(item.getName());
            holder.itemDescription.setText(item.getDescription());
            holder.itemPrice.setText(item.getPrice());
            holder.itemAdmin.setText(item.getAdminName());
            holder.itemQuantity.setText(item.getQuantity());
            holder.addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = mAuth.getCurrentUser().getUid();
                    addToCart(userId, (List<Item>) item);
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
            Button addToCart ;

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

        private void addToCart(String userId, List<Item> products) {
            for (Item product : products) {
                db.collection("users").document(userId)
                        .collection("cart")
                        .document(product.getName())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Item existingProduct = document.toObject(Item.class);
//                                        existingProduct.setQuantity(existingProduct.getQuantity() + product.getQuantity());

                                        db.collection("Users").document(userId)
                                                .collection("cart")
                                                .document(product.getName())
                                                .set(existingProduct)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });
                                    } else {
                                        db.collection("users").document(userId)
                                                .collection("cart")
                                                .document(product.getName())
                                                .set(product)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);
                                                    }
                                                });
                                    }
                                } else {
                                    Log.w(TAG, "Error getting document.", task.getException());
                                }
                            }
                        });
            }
        }
    }
