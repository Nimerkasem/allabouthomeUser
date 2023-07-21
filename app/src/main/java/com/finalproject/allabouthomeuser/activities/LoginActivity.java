package com.finalproject.allabouthomeuser.activities;

import static android.content.ContentValues.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.finalproject.allabouthomeuser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText login_email, login_pass;
    private Button loginBtn,join;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_email =(EditText) findViewById(R.id.login_email);
        login_pass=(EditText) findViewById(R.id.login_pass);
        loginBtn=(Button) findViewById(R.id.loginBtn);
        join=(Button) findViewById(R.id.join);
        sp = getSharedPreferences("CurrentUser", 0);
        join.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == join){
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        }
        if(v == loginBtn){
            LoginUser();
        }
    }

    private void LoginUser() {
        String email=login_email.getText().toString();
        String pass=login_pass.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"please enter your email address",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(),"please enter your password",Toast.LENGTH_SHORT).show();
        }else{
            loginAccountInFirebase(email,pass);
        }
    }
    private void loginAccountInFirebase(String email, String pass) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Log.d(TAG, "Error signing in: ", task.getException());
                    Toast.makeText(LoginActivity.this, "Failed to sign in. Please check your email and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}