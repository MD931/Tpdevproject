package com.tpdevproject.adapters;

import android.content.Context;
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
        public TextView textView_title, textView_score;
        public TextView textView_time_elapsed,
                textView_number_coms, textView_username, textView_minus, textView_add;

        public AnnonceViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            textView_title = (TextView)itemView.findViewById(R.id.item_title);
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
        public void setTimeElapsed(String time) { textView_time_elapsed.setText(time); }
        public void setImage(Context context, String url) {
            Picasso.with(context)
                    .load(url)
                    //.placeholder(R.drawable.ic_launcher_background) //Put image if not exist
                    //.error(R.drawable.ic_launcher_background) // Put image if error
                    .into(view_image);
        }
    }
}
