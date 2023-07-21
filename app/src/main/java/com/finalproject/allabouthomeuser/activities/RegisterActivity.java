package com.finalproject.allabouthomeuser.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText inputUsername, inputEmail, inputPhone, inputBirthday, inputCity, inputPass;
    private Button registerBtn, loginBtn;

    FirebaseFirestore database =FirebaseFirestore.getInstance();
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputUsername = (EditText) findViewById(R.id.username);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPhone = (EditText) findViewById(R.id.phone);
        inputBirthday = (EditText) findViewById(R.id.birthday);
        inputCity = (EditText) findViewById(R.id.City);
        inputPass = (EditText) findViewById(R.id.pass);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn.setOnClickListener(this);
        loginBtn.setOnClickListener((View.OnClickListener) this);
        database= FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View v) {
        if(v==loginBtn){
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
        else if(v== registerBtn){
            isValidData();
        }
    }
    private void isValidData() {
        String username=inputUsername.getText().toString();
        String email=inputEmail.getText().toString();
        String phone=inputPhone.getText().toString();
        String birthday=inputBirthday.getText().toString();
        String city=inputCity.getText().toString();
        String password=inputPass.getText().toString();
        if(TextUtils.isEmpty(username) || username.length()<5){
            Toast.makeText(this, "username must have 5 Character or more", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email) ){
            Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phone) && phone.length()==10){
            Toast.makeText(this, "phone must have 10 Numbers", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(birthday) ){
            Toast.makeText(this, "Please enter your birthday", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(city) ){
            Toast.makeText(this, "Please enter your city", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password) || !isValid(password) ){
            Toast.makeText(getApplicationContext(), "password must contain at least 8 characters,having letter,digit and special symbol", Toast.LENGTH_SHORT).show();
            return;
        }
        checkIfUserExists(email);
    }

    private void checkIfUserExists(String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = usersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(RegisterActivity.this, "try another email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "create", Toast.LENGTH_SHORT).show();

                    createAccount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error occurred, handle the error
            }
        });
    }
    private void createAccount() {
        String username=inputUsername.getText().toString();
        String email=inputEmail.getText().toString();
        String phone=inputPhone.getText().toString();
        String birthday=inputBirthday.getText().toString();
        String city=inputCity.getText().toString();
        String password=inputPass.getText().toString();
        Users u =new Users(username,email,phone,birthday,city,password);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();

                            CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
                            usersRef.document(uid).set(u)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Error creating account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error creating account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static boolean isValid(String pass) {
        int f1=0,f2=0,f3=0;
        if(pass.length()<8) {
            return false;
        }else{
            for(int i=0;i<pass.length();i++){
                if(Character.isLetter(pass.charAt(i))) f1++;
                else if (Character.isDigit(pass.charAt(i))) f2++;
                else{
                    char c=pass.charAt(i);
                    if(c>=33&&c<=46 || c==64) f3=1;
                }
            }
            if(f1>=8 && f2>=1 && f3>=1)
                return true;
            return false;
        }
    }
}


