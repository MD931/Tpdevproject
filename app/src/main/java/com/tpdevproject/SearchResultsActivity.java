package com.tpdevproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tpdevproject.models.Annonce;
import com.tpdevproject.models.Database;
import com.tpdevproject.tab.AdapterTab;
import com.tpdevproject.tab.SlidingTabLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    private static final String TAG = "SearchResultActivity";

    private TextView tx;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference annonceRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        initializeVars();
        handleIntent(getIntent());
    }

    private void initializeVars(){
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        annonceRef= FirebaseDatabase.getInstance().getReference("annonce");
        //tx = (TextView) findViewById(R.id.search_request);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            toolbar.setTitle(query);
            setSupportActionBar(toolbar);
            recyclerView = (RecyclerView) findViewById(R.id.recycle_annonce);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            testSearch(query);
            //bindRecyclerView(query);
        }
    }

    private void testSearch(String query) {
        final ArrayList<DataSnapshot> array = new ArrayList<>();
        annonceRef.orderByChild("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "start onDataChange : "+dataSnapshot);
                for(DataSnapshot e : dataSnapshot.getChildren()){
                    if(e.child("description").getValue(String.class).contains("ipsum")){
                        array.add(e);
                    }
                }
                Log.i(TAG, "end onDataChange : "+array);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void bindRecyclerView(String request){
        Query query = annonceRef.equalTo("title", "Neige");
        FirebaseRecyclerAdapter<Annonce,AnnonceViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<Annonce,AnnonceViewHolder>(
                Annonce.class,
                R.layout.item,
                AnnonceViewHolder.class,
                query
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
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mIntent = new Intent(getApplicationContext(), DetailActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString(DetailActivity.ID_DEAL, model.getId());
                        mIntent.putExtras(mBundle);
                        startActivity(mIntent);
                    }
                });

                viewHolder.textView_score.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Clique sur score", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.imageView_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(user != null){
                            Log.i("votes","add");
                            annonceRef.child(model.getId()).child("votes").child(user.getUid()).setValue(1);
                        }else{
                            Toast.makeText(getApplicationContext(), "Please Login before", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "Please Login before", Toast.LENGTH_SHORT).show();
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
        private TextView textView_number_coms, textView_username;

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
            Picasso.with(context)
                    .load(url)
                    //.placeholder(R.drawable.ic_launcher_background) //Put image if not exist
                    //.error(R.drawable.ic_launcher_background) // Put image if error
                    .into(view_image);}
    }
}
