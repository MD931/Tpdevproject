package com.tpdevproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tpdevproject.Utils.SnackBarUtils;
import com.tpdevproject.adapters.Holder;
import com.tpdevproject.models.Annonce;
import com.tpdevproject.models.Commentaire;
import com.tpdevproject.models.Database;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    private final static String TAG = "DetailActivity";
    public final static String ID_DEAL = "id_deal";

    private Toolbar toolbar;
    private FirebaseUser user;
    private ImageView toolbarImage, share, favoris, addComment, link, map;
    private TextView title, description, price_deal, price, euroPercent
            ,username, score, votePlus, voteMinus, datePost1, datePost2,
            dateBegin, dateEnd, comment, no_comment;
    private RecyclerView recyclerView;
    private LinearLayout holderBegin, holderEnd;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private DatabaseReference annonceRef, userRef;
    private String idDeal;
    private Annonce annonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        idDeal = handleIntent(getIntent());
        initializeVars();
    }

    @Override
    public void onStart(){
        Log.i(TAG, "onStart");
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
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
        price_deal = (TextView) findViewById(R.id.price_deal);
        price = (TextView) findViewById(R.id.price);
        euroPercent = (TextView) findViewById(R.id.euro_percent_deal);
        username = (TextView) findViewById(R.id.detail_username);
        score = (TextView) findViewById(R.id.item_score);
        votePlus = (TextView) findViewById(R.id.vote_add);
        voteMinus = (TextView) findViewById(R.id.vote_minus);
        holderBegin = (LinearLayout) findViewById(R.id.holder_date_begin);
        holderEnd = (LinearLayout) findViewById(R.id.holder_date_end);
        datePost1 = (TextView) findViewById(R.id.detail_date_post1);
        datePost2 = (TextView) findViewById(R.id.detail_date_post2);
        dateBegin = (TextView) findViewById(R.id.detail_date_begin);
        dateEnd = (TextView) findViewById(R.id.detail_date_end);
        no_comment = (TextView) findViewById(R.id.detail_no_comment);
        addComment = (ImageView) findViewById(R.id.detail_btn_comment);
        link = (ImageView) findViewById(R.id.link);
        map = (ImageView) findViewById(R.id.map);
        comment = (TextView) findViewById(R.id.detail_comment);
        if (idDeal == null) idDeal = getIntent().getExtras().getString(ID_DEAL);
        annonce = null;
        toolbar.setTitle("Deal");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ViewCompat.setTransitionName(findViewById(R.id.appBarLayout), "Name");

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        annonceRef = FirebaseDatabase.getInstance().getReference().child("annonce");
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        share = (ImageView) findViewById(R.id.share);
        favoris = (ImageView) findViewById(R.id.favoris);
        bindRecyclerView();

    }

    private void initializeListeners() {
        annonceRef.child(idDeal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bindDataOnView(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled : " + databaseError.toString());
            }
        });

        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(annonce.getImage()!=null){
                    String url = annonce.getImage();
                    Intent intent = new Intent(getApplicationContext(), ShowImageActivity.class);
                    intent.putExtra(ShowImageActivity.URL_IMAGE, url);
                    startActivity(intent);
                }
            }
        });

        votePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Log.i(TAG, "VotePlus, onClick");
                    if (!annonce.getVotes().containsKey(user.getUid())) {
                        annonceRef.child(annonce.getId()).child("votes")
                                .child(user.getUid()).setValue(1);
                        annonceRef.child(annonce.getId()).child("order")
                                .setValue(annonce.getOrder() - 1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login before", Toast.LENGTH_SHORT).show();
                }
            }
        });

        voteMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {

                    Log.i(TAG, "VoteMinus, onClick");
                    if (!annonce.getVotes().containsKey(user.getUid())) {
                        annonceRef.child(annonce.getId()).child("votes")
                                .child(user.getUid()).setValue(-1);
                        annonceRef.child(annonce.getId()).child("order")
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
                Log.i(TAG, "Share, onClick");
                share();
            }
        });

        favoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (user != null) {
                    Log.i(TAG, "Favoris, onClick");
                    if (annonce.getFavoris().containsKey(user.getUid())) {
                        if (annonce.getFavoris().get(user.getUid()) == 1) {
                            annonceRef.child(annonce.getId()).child(Database.COLUMN_FAVORIS)
                                    .child(user.getUid()).setValue(0, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError==null) {
                                        SnackBarUtils.showSnackBarMessage(findViewById(R.id.id_detail_activity)
                                                , getResources()
                                                        .getString(R.string.remove_from_favorite));
                                    }
                                }
                            });
                        } else {
                            annonceRef.child(annonce.getId()).child(Database.COLUMN_FAVORIS)
                                    .child(user.getUid()).setValue(1, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError==null) {
                                        SnackBarUtils.showSnackBarMessage(findViewById(R.id.id_detail_activity)
                                                , getResources()
                                                        .getString(R.string.add_to_favorite));
                                    }
                                }
                            });
                        }
                    } else {
                        annonceRef.child(annonce.getId()).child(Database.COLUMN_FAVORIS)
                                .child(user.getUid()).setValue(1, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError==null) {
                                    SnackBarUtils.showSnackBarMessage(findViewById(R.id.id_detail_activity)
                                            , getResources()
                                                    .getString(R.string.add_to_favorite));
                                }
                            }
                        });
                    }
                } else {
                    SnackBarUtils.showSnackBarLogin(findViewById(R.id.id_detail_activity), getApplicationContext());
                }
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    if (TextUtils.isEmpty(comment.getText())) {
                        return;
                    }
                    Map<String, Object> val = generateMapOfField();
                    DatabaseReference tmp = annonceRef.child(idDeal).child(Database.COLUMN_COMMENTAIRES)
                            .push();
                    tmp.setValue(val);
                    comment.setText("");
                }else {
                    SnackBarUtils.showSnackBarLogin(view, getApplicationContext());
                }
            }
        });
    }


    private HashMap<String, Object> generateMapOfField() {
        HashMap<String, Object> value = new HashMap<>();

        value.put(Database.COLUMN_USER_ID,user.getUid());

        value.put(Database.COLUMN_DATE_POST,
                ServerValue.TIMESTAMP);

        value.put(Database.COLUMN_COMMENTAIRE, comment.getText().toString());

        return value;
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
        //annonce = Parser.parseAnnonce(dataSnapshot.getKey(), dataSnapshot.getValue(JSONObject.class));
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
        price_deal.setText(annonce.getPriceDeal().toString());
        if(annonce.getPriceDeal()<0){
            euroPercent.setText("%");
        }
        if(annonce.getPrice() != null){
            price.setText(annonce.getPrice().toString()+" €");
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            price.setVisibility(View.VISIBLE);
        }

        if(annonce.getLink() != null){
            link.setVisibility(View.VISIBLE);
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse(annonce.getLink());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    startActivity(mapIntent);
                }
            });
        }
        if(annonce.getAddress() != null){
            map.setVisibility(View.VISIBLE);
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q="+annonce.getAddress());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        }

        score.setText(String.valueOf(annonce.getOrder() * -1));

        /* IMAGE */
        if (annonce.getImage() != null)
            picassoLoader(getApplicationContext(), toolbarImage,
                    annonce.getImage());

        if (user != null) {
            if (annonce.getVotes().containsKey(user.getUid())) {
                if (annonce.getVotes().get(user.getUid()) == 1) {
                    setVotedPlus();
                } else {
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMM dd HH:mm");
        datePost1.setText(formatter.format(new Date(annonce.getDatePost() * -1)));
        datePost2.setText(formatter.format(new Date(annonce.getDatePost() * -1)));
        formatter = new SimpleDateFormat("yyyy MMM dd");
        if (annonce.getDateBegin() != null) {
            String[] date = annonce.getDateBegin().split("/");
            dateBegin.setText(formatter.format(new Date(date[1] + "/" + date[0] + "/" + date[2])));
        } else {
            holderBegin.setVisibility(View.GONE);
        }
        if (annonce.getDateEnd() != null) {
            String[] date = annonce.getDateEnd().split("/");
            Log.i(TAG, date[0] + " " + date[1] + " " + date[2]);
            dateEnd.setText(formatter.format(new Date(date[1] + "/" + date[0] + "/" + date[2])));
        } else {
            holderEnd.setVisibility(View.GONE);
        }
        /* FIN DATE */

    }

    private void bindRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.info_comments);
        recyclerView.setHasFixedSize(true);
        DatabaseReference dbRef = annonceRef.child(idDeal).child("commentaires");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "addListenerForSingleValueEvent onDataChange : "+dataSnapshot.getChildrenCount());
                if(dataSnapshot.getChildrenCount()>0){
                    recyclerView.setVisibility(View.VISIBLE);
                    no_comment.setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    no_comment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final FirebaseRecyclerAdapter<Commentaire,Holder.CommentaireViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<Commentaire,Holder.CommentaireViewHolder>(
                Commentaire.class,
                R.layout.item_comment,
                Holder.CommentaireViewHolder.class,
                dbRef
        ) {
            @Override
            protected Commentaire parseSnapshot(DataSnapshot snapshot) {
                Log.i(TAG, "parseSnapshot");
                final Commentaire commentaire = super.parseSnapshot(snapshot);
                if (commentaire != null){
                    Log.i(TAG, "parseSnapshot : "+commentaire.toString());
                    //commentaire.setId(snapshot.getKey());
                }
                return commentaire;
            }

            @Override
            protected void populateViewHolder(final Holder.CommentaireViewHolder viewHolder, final Commentaire model, int position) {
                Log.i(TAG, position+"");
                Log.i(TAG, model.toString());
                userRef.child(model.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(TAG, "onDataChange :"+dataSnapshot.child("username"));
                        viewHolder.setUsername(dataSnapshot.child("username").getValue().toString());
                        if(dataSnapshot.hasChild(Database.COLUMN_IMAGE_PROFILE))
                            viewHolder.setImage(getApplicationContext(),
                                    dataSnapshot.child(Database.COLUMN_IMAGE_PROFILE).getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG, "oncCancelled ValueEventListener");
                    }
                });
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMM dd HH:mm");
                viewHolder.setDatePost(formatter.format(new Date(model.getDatePost())));
                viewHolder.setCommentaire(model.getCommentaire());
            }
        };
        RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                Log.i(TAG, "onItemRangeInserted : "+recyclerAdapter.getItemCount());
                if(recyclerAdapter.getItemCount()>0){
                    recyclerView.setVisibility(View.VISIBLE);
                    no_comment.setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    no_comment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                Log.i(TAG, "onItemRangeRemoved : "+recyclerAdapter.getItemCount());
                if(recyclerAdapter.getItemCount()>0){
                    recyclerView.setVisibility(View.VISIBLE);
                    no_comment.setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    no_comment.setVisibility(View.VISIBLE);
                }
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.registerAdapterDataObserver(mObserver);
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
        voteMinus.setEnabled(false);
        votePlus.setTextColor(getResources().getColor(android.R.color.white));
        votePlus.setBackground(getResources().getDrawable(R.drawable.circle_red));
    }

    public void setVotedMinus() {
        votePlus.setEnabled(false);
        voteMinus.setTextColor(getResources().getColor(android.R.color.white));
        voteMinus.setBackground(getResources().getDrawable(R.drawable.circle_blue));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
