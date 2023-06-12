package com.finalproject.allabouthomeuser.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalproject.allabouthomeuser.R;

import java.util.List;



public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    Context context;
    List<myCart> list;

    public MyCartAdapter(){}

     public MyCartAdapter(Context context, List<myCart>list){
         this.context=context;
         this.list=list;
     }

    @NonNull
    @Override
    public MyCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mycart_item,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull MyCartAdapter.ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.price.setText(String.valueOf( list.get(position).getPrice()));
        holder.quantity.setText(list.get(position).getQuantity());
        holder.desc.setText(list.get(position).getDescription());
        holder.watt.setText(String.valueOf( list.get(position).getWatt()));
        holder.shade.setText(list.get(position).getShade());


    }
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
         TextView name,desc,price,watt,shade,quantity;
        public ViewHolder(@NonNull View inflate) {
            super(inflate);
            name=inflate.findViewById(R.id.textViewProductName);
            desc=inflate.findViewById(R.id.textViewProductDescription);
            price=inflate.findViewById(R.id.textViewProductPrice);
            watt=inflate.findViewById(R.id.textViewLampWatt);
            shade=inflate.findViewById(R.id.textViewLampShade);
            quantity=inflate.findViewById(R.id.textViewProductQuantity);

        }
    }


}
