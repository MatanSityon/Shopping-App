package com.example.shoppingapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shoppingapp.R;
import com.example.shoppingapp.activities.MainActivity;
import com.example.shoppingapp.classes.EmailValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_registration_screen, container, false);
        Button completeRegisterBtn = view.findViewById(R.id.RegButton);
        Button loginBtn = view.findViewById(R.id.BackToLoginBtn);

        completeRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText regEmailEditText = view.findViewById(R.id.reg_TextEmail);
                EditText regPassEditText = view.findViewById(R.id.reg_TextPassword);
                EditText regRePassEditText = view.findViewById(R.id.reg_ReEnterPassword);
                EditText regFullNameEditText = view.findViewById(R.id.reg_FullName);
                String regEmail = regEmailEditText.getText().toString();
                String regPass = regPassEditText.getText().toString();
                String regRePass = regRePassEditText.getText().toString();
                String regFullName = regFullNameEditText.getText().toString();
                if (!(regEmail.isEmpty() && regPass.isEmpty() && regRePass.isEmpty() && regFullName.isEmpty())){
                    if(regPass.equals(regRePass)) {
                        if(EmailValidator.isValidEmail(regEmail)) {
                            if(regPass.length()>=6) {
                                MainActivity mainActivity = (MainActivity) getActivity();
                                mainActivity.register(v);

                            }
                            else {
                                Toast.makeText(requireContext(), "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(requireContext(), "Email format is not valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), "Passwords should be the same", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(requireContext(), "All fields should be filled", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;
    }


}