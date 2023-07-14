package com.finalproject.allabouthomeuser.models;
import static android.content.ContentValues.TAG;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
public class LampAdapter extends RecyclerView.Adapter<LampAdapter.LampViewHolder> {

    private List<Lamp> lampList;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private OnAddToCartClickListener addToCartClickListener;

    public LampAdapter(List<Lamp> lampList, Context context) {
        this.lampList = lampList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Lamp lamp);
    }

    public void setOnAddToCartClickListener(OnAddToCartClickListener listener) {
        this.addToCartClickListener = listener;
    }

    @NonNull
    @Override
    public LampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lamp_layout, parent, false);
        return new LampViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LampViewHolder holder, int position) {
        Lamp lamp = lampList.get(position);

        holder.txtName.setText(lamp.getName());
        holder.txtDescription.setText(lamp.getDescription());
        holder.txtPrice.setText((lamp.getPrice())+"₪");
        holder.txtAdminName.setText(lamp.getAdminName());
        holder.txtWatt.setText((lamp.getWatt())+"Watt");
        holder.txtShade.setText((lamp.getShade())+"K");

        Glide.with(holder.itemView.getContext()).load(lamp.getImage()).into(holder.imgLamp);

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mAuth.getCurrentUser().getUid();
                addToCart(userId, lamp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lampList.size();
    }

    public static class LampViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName, txtDescription, txtPrice, txtAdminName,  txtWatt, txtShade;
        public ImageView imgLamp;
        public Button btnAdd;

        public LampViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.lampName);
            txtDescription = itemView.findViewById(R.id.lampDescription);
            txtPrice = itemView.findViewById(R.id.lampPrice);
            txtAdminName = itemView.findViewById(R.id.lampAdmin);
            txtWatt = itemView.findViewById(R.id.lampWatt);
            txtShade = itemView.findViewById(R.id.lampShade);
            imgLamp = itemView.findViewById(R.id.lampImage);
            btnAdd = itemView.findViewById(R.id.add);

        }
    }

    private void addToCart(String userId, Lamp lamp) {
        DocumentReference cartItemRef = db.collection("Users")
                .document(userId)
                .collection("cart")
                .document(lamp.getUid());
        cartItemRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int quantity = document.getLong("quantity").intValue();

                    db.collection("alllamps").document(lamp.getUid()).get().addOnCompleteListener(secondTask -> {
                        if (secondTask.isSuccessful()) {
                            DocumentSnapshot secondDocument = secondTask.getResult();
                            if (secondDocument.exists()) {
                                int maxQuantity = secondDocument.getLong("quantity").intValue();

                                if (quantity  <= maxQuantity) {
                                    int newQuantity = quantity + 1;
                                    cartItemRef.update("quantity", newQuantity)
                                            .addOnSuccessListener(aVoid -> {
                                                showToast("Lamp added to cart successfully");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w(TAG, "Error updating document", e);
                                            });
                                } else {
                                    // Quantity limit reached
                                    // Show a message or perform any desired action
                                }
                            }
                        }
                    });
                } else {
                    lamp.setQuantity(1);
                    cartItemRef.set(lamp)
                            .addOnSuccessListener(aVoid -> {
                                showToast("Lamp added to cart successfully");
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

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
