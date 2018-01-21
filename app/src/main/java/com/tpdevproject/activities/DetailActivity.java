package com.tpdevproject.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
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
import com.tpdevproject.R;
import com.tpdevproject.utils.GlobalVars;
import com.tpdevproject.utils.SnackBarUtils;
import com.tpdevproject.adapters.Holder;
import com.tpdevproject.entities.Deal;
import com.tpdevproject.entities.Commentaire;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
    Activité pour l'affichage du deal
 */
public class DetailActivity extends AppCompatActivity {
    private final static String TAG = "DetailActivity";
    public final static String ID_DEAL = "id_deal";

    private String URL = "http://bigdeal.com/deal/";
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
    private Deal deal;

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

    /*
        Récuperer l'id deal
     */
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

    /*
        Initialisation des variables
     */
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
        deal = null;
        toolbar.setTitle(
                getResources().getString(R.string.deal)
        );
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ViewCompat.setTransitionName(findViewById(R.id.appBarLayout), "Name");

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        annonceRef = FirebaseDatabase.getInstance().getReference().child(GlobalVars.TABLE_DEALS);
        userRef = FirebaseDatabase.getInstance().getReference().child(GlobalVars.TABLE_USERS);

        share = (ImageView) findViewById(R.id.share);
        favoris = (ImageView) findViewById(R.id.favoris);
        bindRecyclerView();

    }

    /*
        Faire un set des listeners
     */
    private void initializeListeners() {

        /*
            Récuperer de firebase le deal correspondant à idDeal
         */
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

        /*
            Si on clique sur l'image on l'envoi dans l'activité
            ShowImage pour voir l'image au complet
         */
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deal.getImage()!=null){
                    String url = deal.getImage();
                    Intent intent = new Intent(getApplicationContext(), ShowImageActivity.class);
                    intent.putExtra(ShowImageActivity.URL_IMAGE, url);
                    startActivity(intent);
                }
            }
        });

        /*
            Ajouter le vote de l'utilisateur à firebase
            et mettre à jour le compteur order en lui rajoutant -1
            Soustraire 1 pour un votre positif, pour pouvoir faire un
            classement décroissant après
         */
        votePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Log.i(TAG, "VotePlus, onClick");
                    if (!deal.getVotes().containsKey(user.getUid())) {
                        annonceRef.child(deal.getId()).child(GlobalVars.COLUMN_VOTES)
                                .child(user.getUid()).setValue(1);
                        annonceRef.child(deal.getId()).child(GlobalVars.COLUMN_ORDER)
                                .setValue(deal.getOrder() - 1);
                    }
                } else {
                    Toast.makeText(getApplicationContext()
                            ,getResources().getString(R.string.login_before).toString()
                            ,Toast.LENGTH_SHORT).show();
                }
            }
        });


        /*
            Ajouter le vote de l'utilisateur à firebase
            et mettre à jour le compteur order en lui rajoutant +1
            Additionner 1 pour un votre négatif, pour pouvoir faire un
            classement décroissant après
         */
        voteMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {

                    Log.i(TAG, "VoteMinus, onClick");
                    if (!deal.getVotes().containsKey(user.getUid())) {
                        annonceRef.child(deal.getId()).child(GlobalVars.COLUMN_VOTES)
                                .child(user.getUid()).setValue(-1);
                        annonceRef.child(deal.getId()).child(GlobalVars.COLUMN_ORDER)
                                .setValue(deal.getOrder() + 1);
                    }
                } else {
                    Toast.makeText(getApplicationContext()
                            ,getResources().getString(R.string.login_before).toString()
                            ,Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
            Partage
         */
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Share, onClick");
                share();
            }
        });

        /*
            Si l'utilisateur est connecté
            Rajouter le deal à sa liste de favoris
         */
        favoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (user != null) {
                    Log.i(TAG, "Favoris, onClick");
                    if (deal.getFavoris().containsKey(user.getUid())) {
                        if (deal.getFavoris().get(user.getUid()) == 1) {
                            annonceRef.child(deal.getId()).child(GlobalVars.COLUMN_FAVORIS)
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
                            annonceRef.child(deal.getId()).child(GlobalVars.COLUMN_FAVORIS)
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
                        annonceRef.child(deal.getId()).child(GlobalVars.COLUMN_FAVORIS)
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

        /*
            Ajout d'un commentaire
         */
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    if (TextUtils.isEmpty(comment.getText())) {
                        return;
                    }
                    Map<String, Object> val = generateMapOfField();
                    DatabaseReference tmp = annonceRef.child(idDeal).child(GlobalVars.COLUMN_COMMENTAIRES)
                            .push();
                    tmp.setValue(val);
                    comment.setText("");
                }else {
                    SnackBarUtils.showSnackBarLogin(view, getApplicationContext());
                }
            }
        });
    }


    /*
        Générer une hashmap des champs
     */
    private HashMap<String, Object> generateMapOfField() {
        HashMap<String, Object> value = new HashMap<>();

        value.put(GlobalVars.COLUMN_USER_ID,user.getUid());

        value.put(GlobalVars.COLUMN_DATE_POST,
                ServerValue.TIMESTAMP);

        value.put(GlobalVars.COLUMN_COMMENTAIRE, comment.getText().toString());

        return value;
    }

    /*
        Ouvrir l'intent pour le partage
     */
    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //Rajouter le titre et une petite description
        intent.putExtra(Intent.EXTRA_TEXT, deal.getTitle()+" \n \n"+URL + idDeal);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
    }

    /*
        Binder les données du deal dans sa vue correspondante
     */
    private void bindDataOnView(DataSnapshot dataSnapshot) {
        Log.i(TAG, "bindDataOnView : " + dataSnapshot.toString());
        JSONObject json = null;

        deal = dataSnapshot.getValue(Deal.class);
        deal.setId(dataSnapshot.getKey());

        /*
            Récuperer les infos de l'utilisateur qui a posté le deal
         */
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(GlobalVars.TABLE_USERS)
                .child(deal.getUserId());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange :" + dataSnapshot.child(GlobalVars.COLUMN_USERNAME));
                username.setText(dataSnapshot.child(GlobalVars.COLUMN_USERNAME).getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled : "+databaseError.getMessage());
            }
        });

        /*
            Si y a une image on l'affiche
         */
        if (dataSnapshot.hasChild(GlobalVars.COLUMN_IMAGES))
            deal.setImage(dataSnapshot.child(GlobalVars.COLUMN_IMAGES)
                    .child(GlobalVars.COLUMN_THUMBNAIL).getValue().toString());

        /*
            On affiche les différentes infos
         */
        title.setText(deal.getTitle());
        description.setText(deal.getDescription());
        price_deal.setText(deal.getPriceDeal().toString());

        /*
            Si priceDeal est négatif c'est que c'est une promo
         */
        if(deal.getPriceDeal()<0){
            euroPercent.setText("%");
        }

        /*
            Prix avant la réduction
         */
        if(deal.getPrice() != null){
            price.setText(deal.getPrice().toString()+" €");
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            price.setVisibility(View.VISIBLE);
        }

        /*
            Lien
         */
        if(deal.getLink() != null){
            link.setVisibility(View.VISIBLE);
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse(deal.getLink());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    startActivity(mapIntent);
                }
            });
        }

        /*
            Adresse
         */
        if(deal.getAddress() != null){
            map.setVisibility(View.VISIBLE);
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q="+ deal.getAddress());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        }

        /*
            Afficher le score du deal depuis order
            - Faire un vote plus order se décremente de 1
            - Faire un vote négatif order s'incremnte de 1
            c'est pour cela que order est multiplié par -1
         */
        score.setText(String.valueOf(deal.getOrder() * -1));

        /* IMAGE */
        if (deal.getImage() != null)
            picassoLoader(getApplicationContext(), toolbarImage,
                    deal.getImage());

        if (user != null) {
            if (deal.getVotes().containsKey(user.getUid())) {
                if (deal.getVotes().get(user.getUid()) == 1) {
                    setVotedPlus();
                } else {
                    setVotedMinus();
                }
            }

            if(deal.getFavoris().containsKey(user.getUid())){
                if(deal.getFavoris().get(user.getUid()) == 1){
                    favoris.setImageDrawable(getResources().getDrawable(R.mipmap.ic_is_favorite));
                }else{
                    favoris.setImageDrawable(getResources().getDrawable(R.mipmap.ic_favorite));
                }
            }
        }



        /*
            Affichage des dates selon un format 2017 dec 27 18:30
         */
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMM dd HH:mm");
        datePost1.setText(formatter.format(new Date(deal.getDatePost() * -1)));
        datePost2.setText(formatter.format(new Date(deal.getDatePost() * -1)));
        formatter = new SimpleDateFormat("yyyy MMM dd");
        if (deal.getDateBegin() != null) {
            String[] date = deal.getDateBegin().split("/");
            dateBegin.setText(formatter.format(new Date(date[1] + "/" + date[0] + "/" + date[2])));
        } else {
            holderBegin.setVisibility(View.GONE);
        }
        if (deal.getDateEnd() != null) {
            String[] date = deal.getDateEnd().split("/");
            Log.i(TAG, date[0] + " " + date[1] + " " + date[2]);
            dateEnd.setText(formatter.format(new Date(date[1] + "/" + date[0] + "/" + date[2])));
        } else {
            holderEnd.setVisibility(View.GONE);
        }

    }

    /*
        Faire un bind de la recyclerView ou afficher les commentaires
     */
    private void bindRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.info_comments);
        recyclerView.setHasFixedSize(true);
        /*
            Récuperer la liste des commentaires
         */
        DatabaseReference dbRef = annonceRef.child(idDeal).child(GlobalVars.COLUMN_COMMENTAIRES);
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
                Log.e(TAG, "bindRecyclerView, onCancelled "+databaseError.toString());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        final FirebaseRecyclerAdapter<Commentaire,Holder.CommentaireViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<Commentaire,Holder.CommentaireViewHolder>(
                Commentaire.class,
                R.layout.item_comment,
                Holder.CommentaireViewHolder.class,
                dbRef
        ) {
            //TODO
            /* A enlever */
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
                Log.i(TAG, "populateViewHolder : "+model.toString());

                /*
                    Récuperer les infos de l'utilisateur qui a posté le commentaire
                 */
                userRef.child(model.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(TAG, "onDataChange :"+dataSnapshot.child(GlobalVars.COLUMN_USERNAME));
                        viewHolder.setUsername(dataSnapshot.child(GlobalVars.COLUMN_USERNAME).getValue().toString());
                        if(dataSnapshot.hasChild(GlobalVars.COLUMN_IMAGE_PROFILE))
                            viewHolder.setImage(getApplicationContext(),
                                    dataSnapshot.child(GlobalVars.COLUMN_IMAGE_PROFILE).getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG, "oncCancelled ValueEventListener");
                    }
                });

                /*
                    Affichage de la date et du commentaire
                 */
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMM dd HH:mm");
                viewHolder.setDatePost(formatter.format(new Date(model.getDatePost())));
                viewHolder.setCommentaire(model.getCommentaire());
            }
        };

        /*
            Observer pour lors d'un ajout et de suppression d'un commentaire
         */
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

    /*
        picassoLoader pour afficher une image dans une ImageView
     */
    private void picassoLoader(Context context, ImageView imageView, String url) {
        Log.i(TAG, "picassoLoader");
        Picasso.with(context)
                .load(url)
                .into(imageView);
    }

    /*
        Si l'utilisateur clique sur un vote positif
     */
    private void setVotedPlus() {
        voteMinus.setEnabled(false);
        votePlus.setTextColor(getResources().getColor(android.R.color.white));
        votePlus.setBackground(getResources().getDrawable(R.drawable.circle_red));
    }

    /*
        Si l'utilisateur clique sur un vote négatif
     */
    private void setVotedMinus() {
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
