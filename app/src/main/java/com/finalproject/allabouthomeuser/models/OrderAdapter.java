package com.finalproject.allabouthomeuser.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalproject.allabouthomeuser.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userorder, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
    public void setOrders(List<Order> updatedOrders) {
        orders.clear();
        orders.addAll(updatedOrders);
        notifyDataSetChanged();
    }
    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView textStoreName;
        private TextView textStatus;
        private RecyclerView recyclerViewItems;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textStoreName = itemView.findViewById(R.id.textStoreName);
            textStatus = itemView.findViewById(R.id.textStatus);
            recyclerViewItems = itemView.findViewById(R.id.recyclerViewItems);
        }

        public void bind(Order order) {
            textStoreName.setText(order.getStorename());
            textStatus.setText(order.isDelivered() ? "Status: Delivered" : "Status: Not Delivered");

            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            recyclerViewItems.setLayoutManager(layoutManager);

            ItemAdapter itemAdapter = new ItemAdapter(order.getItems(), false, true, true); // Pass true for showItemImage and showItemQuantity
            recyclerViewItems.setAdapter(itemAdapter);
        }

    }
}
