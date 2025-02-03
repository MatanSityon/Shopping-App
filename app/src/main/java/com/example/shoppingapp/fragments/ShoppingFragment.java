package com.example.shoppingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
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
import com.example.shoppingapp.adapters.ItemAdapter;
import com.example.shoppingapp.models.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private TextView totalPriceLabel;
    private Button btnAddToCart;
    private ImageButton goToCart;
    private String emailUser;
    private String fullName;
    private TextView greetingText;
    private MainActivity mainActivity;

    public ShoppingFragment() {
        // Required empty public constructor
    }

    public interface FullNameCallback {
        void onFullNameRetrieved(String fullName);
        void onError(String errorMessage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            emailUser = mainActivity.getUserEmail();
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        totalPriceLabel = view.findViewById(R.id.totalPriceLabel);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        greetingText = view.findViewById(R.id.greetingText);
        goToCart = view.findViewById(R.id.btnCart);
        greetingText.setVisibility(View.INVISIBLE);

        readFullName(emailUser, new FullNameCallback() {
            @Override
            public void onFullNameRetrieved(String retrievedFullName) {
                fullName = retrievedFullName;
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        itemList = new ArrayList<>();
        itemList.add(new Item("Apple", "Fresh red apples", 0, R.drawable.apple, 1.99));
        itemList.add(new Item("Banana", "Ripe yellow bananas", 0, R.drawable.banana, 0.99));
        itemList.add(new Item("Orange", "Juicy oranges", 0, R.drawable.orange, 2.49));
        itemList.add(new Item("Milk", "1L whole milk", 0, R.drawable.milk, 1.50));
        itemList.add(new Item("Bread", "Whole-grain bread loaf", 0, R.drawable.bread, 2.20));
        itemList.add(new Item("Cheese", "Cheddar cheese block", 0, R.drawable.cheese, 3.99));
        itemList.add(new Item("Eggs", "12-pack organic eggs", 0, R.drawable.eggs, 2.75));
        itemList.add(new Item("Tomatoes", "Fresh cherry tomatoes", 0, R.drawable.tomatoes, 2.30));
        itemList.add(new Item("Potatoes", "1kg fresh potatoes", 0, R.drawable.potatoes, 1.80));
        itemList.add(new Item("Chicken", "Skinless chicken breast", 0, R.drawable.chicken, 5.50));
        itemList.add(new Item("Rice", "1kg basmati rice", 0, R.drawable.rice, 2.10));
        itemList.add(new Item("Carrots", "Fresh organic carrots", 0, R.drawable.carrots, 1.20));
        itemList.add(new Item("Yogurt", "Plain Greek yogurt", 0, R.drawable.yogurt, 1.50));
        itemList.add(new Item("Coffee", "Instant coffee jar", 0, R.drawable.coffee, 4.99));
        itemList.add(new Item("Sugar", "1kg white sugar", 0, R.drawable.sugar, 1.10));

        adapter = new ItemAdapter(itemList, this::updateTotalPrice);
        recyclerView.setAdapter(adapter);

        btnAddToCart.setOnClickListener(v -> {
            List<Item> selectedItems = new ArrayList<>();
            for (Item item : itemList) {
                if (item.getQuantity() > 0) {
                    selectedItems.add(item);
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "No items selected!", Toast.LENGTH_SHORT).show();
                return;
            }

            addOrderToFirebase(selectedItems);
        });

        goToCart.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.action_shoppingScreen_to_cartFragment)
        );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double totalPrice = calculateTotalPrice(itemList);
        totalPriceLabel.setText(String.format("Total Price: $%.2f", totalPrice));
    }

    private double calculateTotalPrice(List<Item> items) {
        double total = 0.0;
        for (Item item : items) {
            total += item.getQuantity() * item.getPrice();
        }
        return total;
    }

    public void readFullName(String emailUser, FullNameCallback callback) {
        String sanitizedEmail = emailUser.replace(".", "_");
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(sanitizedEmail);

        if (greetingText != null) {
            greetingText.setVisibility(View.GONE);
        }

        userRef.child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String retrievedFullName = dataSnapshot.getValue(String.class);
                    callback.onFullNameRetrieved(retrievedFullName);

                    if (greetingText != null) {
                        greetingText.setText("Hello, " + retrievedFullName);
                        greetingText.setVisibility(View.VISIBLE);
                        mainActivity.setFullName(retrievedFullName);
                    }
                } else {
                    callback.onError("Full name does not exist.");

                    if (greetingText != null) {
                        greetingText.setText("Hello, Guest");
                        greetingText.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());

                if (greetingText != null) {
                    greetingText.setText("Error loading name");
                    greetingText.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void addOrderToFirebase(List<Item> selectedItems) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                .getReference("Orders").child(fullName);

        Map<String, Object> orderDetails = new HashMap<>();
        for (Item item : selectedItems) {
            Map<String, Object> itemDetails = new HashMap<>();
            itemDetails.put("quantity", item.getQuantity());
            itemDetails.put("price", item.getPrice());
            orderDetails.put(item.getName(), itemDetails);
        }
        orderDetails.put("TotalPrice", calculateTotalPrice(selectedItems));

        ordersRef.setValue(orderDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to place order!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
