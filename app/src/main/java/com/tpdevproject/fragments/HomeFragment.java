package com.tpdevproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tpdevproject.HomeActivity;
import com.tpdevproject.LogAuthActivity;
import com.tpdevproject.R;
import com.tpdevproject.models.User;


public class HomeFragment extends Fragment {
//    final Firebase mRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) view.findViewById(R.id.addBtn);
        //final Firebase mRef = new Firebase("https://tpdevproject.firebaseio.com/users");

        final Firebase mRef = new Firebase("https://tpdevproject.firebaseio.com/annonce");

        final EditText edt = (EditText) view.findViewById(R.id.addValue);
        final TextView txv = (TextView) view.findViewById(R.id.showValue);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        /*mRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msg = dataSnapshot.getValue(String.class);
                Log.i("HomeFragment", "Data changed");
                txv.setText(msg);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
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

                    }
                });
                mRef.child(auth.getCurrentUser().getUid()).setValue(edt.getText().toString());
            }
        });
        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create user
                auth.createUserWithEmailAndPassword(edt.getText().toString(), "123456")
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getContext(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                //progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    User u = new User("coucou123");
                                    mRef.child(task.getResult().getUser().getUid()).setValue(u);
                                    Toast.makeText(getContext(), "Authentication successful." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });*/
    }
}
