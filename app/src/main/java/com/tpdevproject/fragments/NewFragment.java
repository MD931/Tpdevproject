package com.tpdevproject.fragments;

import android.content.Context;
import android.media.Image;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.tpdevproject.R;
import com.tpdevproject.models.Annonce;


public class NewFragment extends Fragment {
    RecyclerView recyclerView;
    private DatabaseReference myRef;
    private FirebaseUser user;
    public NewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("NewFragment","onCreateView");
        View v  = inflater.inflate(R.layout.fragment_new, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        bindRecyclerView(v);
        return v;
    }

    public void bindRecyclerView(View v){
        recyclerView=(RecyclerView)v.findViewById(R.id.recycle_annonce);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myRef= FirebaseDatabase.getInstance().getReference("annonce");
        FirebaseRecyclerAdapter<Annonce,AnnonceViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<Annonce,AnnonceViewHolder>(
                Annonce.class,
                R.layout.item,
                AnnonceViewHolder.class,
                myRef.orderByChild("datePost")
        ) {
            @Override
            protected Annonce parseSnapshot(DataSnapshot snapshot) {
                Annonce annonce = super.parseSnapshot(snapshot);
                if (annonce != null){
                    annonce.setId(snapshot.getKey());
                }
                if(snapshot.hasChild("images"))
                    annonce.setImage(snapshot.child("images").child("thumbnail").getValue().toString());
                int score = 0;
                for(DataSnapshot e : snapshot.child("votes").getChildren()){
                    Log.i("parseSnapshot", "id = "+annonce.getId()+", "+e.getKey());
                    score += Integer.parseInt(e.getValue().toString());
                }
                annonce.setScore(score);
                return annonce;
            }

            @Override
            protected void populateViewHolder(AnnonceViewHolder viewHolder, final Annonce model, int position) {
                Log.i("populateViewHolder", position+"");
                viewHolder.setTitle(model.getTitle());
                viewHolder.setScore(model.getScore());
                viewHolder.setNumberComs(model.getNumberComs());
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
                            myRef.child(model.getId()).child("votes").child(user.getUid()).setValue(1);
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
                            myRef.child(model.getId()).child("votes").child(user.getUid()).setValue(-1);
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
        View mView;
        ImageView view_image;
        TextView textView_title;
        TextView textView_score;
        TextView textView_number_coms;
        ImageView imageView_minus;
        ImageView imageView_add;
        public AnnonceViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            textView_title = (TextView)itemView.findViewById(R.id.item_title);
            textView_score=(TextView)itemView.findViewById(R.id.item_score);
            textView_number_coms=(TextView)itemView.findViewById(R.id.item_number_coms);
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
        public void setImage(Context context, String url) {
            Picasso.with(context).load(url).into(view_image);}
    }
}
