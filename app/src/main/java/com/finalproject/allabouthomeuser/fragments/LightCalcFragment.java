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

import java.util.List;

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
    List<Lamp> lamp ;
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
    public void setLampList(List<Lamp> lamps) {
        lamp = lamps;
    }


    private void matching() {
        double length = Double.parseDouble(etLength.getText().toString());
        double width = Double.parseDouble(etWidth.getText().toString());
        double Height = Double.parseDouble(getheight.getText().toString());
        room a = new room(length, width, Height);
     if (Suitablelamps(a,lamp))
         showMessage("lamps match thr room");
     else {
         showMessage("lamps DOESN'T match the room");

     }
      
    }

  

    private void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    




    private void displayCalculationResult(double ledWatt, int shade,int Angle,String massege) {
        //  String ledWattMessage = "LED Watt: " + ledWatt +" watt led";
        String shadeMessage = "Shade: " + shade+"k";
        String AngleMassage ="Angle: " + Angle+"°";
        tvLedWatt.setText(String.valueOf(ledWatt));
        tvShade.setText(shadeMessage);
        tvAngle.setText(AngleMassage);
        setheight.setText(massege);
    }

}
