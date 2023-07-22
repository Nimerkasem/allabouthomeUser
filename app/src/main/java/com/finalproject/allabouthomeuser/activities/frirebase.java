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

public class frirebase {



        public static void clearRoomLampsCollection() {
            // Get the current user ID (assuming you have already authenticated the user)
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Firestore instance
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Reference to the current user's document
            DocumentReference currentUserRef = firestore.collection("Users").document(currentUserId);

            // Reference to the "roomlamps" subcollection under the current user's document
            CollectionReference roomLampsRef = currentUserRef.collection("roomlamps");

            // Get all documents in the "roomlamps" subcollection
            roomLampsRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    List<Task<Void>> deleteTasks = new ArrayList<>();
                    for (DocumentSnapshot document : documents) {
                        // Add the delete task for each document to the list
                        deleteTasks.add(document.getReference().delete());
                    }

                    // Execute all delete tasks in parallel
                    Tasks.whenAll(deleteTasks)
                            .addOnSuccessListener(result -> {
                                // Roomlamps subcollection is cleared successfully.
                            })
                            .addOnFailureListener(e -> {
                                // An error occurred while clearing the roomlamps subcollection.
                            });
                } else {
                    // An error occurred while fetching the documents from the roomlamps subcollection.
                }
            });
        }
    }


