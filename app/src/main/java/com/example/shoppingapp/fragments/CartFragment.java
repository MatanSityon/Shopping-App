package com.example.shoppingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.shoppingapp.activities.MainActivity;
import com.example.shoppingapp.adapters.CartAdapter;
import com.example.shoppingapp.models.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView totalPriceLabel;
    private MainActivity mainActivity;
    private String fullName;
    private List<Item> orderItems;
    private Button backToShoppingBtn;
    private double totalPrice;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewCart);
        totalPriceLabel = view.findViewById(R.id.totalPriceCart);

        mainActivity = (MainActivity) getActivity();
        fullName = mainActivity.getFullName();

        backToShoppingBtn = view.findViewById(R.id.backShoppingBtn);
        backToShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_cartFragment_to_shoppingScreen);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fetchOrderFromFirebase();
    }

    private void fetchOrderFromFirebase() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference("Orders")
                .child(fullName);

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    orderItems = new ArrayList<>();
                    totalPrice = 0.0;

                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        if (itemSnapshot.getKey().equals("TotalPrice")) {
                            totalPrice = itemSnapshot.getValue(Double.class);
                        } else {
                            String itemName = itemSnapshot.getKey();
                            int quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                            double price = itemSnapshot.child("price").getValue(Double.class);
                            orderItems.add(new Item(itemName, "", quantity, 0, price));
                        }
                    }

                    recyclerView.setAdapter(new CartAdapter(orderItems, CartFragment.this::removeItemFromCart));
                    totalPriceLabel.setText(String.format("Total Price: $%.2f", totalPrice));
                } else {
                    Toast.makeText(requireContext(), "No order found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to fetch order!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeItemFromCart(Item item) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference("Orders")
                .child(fullName);

        // Remove the item from Firebase
        orderRef.child(item.getName()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderItems.remove(item);
                        totalPrice -= item.getQuantity() * item.getPrice();

                        // Update the total price in Firebase
                        orderRef.child("TotalPrice").setValue(totalPrice);

                        // Update the UI
                        recyclerView.getAdapter().notifyDataSetChanged();
                        totalPriceLabel.setText(String.format("Total Price: $%.2f", totalPrice));

                        Toast.makeText(requireContext(), item.getName() + " removed from cart!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to remove item!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
