package com.finalproject.allabouthomeuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.finalproject.allabouthomeuser.databinding.ActivityHomeBinding;
import com.finalproject.allabouthomeuser.fragments.MyCartFragment;
import com.finalproject.allabouthomeuser.fragments.ProfileFragment;
import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.fragments.homeFragment;
public class HomeActivity extends AppCompatActivity {
    private Fragment currentFragment;
    private ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sp = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        String username= sp.getString("username", "");
        if(username!=null)
            Toast.makeText(this, "Welcome " + username, Toast.LENGTH_SHORT).show();

        replaceFragment(new homeFragment());
        binding.bottomnavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new homeFragment());
                    break;
                case R.id.mycart:
                    replaceFragment(new MyCartFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.Signout:
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar, menu);
        MenuItem item = menu.findItem(R.id.search);
        currentFragment = new homeFragment();
        replaceFragment(currentFragment);
        return super.onCreateOptionsMenu(menu);
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
        currentFragment = fragment;


    }


}
