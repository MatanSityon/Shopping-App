package com.example.shoppingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.shoppingapp.models.Item;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<Item> cartItems;
    private final RemoveItemCallback removeItemCallback;

    public CartAdapter(List<Item> cartItems, RemoveItemCallback removeItemCallback) {
        this.cartItems = cartItems;
        this.removeItemCallback = removeItemCallback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Item item = cartItems.get(position);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.format("Quantity: %d", item.getQuantity()));
        holder.itemPrice.setText(String.format("Price: $%.2f", item.getPrice()));

        holder.removeButton.setOnClickListener(v -> {
            if (removeItemCallback != null) {
                removeItemCallback.onRemoveItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemPrice;
        Button removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.cartItemName);
            itemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            itemPrice = itemView.findViewById(R.id.cartItemPrice);
            removeButton = itemView.findViewById(R.id.cartItemRemoveButton);
        }
    }

    public interface RemoveItemCallback {
        void onRemoveItem(Item item);
    }
}
