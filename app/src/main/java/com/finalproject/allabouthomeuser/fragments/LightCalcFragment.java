package com.finalproject.allabouthomeuser.fragments;

import static com.finalproject.allabouthomeuser.models.room.Suitablelamps;
import static com.finalproject.allabouthomeuser.models.room.lamphanging;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.finalproject.allabouthomeuser.models.Lamp;
import com.finalproject.allabouthomeuser.models.room;
import com.finalproject.allabouthomeuser.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LightCalcFragment extends Fragment {
    private EditText etLength;
    private EditText etWidth;
    private Spinner spRoomKind;
    private Button btnCalculate ,check;
    private TextView tvLedWatt;
    private TextView tvShade;
    private EditText getheight;

    private TextView tvAngle;
    private  TextView setheight;
    List<Lamp> lampList = new ArrayList<>();
    FirebaseAuth mAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light_calc, container, false);
        etLength = view.findViewById(R.id.etLength);
        etWidth = view.findViewById(R.id.etWidth);
        spRoomKind = view.findViewById(R.id.spRoomKind);
        btnCalculate = view.findViewById(R.id.btnCalculate);
        tvLedWatt = view.findViewById(R.id.tvLedWatt);
        tvShade = view.findViewById(R.id.tvShade);
        tvAngle =view.findViewById(R.id.tvAngle);
        getheight=view.findViewById(R.id.getheight);
        setheight=view.findViewById(R.id.setheight);
        check =view.findViewById(R.id.check);
        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.room_kinds, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoomKind.setAdapter(adapter);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateLedWatt();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matching();
            }
        });

        return view;
    }


    private void calculateLedWatt() {
        double length = Double.parseDouble(etLength.getText().toString());
        double width = Double.parseDouble(etWidth.getText().toString());
        double Height =Double.parseDouble(getheight.getText().toString());
        String kind = spRoomKind.getSelectedItem().toString();
        room a = new room(length, width, Height,kind);
        double ledWatt = room.Ledwatt(a);
        int shade = room.getShade(a);
        int Angle =room.getAngle(a);
        String massege =lamphanging(a);
        displayCalculationResult(ledWatt, shade,Angle,massege);
    }


    private void matching() {
        double length = Double.parseDouble(etLength.getText().toString());
        double width = Double.parseDouble(etWidth.getText().toString());
        double height = Double.parseDouble(getheight.getText().toString());
        String kind = spRoomKind.getSelectedItem().toString();
        room a = new room(length, width, height, kind);
        String userId = mAuth.getCurrentUser().getUid();

        List<Task<DocumentSnapshot>> lampTasks = new ArrayList<>();

        db.collection("Users").document(userId).collection("roomlamps").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String lampUid = documentSnapshot.getString("uid");
                        Task<DocumentSnapshot> lampTask = db.collection("roomlamps").document(lampUid).get();
                        lampTasks.add(lampTask);
                    }

                    Tasks.whenAllSuccess(lampTasks)
                            .addOnSuccessListener(taskSnapshots -> {
                                List<Lamp> lampList = new ArrayList<>();

                                for (Object snapshot : taskSnapshots) {
                                    DocumentSnapshot lampDocumentSnapshot = (DocumentSnapshot) snapshot;
                                    if (lampDocumentSnapshot.exists()) {
                                        double wattage = lampDocumentSnapshot.getDouble("wattage");
                                        int shade = lampDocumentSnapshot.getLong("shade").intValue();
                                        String name = lampDocumentSnapshot.getString("name");
                                        Lamp lamp = new Lamp(wattage, name, shade);
                                        lampList.add(lamp);
                                    }
                                }

                                if (Suitablelamps(a, lampList)) {
                                    showMessage("Lamps match the room");
                                } else {
                                    showMessage("Lamps DON'T match the room");
                                }
                            })
                            .addOnFailureListener(e -> {
                                showMessage("Error retrieving lamp details: " + e.getMessage());
                            });

                })
                .addOnFailureListener(e -> {
                    showMessage("Error retrieving room lamps: " + e.getMessage());
                });
    }







    private void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    




    private void displayCalculationResult(double ledWatt, int shade,int Angle,String massege) {

        String shadeMessage = "Shade: " + shade+"k";
        String AngleMassage ="Angle: " + Angle+"°";
        tvLedWatt.setText(String.valueOf(ledWatt));
        tvShade.setText(shadeMessage);
        tvAngle.setText(AngleMassage);
        setheight.setText(massege);
    }

}
