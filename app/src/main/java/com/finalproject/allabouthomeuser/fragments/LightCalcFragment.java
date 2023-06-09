package com.finalproject.allabouthomeuser.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalproject.allabouthomeuser.models.room;
import com.finalproject.allabouthomeuser.R;

public class LightCalcFragment extends Fragment {
    private EditText etLength;
    private EditText etWidth;
    private Spinner spRoomKind;
    private Button btnCalculate;
    private TextView tvLedWatt;
    private TextView tvShade;


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

        return view;
    }


    private void calculateLedWatt() {
        double length = Double.parseDouble(etLength.getText().toString());
        double width = Double.parseDouble(etWidth.getText().toString());
        String kind = spRoomKind.getSelectedItem().toString();

        room a = new room(length, width, kind);
//        a.setLength(length);
//        a.setWidth(width);
//        a.setKind(kind);


        double ledWatt = room.Ledwatt(a); // Call Ledwatt using the class name
        int shade = room.getShade(a);

        displayCalculationResult(ledWatt, shade);
    }



    private void displayCalculationResult(double ledWatt, int shade) {
        String ledWattMessage = "LED Watt: " + ledWatt;
        String shadeMessage = "Shade: " + shade;

        tvLedWatt.setText(ledWattMessage);
        tvShade.setText(shadeMessage);
    }

}