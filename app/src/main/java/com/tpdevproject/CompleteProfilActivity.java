package com.tpdevproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CompleteProfilActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    TextView username;
    Button validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
    }

    @Override
    public void onStart(){
        super.onStart();
        user = auth.getCurrentUser();
    }

    private void initializeVars(){
        auth = FirebaseAuth.getInstance();
        validate = (Button) findViewById(R.id.setup_btn);
        username = (TextView) findViewById(R.id.setup_username);
    }

    private void initializeListeners(){
        //Ne pas oublier de vérifier  si le champs est vide ou pas
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(username.getText().toString())){
                    username.setError("Username required");
                }else if(username.getText().toString().matches("^[a-zA-Z0-9._-]{3,}$")){
                    username.setError("Username field error");
                }
                else{
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                    ref.child(user.getUid()).child("username").setValue(username.getText().toString());
                    finish();
                }
            }
        });
    }
}
