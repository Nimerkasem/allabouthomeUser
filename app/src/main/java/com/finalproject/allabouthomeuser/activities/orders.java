package com.finalproject.allabouthomeuser.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.finalproject.allabouthomeuser.fragments.userOrders;

public class orders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the userOrders fragment
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new userOrders())
                .commit();
    }
}
