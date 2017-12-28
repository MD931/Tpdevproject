package com.tpdevproject;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tpdevproject.models.Annonce;
import com.tpdevproject.models.Database;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {
    private final static String TAG = "DetailActivity";
    public final static String ID_DEAL = "id_deal";

    private Toolbar toolbar;
    private FirebaseUser user;
    private ImageView toolbarImage, share, favoris;
    private TextView title, description, username, score, vote_plus,
            vote_minus, datePost1, datePost2, dateBegin, dateEnd;
    private LinearLayout holderBegin, holderEnd;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private DatabaseReference refAnnonce;
    private String idDeal;
    private Annonce annonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        idDeal = handleIntent(getIntent());
        initializeVars();
        initializeListeners();
    }

    private String handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            return appLinkData.getLastPathSegment();
        }
        return null;
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void initializeVars() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarImage = (ImageView) findViewById(R.id.image_toolbar);
        title = (TextView) findViewById(R.id.detail_title);
        description = (TextView) findViewById(R.id.detail_description);
        username = (TextView) findViewById(R.id.detail_username);
        score = (TextView) findViewById(R.id.item_score);
        vote_plus = (TextView) findViewById(R.id.vote_add);
        vote_minus = (TextView) findViewById(R.id.vote_minus);
        holderBegin = (LinearLayout) findViewById(R.id.holder_date_begin);
        holderEnd = (LinearLayout) findViewById(R.id.holder_date_end);
        datePost1 = (TextView) findViewById(R.id.detail_date_post1);
        datePost2 = (TextView) findViewById(R.id.detail_date_post2);
        dateBegin = (TextView) findViewById(R.id.detail_date_begin);
        dateEnd = (TextView) findViewById(R.id.detail_date_end);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (idDeal == null) idDeal = getIntent().getExtras().getString(ID_DEAL);
        annonce = null;
        toolbar.setTitle("Deal");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ViewCompat.setTransitionName(findViewById(R.id.appBarLayout), "Name");

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        refAnnonce = FirebaseDatabase.getInstance().getReference().child("annonce");

        share = (ImageView) findViewById(R.id.share);
        favoris = (ImageView) findViewById(R.id.favoris);
    }

    private void initializeListeners() {
        refAnnonce.child(idDeal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bindDataOnView(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled : " + databaseError.toString());
            }
        });

        vote_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Log.i("votes", "add");
                    if (!annonce.getVotes().containsKey(user.getUid())) {
                        refAnnonce.child(annonce.getId()).child("votes")
                                .child(user.getUid()).setValue(1);
                        refAnnonce.child(annonce.getId()).child("order")
                                .setValue(annonce.getOrder() - 1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login before", Toast.LENGTH_SHORT).show();
                }
            }
        });

        vote_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Log.i("votes", "minus");
                    if (!annonce.getVotes().containsKey(user.getUid())) {
                        refAnnonce.child(annonce.getId()).child("votes")
                                .child(user.getUid()).setValue(-1);
                        refAnnonce.child(annonce.getId()).child("order")
                                .setValue(annonce.getOrder() + 1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login before", Toast.LENGTH_SHORT).show();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        favoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Log.i(TAG, "Favoris, onClick");
                    if (annonce.getFavoris().containsKey(user.getUid())) {
                        if (annonce.getFavoris().get(user.getUid()) == 1) {
                            refAnnonce.child(annonce.getId()).child("favoris")
                                    .child(user.getUid()).setValue(0);
                        } else {
                            refAnnonce.child(annonce.getId()).child("favoris")
                                    .child(user.getUid()).setValue(1);
                        }
                    } else {
                        refAnnonce.child(annonce.getId()).child("favoris")
                                .child(user.getUid()).setValue(1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login before", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //ici on ajoute d'autre information genre "veuillez ouvrir le lien pour découvrir un deal" + une photo
        intent.putExtra(Intent.EXTRA_TEXT, "http://bigdeal.com/deal/" + idDeal);
        startActivity(Intent.createChooser(intent, "Share !"));
    }

    private void bindDataOnView(DataSnapshot dataSnapshot) {
        Log.i(TAG, "bindDataOnView : " + dataSnapshot.toString());
        JSONObject json = null;
        //annonce = AnnonceParser.parseAnnonce(dataSnapshot.getKey(), dataSnapshot.getValue(JSONObject.class));
        annonce = dataSnapshot.getValue(Annonce.class);
        annonce.setId(dataSnapshot.getKey());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(annonce.getUserId());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange :" + dataSnapshot.child("username"));
                username.setText(dataSnapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "oncCancelled ValueEventListener");
            }
        });

        if (dataSnapshot.hasChild(Database.COLUMN_IMAGES))
            annonce.setImage(dataSnapshot.child(Database.COLUMN_IMAGES)
                    .child(Database.COLUMN_THUMBNAIL).getValue().toString());

        Log.i(TAG, "bindDataOnView : " + annonce.toString());

        title.setText(annonce.getTitle());
        description.setText(annonce.getDescription());
        score.setText(String.valueOf(annonce.getOrder() * -1));

        /* IMAGE */
        if (annonce.getImage() != null)
            picassoLoader(getApplicationContext(), toolbarImage,
                    annonce.getImage());

        if (user != null) {
            if (annonce.getVotes().containsKey(user.getUid())) {
                if (annonce.getVotes().get(user.getUid()) == 1) {
                    //viewHolder.imageView_add.setBackgroundColor(getResources().getColor(android.R.color.black));
                    setVotedPlus();
                } else {
                    //viewHolder.imageView_minus.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                    setVotedMinus();
                }
            }

            if(annonce.getFavoris().containsKey(user.getUid())){
                if(annonce.getFavoris().get(user.getUid()) == 1){
                    favoris.setImageDrawable(getResources().getDrawable(R.mipmap.ic_is_favorite));
                }else{
                    favoris.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite));
                }
            }
        }



        /* DATE */
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMM dd hh:mm");
        datePost1.setText(formatter.format(new Date(annonce.getDatePost() * -1)));
        datePost2.setText(formatter.format(new Date(annonce.getDatePost() * -1)));
        formatter = new SimpleDateFormat("yyyy MMM dd");
        if (annonce.getDateBegin() != null) {
            String[] date = annonce.getDateBegin().split("/");
            dateBegin.setText(formatter.format(new Date(date[1] + "/" + date[0] + "/" + date[2])));
        } else {
            holderBegin.setVisibility(View.INVISIBLE);
        }
        if (annonce.getDateEnd() != null) {
            String[] date = annonce.getDateEnd().split("/");
            Log.i(TAG, date[0] + " " + date[1] + " " + date[2]);
            dateEnd.setText(formatter.format(new Date(date[1] + "/" + date[0] + "/" + date[2])));
        } else {
            holderEnd.setVisibility(View.INVISIBLE);
        }
        /* FIN DATE */

    }

    private void picassoLoader(Context context, ImageView imageView, String url) {
        Log.i(TAG, "picassoLoader");
        Picasso.with(context)
                .load(url)
                //.resize(30,30)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }

    public void setVotedPlus() {
        vote_minus.setEnabled(false);
        vote_plus.setTextColor(getResources().getColor(android.R.color.white));
        vote_plus.setBackground(getResources().getDrawable(R.drawable.circle_red));
    }

    public void setVotedMinus() {
        vote_plus.setEnabled(false);
        vote_minus.setTextColor(getResources().getColor(android.R.color.white));
        vote_minus.setBackground(getResources().getDrawable(R.drawable.circle_blue));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
