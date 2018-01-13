package com.tpdevproject.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tpdevproject.R;

/**
 * Created by salim on 26/12/2017.
 */

public class Holder {
    public static class AnnonceViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ImageView view_image;
        public TextView textView_title, textView_score, textView_time_elapsed,
                textView_number_coms, textView_price_deal, textView_price,
                textView_username, textView_minus, textView_add;

        public AnnonceViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            textView_title = (TextView)itemView.findViewById(R.id.item_title);
            textView_price_deal = (TextView) itemView.findViewById(R.id.price_deal);
            textView_price = (TextView) itemView.findViewById(R.id.price);
            textView_price.setVisibility(View.INVISIBLE);
            textView_score = (TextView)itemView.findViewById(R.id.item_score);
            textView_number_coms = (TextView)itemView.findViewById(R.id.item_number_coms);
            textView_username = (TextView)itemView.findViewById(R.id.item_username);
            view_image = (ImageView) itemView.findViewById(R.id.item_image);
            textView_add = (TextView) itemView.findViewById(R.id.vote_add);
            textView_minus = (TextView) itemView.findViewById(R.id.vote_minus);
            textView_time_elapsed = (TextView) itemView.findViewById(R.id.item_time_elapsed);
        }

        public void setTitle(String title)
        {
            textView_title.setText(title);
        }
        public void setPriceDeal(Double priceDeal){
            textView_price_deal.setText(priceDeal.toString());
        }

        public void setPrice(Double price){
            textView_price.setText(price.toString()+" €");
            textView_price.setPaintFlags(textView_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView_price.setVisibility(View.VISIBLE);
        }

        public void setScore(Integer score)
        {
            textView_score.setText(score.toString());
        }
        public void setNumberComs(Long numberComs)
        {
            textView_number_coms.setText(numberComs.toString());
        }
        public void setUsername(String username)
        {
            textView_username.setText(username);
        }
        public void setTimeElapsed(String time) { textView_time_elapsed.setText(time); }
        public void setImage(Context context, String url) {
            Picasso.with(context)
                    .load(url)
                    //.placeholder(R.drawable.ic_launcher_background) //Put image if not exist
                    //.error(R.drawable.ic_launcher_background) // Put image if error
                    .into(view_image);
        }

        public void setVotedPlus(){
            textView_minus.setEnabled(false);
            textView_add.setTextColor(itemView.getResources().getColor(android.R.color.white));
            textView_add.setBackground(itemView.getResources().getDrawable(R.drawable.circle_red));
        }

        public void setVotedMinus(){
            textView_add.setEnabled(false);
            textView_minus.setTextColor(itemView.getResources().getColor(android.R.color.white));
            textView_minus.setBackground(itemView.getResources().getDrawable(R.drawable.circle_blue));
        }
    }

    public static class CommentaireViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ImageView view_image;
        public TextView textView_username, textView_date_post, textView_commentaire;

        public CommentaireViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            view_image = (ImageView) itemView.findViewById(R.id.comment_image_user);
            textView_username = (TextView) itemView.findViewById(R.id.comment_username);
            textView_date_post = (TextView) itemView.findViewById(R.id.comment_date);
            textView_commentaire = (TextView) itemView.findViewById(R.id.comment_text);
        }

        public void setImage(Context context, String url) {
            Picasso.with(context)
                    .load(url)
                    .into(view_image);
        }

        public void setUsername(String username)
        {
            textView_username.setText(username);
        }

        public void setDatePost(String date){
            textView_date_post.setText(date);
        }

        public void setCommentaire(String text){
            textView_commentaire.setText(text);
        }

    }
}
