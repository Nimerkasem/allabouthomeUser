package com.finalproject.allabouthomeuser.activities;

import android.os.Build;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class firebase {
    public static void clearRoomLampsCollection() {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference currentUserRef = firestore.collection("Users").document(currentUserId);
        CollectionReference roomLampsRef = currentUserRef.collection("roomlamps");
        roomLampsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                List<Task<Void>> deleteTasks = new ArrayList<>();
                for (DocumentSnapshot document : documents) {
                    deleteTasks.add(document.getReference().delete());
                }

                Tasks.whenAll(deleteTasks)
                        .addOnSuccessListener(result -> {
                        })
                        .addOnFailureListener(e -> {
                        });
            } else {
            }
        });
    }

    public  CompletableFuture<Boolean> adminactive(String adminuid) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference currentadminRef = firestore.collection("Admins").document(adminuid);

        CompletableFuture<Boolean> future = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            future = new CompletableFuture<>();
        }

        CompletableFuture<Boolean> finalFuture = future;
        currentadminRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    boolean isActive = document.getBoolean("isActive");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        finalFuture.complete(isActive);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        finalFuture.complete(false);
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    finalFuture.complete(false);
                }
            }
        });

        return future;
    }
}


