package com.example.shoppingapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.shoppingapp.R;
import com.example.shoppingapp.models.Customer;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private String userEmail;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
    }



    public void login(View v) {
        String email = ((EditText) findViewById(R.id.login_TextEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_TextPassword)).getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "login ok", Toast.LENGTH_LONG).show();
                            userEmail = email;
                            Navigation.findNavController(v).navigate(R.id.action_loginScreen_to_shoppingScreen);

                        } else {
                            Toast.makeText(MainActivity.this, "login fail", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void register(View v) {
        String email = ((EditText) findViewById(R.id.reg_TextEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.reg_TextPassword)).getText().toString();
        String fullName = ((EditText) findViewById(R.id.reg_FullName)).getText().toString();
        String address = ((EditText) findViewById(R.id.reg_Adress)).getText().toString();
        String phoneNumber = ((EditText) findViewById(R.id.reg_Phone)).getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Registration Completed", Toast.LENGTH_LONG).show();
                            addData(email, fullName,address,phoneNumber);
                            Navigation.findNavController(v).navigate(R.id.action_registrationScreen_to_loginScreen);
                        } else {
                            Toast.makeText(MainActivity.this, "Email is already exists", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void addData(String email, String fullName,String addres,String phoneNumber) {
        String sanitizedEmail = email.replace(".", "_");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(sanitizedEmail);
        Customer customer = new Customer(email, fullName,addres,phoneNumber);
        myRef.setValue(customer);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
