package com.example.shoppingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.shoppingapp.models.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> originalItemList;
    private List<Item> filteredItemList;
    private OnPriceChangeListener onPriceChangeListener;

    public ItemAdapter(List<Item> itemList, OnPriceChangeListener listener) {
        this.originalItemList = new ArrayList<>(itemList);
        this.filteredItemList = new ArrayList<>(itemList);
        this.onPriceChangeListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = filteredItemList.get(position);

        // ✅ Ensure Buttons Do Not Disappear
        holder.btnAdd.setVisibility(View.VISIBLE);
        holder.btnRemove.setVisibility(View.VISIBLE);

        holder.itemName.setText(item.getName());
        holder.itemDescription.setText(item.getDescription());
        holder.itemPrice.setText(String.format("Price: $%.2f", item.getPrice()));
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        double totalItemPrice = item.getQuantity() * item.getPrice();
        holder.itemTotalPrice.setText(String.format("Total: $%.2f", totalItemPrice));

        holder.itemImage.setImageResource(item.getImageResource());

        holder.btnAdd.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(holder.getAdapterPosition(), "update_price");
            if (onPriceChangeListener != null) {
                onPriceChangeListener.onPriceChange();
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(holder.getAdapterPosition(), "update_price");
                if (onPriceChangeListener != null) {
                    onPriceChangeListener.onPriceChange();
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains("update_price")) {
            // ✅ Update only quantity & total price without resetting entire UI
            Item item = filteredItemList.get(position);
            holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
            holder.itemTotalPrice.setText(String.format("Total: $%.2f", item.getQuantity() * item.getPrice()));
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemCount() {
        return filteredItemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemDescription, itemPrice, itemTotalPrice, itemQuantity;
        ImageView itemImage;
        Button btnAdd, btnRemove;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemTotalPrice = itemView.findViewById(R.id.itemTotalPrice);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemImage = itemView.findViewById(R.id.itemImage);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }

    public interface OnPriceChangeListener {
        void onPriceChange();
    }

    public void filter(String query) {
        query = query.toLowerCase().trim();
        filteredItemList.clear();
        if (query.isEmpty()) {
            filteredItemList.addAll(originalItemList);
        } else {
            for (Item item : originalItemList) {
                if (item.getName().toLowerCase().contains(query) ||
                        item.getDescription().toLowerCase().contains(query)) {
                    filteredItemList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
