package com.tpdevproject.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.tpdevproject.models.Annonce;
import com.tpdevproject.models.Commentaire;

import java.util.List;

/**
 * Created by root on 13/01/18.
 */

public class CommentaireAdapter extends RecyclerView.Adapter<Holder.CommentaireViewHolder> {
    private List<Commentaire> listCommentaire;
    private Context context;
    private FirebaseUser user;
    private DatabaseReference annonceRef;

    public CommentaireAdapter(List<Commentaire> listCommentaire, Context context, FirebaseUser user, DatabaseReference annonceRef) {
        this.listCommentaire = listCommentaire;
        this.context = context;
        this.user = user;
        this.annonceRef = annonceRef;
    }

    @Override
    public Holder.CommentaireViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(Holder.CommentaireViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
