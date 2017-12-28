package com.tpdevproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tpdevproject.DetailActivity;
import com.tpdevproject.R;
import com.tpdevproject.Utils.DateTimeUtils;
import com.tpdevproject.models.Annonce;
import com.tpdevproject.models.Database;
import com.tpdevproject.adapters.Holder.AnnonceViewHolder;


public class CategoryFragment extends Fragment {
    private final static String TAG = "CategoryFragment";

    private RecyclerView recyclerView;
    private DatabaseReference annonceRef;
    private FirebaseUser user;

    public CategoryFragment() {
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

    private void initializeVars(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        annonceRef= FirebaseDatabase.getInstance().getReference("annonce");
    }

    private void bindRecyclerView(View v){
        recyclerView = (RecyclerView) v.findViewById(R.id.recycle_annonce);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerAdapter<Annonce,AnnonceViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<Annonce,AnnonceViewHolder>(
                Annonce.class,
                R.layout.item,
                AnnonceViewHolder.class,
                annonceRef.orderByChild(Database.COLUMN_ORDER)
        ) {
            @Override
            protected Annonce parseSnapshot(DataSnapshot snapshot) {
                final Annonce annonce = super.parseSnapshot(snapshot);
                if (annonce != null){
                    annonce.setId(snapshot.getKey());

                    //Image
                    if(snapshot.hasChild(Database.COLUMN_IMAGES))
                        annonce.setImage(snapshot.child(Database.COLUMN_IMAGES)
                                .child(Database.COLUMN_THUMBNAIL).getValue().toString());
                    /*annonce.setUserId(snapshot.child(getResources().getString(R.string.column_user_id))
                            .getValue().toString());
                    /*
                    userRef.child(userId)
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            annonce.setUsername("12");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });*/

                    //annonce.setUsername(userRef.child(userId).toString());
                    //Votes
                    int votes = 0;
                    for(DataSnapshot e : snapshot.child(Database.COLUMN_VOTES)
                            .getChildren()){
                        Log.i("parseSnapshot", "id = "+annonce.getId()+", "+e.getKey());
                        votes += Integer.parseInt(e.getValue().toString());
                    }
                    annonce.setScore(votes);
                }
                Log.i(TAG, "parse : "+annonce.getUsername());
                return annonce;
            }

            @Override
            protected void populateViewHolder(final AnnonceViewHolder viewHolder, final Annonce model, int position) {
                Log.i(TAG, position+"");
                Log.i(TAG, model.toString());
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(model.getUserId());
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(TAG, "onDataChange :"+dataSnapshot.child("username"));
                        viewHolder.setUsername(dataSnapshot.child("username").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG, "oncCancelled ValueEventListener");
                    }
                });

                viewHolder.setTitle(model.getTitle());
                viewHolder.setPriceDeal(model.getPriceDeal());
                if(model.getPrice() != null)
                    viewHolder.setPrice(model.getPrice());
                viewHolder.setScore(model.getScore());
                viewHolder.setNumberComs(model.getNumberCommentaires());
                viewHolder.setImage(getContext(), model.getImage());
                viewHolder.setTimeElapsed(
                        DateTimeUtils.elapsedTimes(model.getDatePost()*-1
                                ,System.currentTimeMillis()));
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
                if(user!=null)
                    if(model.getVotes().containsKey(user.getUid())){
                        if (model.getVotes().get(user.getUid()) == 1){
                            //viewHolder.imageView_add.setBackgroundColor(getResources().getColor(android.R.color.black));
                            viewHolder.setVotedPlus();
                        }else{
                            //viewHolder.imageView_minus.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                            viewHolder.setVotedMinus();
                        }
                    }
                viewHolder.textView_score.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Clique sur score", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.textView_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(user != null){
                            Log.i("votes","add");
                            if(!model.getVotes().containsKey(user.getUid())) {
                                annonceRef.child(model.getId()).child("votes")
                                        .child(user.getUid()).setValue(1);
                                annonceRef.child(model.getId()).child("order")
                                        .setValue(model.getOrder()-1);
                            }
                        }else{
                            Toast.makeText(getContext(), "Please Login before", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.textView_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(user != null){
                            Log.i("votes","minus");
                            if(!model.getVotes().containsKey(user.getUid())) {
                                annonceRef.child(model.getId()).child("votes")
                                        .child(user.getUid()).setValue(-1);
                                annonceRef.child(model.getId()).child("order")
                                        .setValue(model.getOrder()+1);
                            }
                        }else{
                            Toast.makeText(getContext(), "Please Login before", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
    }
}