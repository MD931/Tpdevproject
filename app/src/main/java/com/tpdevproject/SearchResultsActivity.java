package com.tpdevproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.provider.ContactsContract;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.tpdevproject.adapters.AnnonceAdapter;
import com.tpdevproject.models.Annonce;
import com.tpdevproject.models.Database;
import com.tpdevproject.tab.AdapterTab;
import com.tpdevproject.tab.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {
    private static final String TAG = "SearchResultActivity";

    private Toolbar toolbar;
    private FirebaseUser user;
    private ProgressBar progressSearch;
    private RelativeLayout resultSearch;

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
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressSearch = (ProgressBar) findViewById(R.id.progress_search);
        resultSearch = (RelativeLayout) findViewById(R.id.result_search);
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
            showResults(query);
        }
    }

    private void showResults(String query) {
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, Database.URL + "bigben?search=" + query,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG, "onResponse : "+response.toString());
                try{
                    List<Annonce> annonces = getAnnonces(response);
                    handleResult(annonces);
                }catch(JSONException e){
                    Log.e(TAG, "onResponse : "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse : "+error.toString());
                hideProgressSearch();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void handleResult(List<Annonce> annonces) {
        Log.i(TAG, "handleResult : "+annonces);
        if(annonces.size()>0){
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.MATCH_PARENT));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            bindRecyclerView(recyclerView,annonces);
            resultSearch.addView(recyclerView);
        }else{
            TextView tx = new TextView(this);
            tx.setText(getResources().getString(R.string.no_result));
            resultSearch.addView(tx);
            hideProgressSearch();
        }
    }

    private void bindRecyclerView(RecyclerView recyclerView, List<Annonce> annonces) {
        AnnonceAdapter annonceAdapter = new AnnonceAdapter(annonces, getApplicationContext()
                , user, FirebaseDatabase.getInstance().getReference(Database.TABLE_ANNONCES));
        recyclerView.setAdapter(annonceAdapter);
    }

    private List<Annonce> getAnnonces(JSONArray response) throws JSONException{
        List<Annonce> listAnnonce = new ArrayList();
        for(int i = 0; i<response.length();i++){
            listAnnonce.add(parseAnnonce(response.getJSONObject(i)));
        }

        return listAnnonce;
    }

    private Annonce parseAnnonce(JSONObject json) throws JSONException{
        Annonce annonce = new Annonce();
        if(json.has(Database.COLUMN_TITLE))
            annonce.setTitle(json.getString(Database.COLUMN_TITLE));
        if(json.has(Database.COLUMN_DESCRIPTION))
            annonce.setDescription(json.getString(Database.COLUMN_DESCRIPTION));
        if(json.has(Database.COLUMN_DATE_POST))
            annonce.setDatePost(json.getLong(Database.COLUMN_DATE_POST));
        if(json.has(Database.COLUMN_DATE_BEGIN))
            annonce.setDateBegin(json.getString(Database.COLUMN_DATE_BEGIN));
        if(json.has(Database.COLUMN_DATE_END))
            annonce.setDateEnd(json.getString(Database.COLUMN_DATE_END));
        if(json.has(Database.COLUMN_THUMBNAIL))
            annonce.setImage(json.getString(Database.COLUMN_THUMBNAIL));
        if(json.has(Database.COLUMN_ORDER))
            annonce.setOrder(json.getInt(Database.COLUMN_ORDER));
        if(json.has(Database.COLUMN_VOTES))
            annonce.setVotes(parseVotes(json.getJSONObject(Database.COLUMN_VOTES)));
        if(json.has(Database.COLUMN_PRICE))
            annonce.setPrice(json.getDouble(Database.COLUMN_PRICE));
        if(json.has(Database.COLUMN_LINK))
            annonce.setLink(json.getString(Database.COLUMN_LINK));

        return annonce;
    }

    private Map<String,Integer> parseVotes(JSONObject jsonObject) throws JSONException{
        Map<String, Integer> m = new HashMap();
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()){
            String key = keys.next();
            m.put(key, jsonObject.getInt(key) );
        }
        return m;
    }

    private void hideProgressSearch(){
        progressSearch.setVisibility(ProgressBar.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
