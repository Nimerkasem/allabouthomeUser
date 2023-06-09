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

    import java.util.List;

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
        private List<Item> itemList;
        private FirebaseAuth mAuth;

        public ItemAdapter(Context context, List<Item> itemList) {
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
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Item existingProduct = document.toObject(Item.class);
                                    // Update quantity if needed here

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
                                    db.collection("Users").document(userId)
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
