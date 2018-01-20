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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tpdevproject.activities.DetailActivity;
import com.tpdevproject.R;
import com.tpdevproject.utils.DateTimeUtils;
import com.tpdevproject.entities.Deal;
import com.tpdevproject.utils.GlobalVars;

import java.util.List;

/**
 * Created by salim on 26/12/2017.
 */

public class DealAdapter extends RecyclerView.Adapter<Holder.DealViewHolder> {
    private List<Deal> listDeal;
    private Context context;
    private FirebaseUser user;
    private DatabaseReference annonceRef;

    public DealAdapter(List<Deal> listDeal, Context context, FirebaseUser user, DatabaseReference annonceRef) {
        this.listDeal = listDeal;
        this.context = context;
        this.user = user;
        this.annonceRef = annonceRef;
    }

    @Override
    public Holder.DealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView item = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new Holder.DealViewHolder(item);
    }


    @Override
    public void onBindViewHolder(final Holder.DealViewHolder holder, final int position) {
        //Jointure pour récuperer le username de l'utilisateur
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(GlobalVars.TABLE_USERS)
                .child(listDeal.get(position).getUserId());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.setUsername(dataSnapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DealAdapter", "onCancelled : "+databaseError.toString());
            }
        });


        annonceRef.child(listDeal.get(position).getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Deal deal = dataSnapshot.getValue(Deal.class);
                Log.i("DealAdapter", deal.toString());
                listDeal.get(position).setVotes(deal.getVotes());
                if (listDeal.get(position).getOrder() !=  deal.getOrder()) {
                    listDeal.get(position).setOrder(deal.getOrder());
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.setTitle(listDeal.get(position).getTitle());
        holder.setPriceDeal(listDeal.get(position).getPriceDeal());
        if (listDeal.get(position).getPrice() != null)
            holder.setPrice(listDeal.get(position).getPrice());
        holder.setScore(listDeal.get(position).getOrder()*-1);
        holder.setNumberComs(listDeal.get(position).getNumberCommentaires());
        holder.setTimeElapsed(
                DateTimeUtils.elapsedTimes(listDeal.get(position).getDatePost() * -1
                        , System.currentTimeMillis()));
        //holder.setUsername();
        if(listDeal.get(position).getImage() != null)
            holder.setImage(context, listDeal.get(position).getImage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(context, DetailActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle mBundle = new Bundle();
                mBundle.putString(DetailActivity.ID_DEAL, listDeal.get(position).getId());
                mIntent.putExtras(mBundle);
                context.startActivity(mIntent);
            }
        });
        if (user != null && listDeal.get(position).getVotes().containsKey(user.getUid()))
                if (listDeal.get(position).getVotes().get(user.getUid()) == 1) {
                    holder.setVotedPlus();
                } else {
                    holder.setVotedMinus();
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
                if (user != null) {
                    Log.i("votes", "add");
                    if (!listDeal.get(position).getVotes().containsKey(user.getUid())) {
                        annonceRef.child(listDeal.get(position).getId()).child("votes")
                                .child(user.getUid()).setValue(1);
                        annonceRef.child(listDeal.get(position).getId()).child("order")
                                .setValue(listDeal.get(position).getOrder() - 1);
                        //notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(context, "Please Login before", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.textView_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Log.i("votes", "minus");
                    if (!listDeal.get(position).getVotes().containsKey(user.getUid())) {
                        annonceRef.child(listDeal.get(position).getId()).child("votes")
                                .child(user.getUid()).setValue(-1);
                        annonceRef.child(listDeal.get(position).getId()).child("order")
                                .setValue(listDeal.get(position).getOrder() + 1);
                        //notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(context, "Please Login before", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDeal.size();
    }
}
