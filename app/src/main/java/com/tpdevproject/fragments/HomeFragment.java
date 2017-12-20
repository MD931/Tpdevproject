package com.tpdevproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.tpdevproject.LogAuthActivity;
import com.tpdevproject.R;


public class HomeFragment extends Fragment {
    private static String TAG = "HomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) view.findViewById(R.id.addBtn);

        final Firebase mRef = new Firebase("https://tpdevproject.firebaseio.com/annonce");

        final EditText edt = (EditText) view.findViewById(R.id.addValue);
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        Button btnLogin = (Button) view.findViewById(R.id.goLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LogAuthActivity.class);
                startActivity(intent);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                mRef.authWithCustomToken(auth.getCurrentUser().getUid(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Toast.makeText(getContext(), "coucou", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Log.e(TAG, "onAuthenticationError : "+firebaseError.toString());
                    }
                });
                mRef.child(auth.getCurrentUser().getUid()).setValue(edt.getText().toString());
            }
        });
    }
}
