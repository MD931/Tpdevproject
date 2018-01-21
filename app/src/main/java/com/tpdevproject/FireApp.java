package com.tpdevproject;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by root on 06/12/17.
 */

public class FireApp extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
