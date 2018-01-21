package com.tpdevproject.activities;

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
import com.tpdevproject.R;
import com.tpdevproject.utils.GlobalVars;

public class CompleteProfilActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView username;
    private Button validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        initializeVars();
        initializeListeners();
    }

    @Override
    public void onStart(){
        super.onStart();
        user = auth.getCurrentUser();
    }

    /*
        Initialisation des variables
    */
    private void initializeVars(){
        auth = FirebaseAuth.getInstance();
        validate = (Button) findViewById(R.id.setup_btn);
        username = (TextView) findViewById(R.id.setup_username);
    }

    /*
        Faire un set des listeners
    */
    private void initializeListeners(){
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(username.getText().toString())){
                    username.setError(
                            getResources().getString(R.string.username_required).toString()
                    );
                }else if(!username.getText().toString().matches("^[a-zA-Z0-9._-]{3,}$")){
                    username.setError(
                            getResources().getString(R.string.username_field_error).toString()
                    );
                }
                else{
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(GlobalVars.TABLE_USERS);
                    ref.child(user.getUid()).child(GlobalVars.COLUMN_USERNAME).setValue(username.getText().toString());
                    finish();
                }
            }
        });
    }
}
