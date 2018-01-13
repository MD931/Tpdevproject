package com.tpdevproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.tpdevproject.adapters.AnnonceAdapter;
import com.tpdevproject.models.Annonce;
import com.tpdevproject.models.Database;
import com.tpdevproject.parsers.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FavorisActivity extends AppCompatActivity {
    private static final String TAG = "FavorisActivity";

    private FirebaseUser user;
    private ProgressBar progressSearch;
    private RelativeLayout resultSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        initializeVars();
    }

    private void initializeVars(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_favoris);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressSearch = (ProgressBar) findViewById(R.id.progress_search);
        resultSearch = (RelativeLayout) findViewById(R.id.result_search);
        showResults(user.getUid());
    }

    @Override
    public void onStart(){
        super.onStart();
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

    private void showResults(String id) {
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Database.URL + "favoris?id="+id,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
            hideProgressSearch();
            resultSearch.addView(recyclerView);
        }else{
            TextView tx = new TextView(this);
            tx.setText(getResources().getString(R.string.no_result));
            hideProgressSearch();
            resultSearch.addView(tx);
        }
    }

    private void bindRecyclerView(RecyclerView recyclerView, List<Annonce> annonces) {
        AnnonceAdapter annonceAdapter = new AnnonceAdapter(annonces, getApplicationContext()
                , user, FirebaseDatabase.getInstance().getReference(Database.TABLE_ANNONCES));
        recyclerView.setAdapter(annonceAdapter);
    }

    private List<Annonce> getAnnonces(JSONObject response) throws JSONException{
        List<Annonce> listAnnonce = new ArrayList();
        Iterator<String> keys = response.keys();
        while(keys.hasNext()){
            String key = keys.next();
            listAnnonce.add(Parser.parseAnnonce(key, response.getJSONObject(key)));
        }

        return listAnnonce;
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