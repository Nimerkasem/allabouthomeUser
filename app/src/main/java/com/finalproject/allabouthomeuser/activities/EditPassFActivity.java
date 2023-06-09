package com.finalproject.allabouthomeuser.activities;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditPassFActivity extends AppCompatActivity {
    private EditText org, newp, confp;
    private Button save;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pass_factivity);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        org = findViewById(R.id.orginal_pass);
        newp = findViewById(R.id.new_pass);
        confp = findViewById(R.id.conf_pass);
        save = findViewById(R.id.savepass);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNewPass();
            }
        });

    }

    private void updateNewPass() {
        String originalPass = org.getText().toString();
        String newPass = newp.getText().toString();
        String confirmPass = confp.getText().toString();

        if (originalPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(EditPassFActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(getApplicationContext(), "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValid(newPass) || !isValid(confirmPass)) {
            Toast.makeText(getApplicationContext(), "Password must contain at least 8 characters, a letter, a digit, and a special symbol", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reauthenticate the user with their current password
        String email = firebaseUser.getEmail();
        if (email != null) {
            firebaseUser.reauthenticate(com.google.firebase.auth.EmailAuthProvider.getCredential(email, originalPass))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update the password in Firebase Authentication
                            firebaseUser.updatePassword(newPass)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            // Update the password in Firestore
                                            String userId = firebaseUser.getUid();
                                            firestore.collection("Users")
                                                    .document(userId)
                                                    .update("password", newPass)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(EditPassFActivity.this, HomeActivity.class));
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(getApplicationContext(), "Failed to update password in Firestore", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed. Please check your current password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public static boolean isValid(String pass) {
        int f1 = 0, f2 = 0, f3 = 0;
        if (pass.length() < 8) {
            return false;
        } else {
            for (int i = 0; i < pass.length(); i++) {
                if (Character.isLetter(pass.charAt(i))) f1++;
                else if (Character.isDigit(pass.charAt(i))) f2++;
                else {
                    char c = pass.charAt(i);
                    if (c >= 33 && c <= 46 || c == 64) f3 = 1;
                }
            }
            if (f1 >= 8 && f2 >= 1 && f3 >= 1)
                return true;
            return false;
        }
    }
}

