package com.finalproject.allabouthomeuser.activities;

import static com.google.common.net.HostSpecifier.isValid;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.finalproject.allabouthomeuser.R;
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
        String email = firebaseUser.getEmail();
        if (email != null) {
            firebaseUser.reauthenticate(com.google.firebase.auth.EmailAuthProvider.getCredential(email, originalPass))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            firebaseUser.updatePassword(newPass)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
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

}

