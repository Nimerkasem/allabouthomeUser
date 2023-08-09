package com.finalproject.allabouthomeuser.activities;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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
}

