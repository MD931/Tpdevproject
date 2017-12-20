package com.tpdevproject.fragments;

import android.content.Context;
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
import com.tpdevproject.R;
import com.tpdevproject.models.Annonce;
import com.tpdevproject.models.Database;


public class NewFragment extends Fragment {
    private static final String TAG = "NewFragment";

    private RecyclerView recyclerView;
    private DatabaseReference annonceRef;
    private FirebaseUser user;

    public NewFragment() {
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
                annonceRef.orderByChild(Database.COLUMN_DATE_POST)
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
                //Username
                /*String userId = model.getUserId();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(userId);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        model.setUsername("12777");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
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
                viewHolder.setScore(model.getScore());
                viewHolder.setNumberComs(model.getNumberCommentaires());
                Log.i("populateViewHolder", ""+model.getUsername());
                viewHolder.setUsername("aaaaa");
                viewHolder.setImage(getContext(), model.getImage());

                viewHolder.textView_score.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Clique sur score", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.imageView_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(user != null){
                            Log.i("votes","add");
                            annonceRef.child(model.getId()).child("votes").child(user.getUid()).setValue(1);
                        }else{
                            Toast.makeText(getContext(), "Please Login before", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.imageView_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(user != null){
                            Log.i("votes","minus");
                            annonceRef.child(model.getId()).child("votes").child(user.getUid()).setValue(-1);
                        }else{
                            Toast.makeText(getContext(), "Please Login before", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
    }

    public static class AnnonceViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private ImageView view_image, imageView_minus, imageView_add;
        private TextView textView_title, textView_score;
        TextView textView_number_coms, textView_username;

        public AnnonceViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            textView_title = (TextView)itemView.findViewById(R.id.item_title);
            textView_score = (TextView)itemView.findViewById(R.id.item_score);
            textView_number_coms = (TextView)itemView.findViewById(R.id.item_number_coms);
            textView_username = (TextView)itemView.findViewById(R.id.item_username);
            view_image = (ImageView) itemView.findViewById(R.id.item_image);
            imageView_add = (ImageView) itemView.findViewById(R.id.vote_add);
            imageView_minus = (ImageView) itemView.findViewById(R.id.vote_minus);
        }
        public void setTitle(String title)
        {
            textView_title.setText(title+"");
        }
        public void setScore(int score)
        {
            textView_score.setText(score+"");
        }
        public void setNumberComs(int numberComs)
        {
            textView_number_coms.setText(numberComs+"");
        }
        public void setUsername(String username)
        {
            textView_username.setText(username);
        }
        public void setImage(Context context, String url) {
            Picasso.with(context).load(url).into(view_image);}
    }
}
