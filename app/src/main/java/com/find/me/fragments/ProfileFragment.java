package com.find.me.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.find.me.R;
import com.find.me.dbHelper.UsersDB;
import com.find.me.model.User;
import com.find.me.utils.Preferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
 EditText phone, iqama,password;
 TextView emailad, dobad,update;
    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        UsersDB db = new UsersDB(getActivity());
        phone = view.findViewById(R.id.pno);
        iqama = view.findViewById(R.id.iqamano);
        update = view.findViewById(R.id.updateuser);
        password = view.findViewById(R.id.pass);
        emailad = view.findViewById(R.id.email);
        dobad = view.findViewById(R.id.dob);
        User user =db.getUser(Preferences.readString(getContext(),"username"));
        emailad.setText(""+user.getUsername());
        phone.setText(""+user.getPhone_number());
        password.setText(""+user.getPassword());
        dobad.setText(""+user.getDOB());
        iqama.setText(""+user.getIqama());
        Log.i("tariq", "onViewCreated: "+"iqama "+user.getIqama() +"dob "+user.getDOB()+"pas "+user.getPassword());
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!emailad.getText().toString().isEmpty() && ! password.getText().toString().isEmpty() && ! iqama.getText().toString().isEmpty() && ! dobad.getText().toString().isEmpty() && ! phone.getText().toString().isEmpty()){
                    UsersDB db = new UsersDB(getActivity());
                    User user = new User(emailad.getText().toString(),password.getText().toString(),iqama.getText().toString(),dobad.getText().toString(),phone.getText().toString());
                    db.updateuser(user);
                    Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    getActivity().finishAffinity();
                }
                else {
                    Toast.makeText(getActivity(), "Can't miss fields", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}