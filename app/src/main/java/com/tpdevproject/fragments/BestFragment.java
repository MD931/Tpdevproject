package com.tpdevproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tpdevproject.utils.GlobalVars;
import com.tpdevproject.activities.DetailActivity;
import com.tpdevproject.R;
import com.tpdevproject.utils.DateTimeUtils;
import com.tpdevproject.utils.SnackBarUtils;
import com.tpdevproject.entities.Deal;
import com.tpdevproject.adapters.Holder.DealViewHolder;


/*
    Fragment for best Deal
 */
public class BestFragment extends Fragment {
    private final static String TAG = "BestFragment";

    private DatabaseReference annonceRef;
    private FirebaseUser user;

    public BestFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        View v  = inflater.inflate(R.layout.fragment_new, container, false);
        initializeVars();
        bindRecyclerView(v);
        return v;
    }

    /*
        Initialisation des variables
     */
    private void initializeVars(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        annonceRef= FirebaseDatabase.getInstance().getReference(GlobalVars.TABLE_DEALS);
    }

    /*
        bind des données dans le recyclerView
    */
    private void bindRecyclerView(View v){
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycle_annonce);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerAdapter<Deal,DealViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<Deal,DealViewHolder>(
                Deal.class,
                R.layout.item,
                DealViewHolder.class,
                annonceRef.orderByChild(GlobalVars.COLUMN_ORDER)
        ) {
            @Override
            protected Deal parseSnapshot(DataSnapshot snapshot) {
                Log.i(TAG, "parseSnapshot");
                final Deal deal = super.parseSnapshot(snapshot);
                if (deal != null){
                    deal.setId(snapshot.getKey());

                    //Image
                    if(snapshot.hasChild(GlobalVars.COLUMN_IMAGES))
                        deal.setImage(snapshot.child(GlobalVars.COLUMN_IMAGES)
                                .child(GlobalVars.COLUMN_THUMBNAIL).getValue().toString());
                    //Votes
                    if(snapshot.hasChild(GlobalVars.COLUMN_ORDER))
                        deal.setScore(
                                Integer.parseInt(
                                        snapshot.child(GlobalVars.COLUMN_ORDER).getValue().toString())*-1);

                    //Commentaires
                    if(snapshot.hasChild(GlobalVars.COLUMN_COMMENTAIRES)){
                        deal.setNumberCommentaires(snapshot.child(GlobalVars.COLUMN_COMMENTAIRES).getChildrenCount());
                    }
                }
                return deal;
            }

            /*
                bind des données dans la vue
            */
            @Override
            protected void populateViewHolder(final DealViewHolder viewHolder, final Deal model, int position) {
                Log.i(TAG, position+"");
                Log.i(TAG, model.toString());

                /*
                    Récuperer les infos de l'utilisateur du deal
                 */
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(GlobalVars.TABLE_USERS)
                        .child(model.getUserId());
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(TAG, "onDataChange :"+dataSnapshot.child(GlobalVars.COLUMN_USERNAME));
                        viewHolder.setUsername(dataSnapshot.child(GlobalVars.COLUMN_USERNAME).getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG, "oncCancelled ValueEventListener");
                    }
                });

                /*
                    bind des différentes données
                 */
                viewHolder.setTitle(model.getTitle());
                viewHolder.setPriceDeal(model.getPriceDeal());
                if(model.getPrice() != null)
                    viewHolder.setPrice(model.getPrice());
                viewHolder.setScore(model.getScore());
                viewHolder.setNumberComs(model.getNumberCommentaires());
                if(model.getImage() != null)
                    viewHolder.setImage(getContext(), model.getImage());
                viewHolder.setTimeElapsed(
                        DateTimeUtils.elapsedTimes(model.getDatePost()*-1
                                ,System.currentTimeMillis()));

                /*
                    Clique sur un item
                 */
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mIntent = new Intent(getContext(), DetailActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString(DetailActivity.ID_DEAL, model.getId());
                        mIntent.putExtras(mBundle);
                        startActivity(mIntent);
                    }
                });

                /*
                    Vérifier si l'utilisateur a voté pour le deal
                 */
                if(user!=null && model.getVotes().containsKey(user.getUid()))
                        if (model.getVotes().get(user.getUid()) == 1){
                            viewHolder.setVotedPlus();
                        }else{
                            viewHolder.setVotedMinus();
                        }

                /*
                    Clique sur vote plus
                 */
                viewHolder.textView_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(user != null){
                            Log.i(TAG,"votes positif");
                            if(!model.getVotes().containsKey(user.getUid())) {
                                annonceRef.child(model.getId()).child(GlobalVars.COLUMN_VOTES)
                                        .child(user.getUid()).setValue(1);
                                annonceRef.child(model.getId()).child(GlobalVars.COLUMN_ORDER)
                                        .setValue(model.getOrder()-1);
                            }
                        }else{
                            SnackBarUtils.showSnackBarLogin(view, getContext());
                        }
                    }
                });

                /*
                    Clique sur vote moins
                 */
                viewHolder.textView_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(user != null){
                            Log.i(TAG,"votes négatif");
                            if(!model.getVotes().containsKey(user.getUid())) {
                                annonceRef.child(model.getId()).child(GlobalVars.COLUMN_VOTES)
                                        .child(user.getUid()).setValue(-1);
                                annonceRef.child(model.getId()).child(GlobalVars.COLUMN_ORDER)
                                        .setValue(model.getOrder()+1);
                            }
                        }else{
                            SnackBarUtils.showSnackBarLogin(view, getContext());
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
    }
}