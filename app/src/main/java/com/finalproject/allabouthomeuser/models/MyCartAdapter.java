package com.finalproject.allabouthomeuser.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalproject.allabouthomeuser.R;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private Context context;
    private List<myCart> list;

    public MyCartAdapter(Context context, List<myCart> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycart_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartAdapter.ViewHolder holder, int position) {
        myCart item = list.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        holder.quantity.setText(item.getQuantity());
        holder.desc.setText(item.getDescription());
//        holder.watt.setText(String.valueOf(item.getWatt()));
//        holder.shade.setText(String.valueOf(item.getShade()));
        Glide.with(context).load(item.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, price, watt, shade, quantity;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewProductName);
            desc = itemView.findViewById(R.id.textViewProductDescription);
            price = itemView.findViewById(R.id.textViewProductPrice);
//            watt = itemView.findViewById(R.id.textViewLampWatt);
//            shade = itemView.findViewById(R.id.textViewLampShade);
            quantity = itemView.findViewById(R.id.textViewProductQuantity);
            image = itemView.findViewById(R.id.imageViewProductImage);
        }
    }
}
