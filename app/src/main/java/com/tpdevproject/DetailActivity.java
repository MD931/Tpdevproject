package com.tpdevproject;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tpdevproject.models.Database;

public class DetailActivity extends AppCompatActivity {
    private final static String TAG = "DetailActivity";
    public final static String ID_DEAL = "id_deal";

    private Toolbar toolbar;
    private ImageView toolbarImage;
    private TextView title;
    private TextView description;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private DatabaseReference refAnnonce;
    private String idDeal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initializeVars();
        initializeListeners();
    }

    private void initializeVars() {
        toolbar =   (Toolbar) findViewById(R.id.toolbar);
        toolbarImage = (ImageView) findViewById(R.id.image_toolbar);
        title = (TextView) findViewById(R.id.detail_title);
        description = (TextView) findViewById(R.id.detail_description);
        idDeal = getIntent().getExtras().getString(ID_DEAL);
        toolbar.setTitle("Deal");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ViewCompat.setTransitionName(findViewById(R.id.appBarLayout), "Name");

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        refAnnonce = FirebaseDatabase.getInstance().getReference().child("annonce");
    }

    private void initializeListeners(){
        refAnnonce.child(idDeal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bindDataOnView(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled : "+databaseError.toString());
            }
        });
    }

    private void bindDataOnView(DataSnapshot dataSnapshot){
        Log.i(TAG, "bindDataOnView");
        if(dataSnapshot.hasChild(Database.COLUMN_IMAGES))
            picassoLoader(getApplicationContext(), toolbarImage,
                dataSnapshot.child(Database.COLUMN_IMAGES)
                        .child(Database.COLUMN_THUMBNAIL).getValue().toString());

        title.setText(dataSnapshot
                .child(Database.COLUMN_TITLE).getValue().toString());
        description.setText(dataSnapshot
                .child(Database.COLUMN_DESCRIPTION).getValue().toString());
    }

    private void picassoLoader(Context context, ImageView imageView, String url){
        Log.i(TAG, "picassoLoader");
        Picasso.with(context)
                .load(url)
                //.resize(30,30)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
