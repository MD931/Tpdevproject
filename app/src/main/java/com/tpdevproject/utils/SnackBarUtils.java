package com.tpdevproject.utils;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.tpdevproject.activities.LoginActivity;
import com.tpdevproject.R;

/**
 * Created by root on 10/01/18.
 */

public class SnackBarUtils {
    public static void showSnackBarLogin(View view, final Context c) {
        Snackbar.make(view, view.getResources().getString(R.string.login_before), Snackbar.LENGTH_LONG)
                .setAction(view.getResources().getString(R.string.login), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startLoginActivity(c);
                    }
                }).show();
    }

    private static void startLoginActivity(Context c) {
        Intent intent = new Intent(c, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }

    public static void showSnackBarMessage(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
