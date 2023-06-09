package com.finalproject.allabouthomeuser.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.models.Item;
import com.finalproject.allabouthomeuser.models.Lamp;
import com.finalproject.allabouthomeuser.models.MyCartAdapter;
import com.finalproject.allabouthomeuser.models.myCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCartFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<myCart> cartList;
    private MyCartAdapter cartAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private TextView tvTotalPrice;
    private Button buy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycart, container, false);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.cart_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(getContext(), cartList,this);
        recyclerView.setAdapter(cartAdapter);
        buy = view.findViewById(R.id.buy_now);
        tvTotalPrice = view.findViewById(R.id.total_price_text_view);
        buy.setOnClickListener(v -> payment());
        fetchCartItems();

        return view;
    }

    private void payment() {
        for (int i = 0; i < cartList.size(); i++) {
            Item item = (Item) cartList.get(i);
            String itemUid = item.getUid();
            int currentQuantity = item.getQuantity();
            String adminUid = item.getAdminuid();
            String itemName = item.getName();

            firestore.collection("allproducts").document(itemUid)
                    .update("quantity", FieldValue.increment(-currentQuantity))
                    .addOnFailureListener(e -> {
                        showMessage("Error updating quantity in 'allproducts' collection: " + e.getMessage());
                    });

            firestore.collection("Admins").document(adminUid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<Map<String, Object>> products = (List<Map<String, Object>>) documentSnapshot.get("products");
                            if (products != null) {
                                for (int j = 0; j < products.size(); j++) {
                                    Map<String, Object> productData = products.get(j);
                                    String productId = (String) productData.get("uid");
                                    if (productId != null && productId.equals(itemUid)) {
                                        long existingQuantity = (long) productData.get("quantity");
                                        long updatedQuantity = existingQuantity - currentQuantity;
                                        productData.put("quantity", updatedQuantity);
                                    }
                                }

                                firestore.collection("Admins").document(adminUid)
                                        .update("products", products)
                                        .addOnSuccessListener(aVoid -> {
                                            System.out.println("Quantity updated successfully for product: " + itemName);
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Error updating quantity in admin's 'products' array: " + e.getMessage());
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Error retrieving admin document: " + e.getMessage());
                    });

            if (item instanceof Lamp) {
                Lamp lamp = (Lamp) item;
                int lampCurrentQuantity = lamp.getQuantity();

                firestore.collection("alllamps").document(itemUid)
                        .update("quantity", FieldValue.increment(-lampCurrentQuantity))
                        .addOnFailureListener(e -> {
                            showMessage("Error updating quantity in 'alllamps' collection: " + e.getMessage());
                        });

                firestore.collection("Admins").document(adminUid)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                List<Map<String, Object>> lamps = (List<Map<String, Object>>) documentSnapshot.get("lamps");
                                if (lamps != null) {
                                    for (int j = 0; j < lamps.size(); j++) {
                                        Map<String, Object> lampData = lamps.get(j);
                                        String lampId = (String) lampData.get("uid");
                                        if (lampId != null && lampId.equals(itemUid)) {
                                            long existingQuantity = (long) lampData.get("quantity");
                                            long updatedQuantity = existingQuantity - lampCurrentQuantity;
                                            lampData.put("quantity", updatedQuantity);
                                        }
                                    }

                                    firestore.collection("Admins").document(adminUid)
                                            .update("lamps", lamps)
                                            .addOnSuccessListener(aVoid -> {
                                                System.out.println("Quantity updated successfully for lamp: " + itemName);
                                            })
                                            .addOnFailureListener(e -> {
                                                System.out.println("Error updating quantity in admin's 'lamps' array: " + e.getMessage());
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Error retrieving admin document: " + e.getMessage());
                        });
            }
        }

        String userId = auth.getCurrentUser().getUid();
        firestore.collection("Users")
                .document(userId)
                .collection("adminCart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    HashMap<String, Integer> adminCart = new HashMap<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String adminId = document.getId();
                        int totalPrice = document.getLong(adminId).intValue();
                        adminCart.put(adminId, totalPrice);
                    }
                    List<Task<Void>> deleteTasks = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        deleteTasks.add(document.getReference().delete());
                    }
                    Tasks.whenAll(deleteTasks)
                            .addOnSuccessListener(aVoid -> {
                                for (Map.Entry<String, Integer> entry : adminCart.entrySet()) {
                                    String adminUid = entry.getKey();
                                    Integer amountToSend = entry.getValue();

                                    Map<String, Object> bagData = new HashMap<>();
                                    bagData.put("adminUid", adminUid);
                                    bagData.put("amountToSend", amountToSend);

                                    firestore.collection("Admins")
                                            .document(adminUid)
                                            .collection("bag")
                                            .add(bagData)
                                            .addOnSuccessListener(documentReference -> {
                                                for (Item item : cartList) {
                                                    String itemName = item.getName();
                                                    int amountBought = item.getQuantity();
                                                    String itemAdminUid = item.getAdminuid();

                                                    if (adminUid.equals(itemAdminUid)) {
                                                        firestore.collection("Admins").document(adminUid)
                                                                .collection("sales")
                                                                .whereEqualTo("name", itemName)
                                                                .get()
                                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                                    if (!queryDocumentSnapshots.isEmpty()) {
                                                                        DocumentSnapshot salesDocument = queryDocumentSnapshots.getDocuments().get(0);
                                                                        String salesDocumentId = salesDocument.getId();
                                                                        long existingQuantityBought = (long) salesDocument.get("quantityBought");
                                                                        long updatedQuantityBought = existingQuantityBought + amountBought;

                                                                        firestore.collection("Admins").document(adminUid)
                                                                                .collection("sales").document(salesDocumentId)
                                                                                .update("quantityBought", updatedQuantityBought)
                                                                                .addOnSuccessListener(aVoid2 -> {
                                                                                    System.out.println("Quantity updated successfully in 'sales' subcollection for item: " + itemName);
                                                                                })
                                                                                .addOnFailureListener(e -> {
                                                                                    System.out.println("Error updating quantity in 'sales' subcollection for item: " + itemName + ": " + e.getMessage());
                                                                                });
                                                                    } else {
                                                                        Map<String, Object> salesData = new HashMap<>();
                                                                        salesData.put("name", itemName);
                                                                        salesData.put("quantityBought", amountBought);

                                                                        firestore.collection("Admins").document(adminUid)
                                                                                .collection("sales").add(salesData)
                                                                                .addOnSuccessListener(documentReference2 -> {
                                                                                    System.out.println("New item added to 'sales' subcollection: " + itemName);
                                                                                })
                                                                                .addOnFailureListener(e -> {
                                                                                    System.out.println("Error adding item to 'sales' subcollection: " + itemName + ": " + e.getMessage());
                                                                                });
                                                                    }
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    System.out.println("Error retrieving 'sales' subcollection for item: " + itemName + ": " + e.getMessage());
                                                                });
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                System.out.println("Error adding bag data for admin: " + adminUid + ": " + e.getMessage());
                                            });
                                }

                                firestore.collection("Users").document(userId).collection("cart")
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            List<Task<Void>> clearCartTasks = new ArrayList<>();
                                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                                clearCartTasks.add(doc.getReference().delete());
                                            }

                                            Tasks.whenAll(clearCartTasks)
                                                    .addOnSuccessListener(aVoid1 -> {
                                                        cartList.clear();
                                                        cartAdapter.notifyDataSetChanged();
                                                        displayTotalPrice(0.0);
                                                        showMessage("Payment successful!");
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        showMessage("Error clearing cart: " + e.getMessage());
                                                    });
                                        });
                            })
                            .addOnFailureListener(e -> {
                                showMessage("Error clearing adminCart: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    showMessage("Error retrieving adminCart documents: " + e.getMessage());
                });
    }



    private void fetchCartItems() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("Users").document(userId).collection("cart")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cartList.clear();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            myCart cartItem = doc.toObject(myCart.class);
                            cartList.add(cartItem);
                        }
                        cartAdapter.notifyDataSetChanged();

                        double totalPrice = calculateTotalPrice(cartList);
                        displayTotalPrice(totalPrice);
                    }
                });
    }


    private double calculateTotalPrice(List<myCart> cartItems) {
        double totalPrice = 0;
        for (myCart item : cartItems) {
            double itemPrice = item.getPrice();
            double itemQuantity = item.getQuantity();
            totalPrice += (itemPrice * itemQuantity);
        }

        return totalPrice;
    }

    private void displayTotalPrice(double totalPrice) {
        String totalPriceText = "Total Price: ₪" + totalPrice;
        tvTotalPrice.setText(totalPriceText);
    }

    public void updateTotalPrice() {
        double totalPrice = calculateTotalPrice(cartList);
        displayTotalPrice(totalPrice);
    }



    private void showMessage(String message) {
       Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
