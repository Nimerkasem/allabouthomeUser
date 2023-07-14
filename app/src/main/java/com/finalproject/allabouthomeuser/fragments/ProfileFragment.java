package com.finalproject.allabouthomeuser.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.activities.EditPassFActivity;
import com.finalproject.allabouthomeuser.activities.HomeActivity;
import com.finalproject.allabouthomeuser.activities.LoginActivity;
import com.finalproject.allabouthomeuser.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private Users user;
    private String userId;
    private ImageView profile;
    private TextView email, birthday;
    private EditText username,  phone;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();

            username = view.findViewById(R.id.username);
            email = view.findViewById(R.id.email);
            phone = view.findViewById(R.id.phone);
            birthday = view.findViewById(R.id.birthday);

            profile = view.findViewById(R.id.profileImg);
            profile.setOnClickListener(this);

            view.findViewById(R.id.save).setOnClickListener(this);
            view.findViewById(R.id.editPass).setOnClickListener(this);


            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Retrieve the user object from the Firestore document
                            user = document.toObject(Users.class);

                            // Update the EditText fields with the user data
                            if (user != null) {
                                username.setText(user.getUsername());
                                email.setText(user.getEmail());
                                phone.setText(user.getPhone());
                                birthday.setText(user.getBirthday());

                                // Load profile image if available
                                String profilePicUrl = user.getProfilePicUrl();
                                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                    Picasso.get().load(profilePicUrl).into(profile);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save) {
            updateProfile();
        } else if (v.getId() == R.id.editPass) {
            startActivity(new Intent(getActivity(), EditPassFActivity.class));


        } else if (v.getId() == R.id.profileImg) {
            openImageChooser();
        }
    }

    private void updateProfile() {
        if (user != null) {
            String existingAddress = user.getAddress();
            String existingBirthday = user.getBirthday();
            String existingPassword = user.getPassword();

            user.setUsername(username.getText().toString().trim());
            user.setEmail(email.getText().toString().trim());
            user.setPhone(phone.getText().toString().trim());
            user.setAddress(existingAddress);
            user.setBirthday(existingBirthday);
            user.setPassword(existingPassword);

            if (imageUri != null) {
                StorageReference imageRef = storageRef.child("user_images/" + userId);
                imageRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        user.setProfilePicUrl(uri.toString());

                                        updateUserProfile(user);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                updateUserProfile(user);
            }
        }
    }

    private void updateUserProfile(Users updatedUser) {
        db.collection("Users").document(userId)
                .set(updatedUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Navigate back to the UserProfileFragment
                            FragmentManager fragmentManager = getParentFragmentManager();
                            fragmentManager.popBackStack();
                            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profile);
        }
    }
}
