package com.tpdevproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetupActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        auth = FirebaseAuth.getInstance();
        Button setupbtn = (Button) findViewById(R.id.setup_btn);
        final TextView tv = (TextView) findViewById(R.id.setup_username);
        //Ne pas oublier de vérifier  si le champs est vide ou pas
        setupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                ref.child(user.getUid()).child("username").setValue(tv.getText().toString());
                finish();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        user = auth.getCurrentUser();
    }
}
