package com.tpdevproject.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tpdevproject.R;

public class ShowImageActivity extends AppCompatActivity {
    private final static String TAG = "ShowImageActivity";
    public final static String URL_IMAGE = "url_image";
    private ImageView image;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        image = (ImageView) findViewById(R.id.show_image);
        if (url == null) url = getIntent().getExtras().getString(URL_IMAGE);
        picassoLoader(getApplicationContext(), image,
                url);
    }

    private void picassoLoader(Context context, ImageView imageView, String url) {
        Log.i(TAG, "picassoLoader");
        Picasso.with(context)
                .load(url)
                .into(imageView);
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
