package com.finalproject.allabouthomeuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;
import com.finalproject.allabouthomeuser.databinding.ActivityHomeBinding;
import com.finalproject.allabouthomeuser.fragments.MyCartFragment;
import com.finalproject.allabouthomeuser.fragments.ProfileFragment;
import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.fragments.furniture;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements Application.ActivityLifecycleCallbacks {
    private Fragment currentFragment;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getApplication().registerActivityLifecycleCallbacks(this);
        SharedPreferences sp = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        String username = sp.getString("username", "");
        if (username != null) {
            Toast.makeText(this, "Welcome " + username, Toast.LENGTH_SHORT).show();
        }
        replaceFragment(new furniture());
        binding.bottomnavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new furniture());
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
        currentFragment = new furniture();
        replaceFragment(currentFragment);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }
    @Override
    public void onActivityStarted(Activity activity) {
    }
    @Override
    public void onActivityResumed(Activity activity) {
    }
    @Override
    public void onActivityPaused(Activity activity) {
    }
    @Override
    public void onActivityStopped(Activity activity) {
        clearRoomLampsCollection();
    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }
    @Override
    public void onActivityDestroyed(Activity activity) {
        clearRoomLampsCollection();
    }
    private void clearRoomLampsCollection() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users").document(currentUserId).collection("roomlamps").get()
                .addOnCompleteListener(task -> {
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
        firestore.collection("Users").document(currentUserId).collection("userroom").get()
                .addOnCompleteListener(task -> {
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }
}
