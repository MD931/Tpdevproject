package com.tpdevproject;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by root on 06/12/17.
 */

public class FireApp extends Application {

    Firebase mRef;
    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://tpdevproject.firebaseio.com/");
    }

    public Firebase getMRef(){
        return mRef;
    }
}