package com.tpdevproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class LogAuthActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_auth);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);*/

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        LoginFragment lf = new LoginFragment();
        transaction.add(R.id.frag_holder, lf);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void choixFragment(View view){
        Fragment newFragment = null;

        if(view == findViewById(R.id.btnLogin)){
            newFragment = new LoginFragment();
        }else if(view == findViewById(R.id.btnAuthentification)){
            newFragment = new AuthentificationFragment();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_holder, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed(){
        Log.i("onBackPressed", "start");
        finish();
    }

}
