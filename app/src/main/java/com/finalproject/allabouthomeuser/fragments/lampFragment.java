package com.finalproject.allabouthomeuser.fragments;

import static com.finalproject.allabouthomeuser.models.Lamp.isRoomKindInCategoryList;
import static com.finalproject.allabouthomeuser.models.room.Ledwatt;
import static com.finalproject.allabouthomeuser.models.room.getShade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.finalproject.allabouthomeuser.R;
import com.finalproject.allabouthomeuser.models.Lamp;
import com.finalproject.allabouthomeuser.models.LampAdapter;
import com.finalproject.allabouthomeuser.models.room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class lampFragment extends Fragment {
    private RecyclerView lampRecyclerView;
    private LampAdapter lampAdapter;
    private List<Lamp> lampList;
    private FirebaseFirestore db;
    private Button filter;
    private boolean isFilterActive = false;



    public lampFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lamp, container, false);
        lampRecyclerView = view.findViewById(R.id.lampRecyclerView);
        lampList = new ArrayList<>();
        lampAdapter = new LampAdapter(lampList, getActivity());
        lampRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampRecyclerView.setAdapter(lampAdapter);
        db = FirebaseFirestore.getInstance();
        filter = view.findViewById(R.id.filter);

        filter.setOnClickListener(v -> {
            isFilterActive = !isFilterActive;
            if (isFilterActive) {
                lampsfilter();
            } else {
                getAllLamps();
            }
        });

        getAllLamps();

        return view;
    }
    public static boolean SuitableLampRoom(room room, Lamp lamp) {
        System.out.println("Lamp Wattage: " + lamp.getWatt() + " LED Watt");

        double lampWattage = lamp.getWatt();
        double roomWattNeeded = Ledwatt(room);

        System.out.println("Room Wattage Needed: " + roomWattNeeded + " LED Watt");

        if (lampWattage < roomWattNeeded || lampWattage > roomWattNeeded * 1.5) {
            return false;
        }

        if (getShade(room) != lamp.getShade() || !isRoomKindInCategoryList(room.getKind(), lamp)) {
            return false;
        }

        return true;
    }

    private void lampsfilter() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Users").document(currentUserId).collection("userroom").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Lamp> filteredLamps = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    double length = document.getDouble("length");
                    double height = document.getDouble("height");
                    double width = document.getDouble("width");
                    String kind = document.getString("kind");
                    room room = new room(length, width, height, kind);
                    for (Lamp lamp : lampList) {
                        if (SuitableLampRoom(room, lamp)) {
                            filteredLamps.add(lamp);
                        }
                    }
                }
                lampAdapter.setData(filteredLamps);
            }
        });
    }

    private void getAllLamps() {
        Query allLampsRef = db.collection("alllamps").whereGreaterThan("quantity", 0);
        allLampsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                lampList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    String itemUid = document.getId();
                    int quantity = document.getLong("quantity").intValue();
                    String name = document.getString("name");
                    String description = document.getString("description");
                    int price = document.getLong("price").intValue();
                    String adminName = document.getString("adminName");
                    String adminuid = document.getString("adminUID");
                    int shade = Math.toIntExact(document.getLong("shade"));
                    double watt = document.getLong("wattage").intValue();
                    String imageURL = document.getString("imageURL");
                    ArrayList<String> categories = (ArrayList<String>) document.get("categories");
                    Lamp lamp = new Lamp(categories,itemUid, adminuid, name, description, price, adminName, quantity, imageURL, watt, shade);
                     lampList.add(lamp);
                }
                lampAdapter.notifyDataSetChanged();
            } else {
                // Handle the error
            }
        });
    }


}
