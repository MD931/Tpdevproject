package com.tpdevproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.tpdevproject.DetailActivity;
import com.tpdevproject.R;
import com.tpdevproject.models.Annonce;

import java.util.List;

/**
 * Created by salim on 26/12/2017.
 */

public class AnnonceAdapter extends RecyclerView.Adapter<Holder.AnnonceViewHolder> {
    private List<Annonce> listAnnonce;
    private Context context;
    private FirebaseUser user;
    private DatabaseReference annonceRef;

    public AnnonceAdapter(List<Annonce> listAnnonce, Context context, FirebaseUser user, DatabaseReference annonceRef){
        this.listAnnonce = listAnnonce;
        this.context = context;
        this.user = user;
        this.annonceRef = annonceRef;
    }

    @Override
    public Holder.AnnonceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView item = (CardView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new Holder.AnnonceViewHolder(item);
    }

    @Override
    public void onBindViewHolder(Holder.AnnonceViewHolder holder,final int position) {
        holder.setTitle(listAnnonce.get(position).getTitle());
        holder.setScore(listAnnonce.get(position).getScore());
        holder.setNumberComs(listAnnonce.get(position).getNumberCommentaires());
        //holder.setUsername();
        holder.setImage(context, listAnnonce.get(position).getImage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(context, DetailActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(DetailActivity.ID_DEAL, listAnnonce.get(position).getId());
                mIntent.putExtras(mBundle);
                context.startActivity(mIntent);
            }
        });
        if(user!=null)
            if(listAnnonce.get(position).getVotes().containsKey(user.getUid())){
                if (listAnnonce.get(position).getVotes().get(user.getUid()) == 1){
                    //viewHolder.imageView_add.setBackgroundColor(getResources().getColor(android.R.color.black));
                    holder.textView_minus.setEnabled(false);
                    holder.textView_add.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.textView_add.setBackground(context.getResources().getDrawable(R.drawable.circle_red));
                }else{
                    //viewHolder.imageView_minus.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                    holder.textView_add.setEnabled(false);
                    holder.textView_minus.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.textView_minus.setBackground(context.getResources().getDrawable(R.drawable.circle_blue));
                }
            }
        holder.textView_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clique sur score", Toast.LENGTH_SHORT).show();
            }
        });
        holder.textView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null){
                    Log.i("votes","add");
                    if(!listAnnonce.get(position).getVotes().containsKey(user.getUid())) {
                        annonceRef.child(listAnnonce.get(position).getId()).child("votes")
                                .child(user.getUid()).setValue(1);
                        annonceRef.child(listAnnonce.get(position).getId()).child("order")
                                .setValue(listAnnonce.get(position).getOrder()-1);
                    }
                }else{
                    Toast.makeText(context, "Please Login before", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.textView_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null){
                    Log.i("votes","minus");
                    if(!listAnnonce.get(position).getVotes().containsKey(user.getUid())) {
                        annonceRef.child(listAnnonce.get(position).getId()).child("votes")
                                .child(user.getUid()).setValue(-1);
                        annonceRef.child(listAnnonce.get(position).getId()).child("order")
                                .setValue(listAnnonce.get(position).getOrder()+1);
                    }
                }else{
                    Toast.makeText(context, "Please Login before", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAnnonce.size();
    }
}
