package com.finalproject.allabouthomeuser.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalproject.allabouthomeuser.R;

import java.util.List;

public class LampAdapter extends RecyclerView.Adapter<LampAdapter.LampViewHolder> {

    private List<Lamp> lampList;
    private Context context;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnAddButtonClickListener addButtonClickListener;

    public LampAdapter(List<Lamp> lampList, Context context) {
        this.lampList = lampList;
        this.context = context;
    }

    public interface OnAddButtonClickListener {
        void onAddButtonClick(Lamp lamp);
    }

    public void setOnAddButtonClickListener(OnAddButtonClickListener listener) {
        this.addButtonClickListener = listener;
    }

    @NonNull
    @Override
    public LampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lamp_layout, parent, false);
        return new LampViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LampViewHolder holder, int position) {
        Lamp lamp = lampList.get(position);

        holder.txtName.setText(lamp.getName());
        holder.txtDescription.setText(lamp.getDescription());
        holder.txtPrice.setText(String.valueOf(lamp.getPrice()));
        holder.txtAdminName.setText(lamp.getAdminName());
        holder.txtQuantity.setText(String.valueOf(lamp.getQuantity()));
        holder.txtWatt.setText(String.valueOf(lamp.getWatt()));
        holder.txtShade.setText(String.valueOf(lamp.getShade()));

        Glide.with(holder.itemView.getContext()).load(lamp.getImage()).into(holder.imgLamp);

    }

    @Override
    public int getItemCount() {
        return lampList.size();
    }

    public class LampViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName, txtDescription, txtPrice, txtAdminName, txtQuantity, txtWatt, txtShade;
        public ImageView imgLamp;
        public Button btnAdd;

        public LampViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.lampName);
            txtDescription = itemView.findViewById(R.id.lampDescription);
            txtPrice = itemView.findViewById(R.id.lampPrice);
            txtAdminName = itemView.findViewById(R.id.lampAdmin);
            txtQuantity = itemView.findViewById(R.id.lampQuantity);
            txtWatt = itemView.findViewById(R.id.lampWatt);
            txtShade = itemView.findViewById(R.id.lampShade);
            imgLamp = itemView.findViewById(R.id.lampImage);

        }
    }


}
