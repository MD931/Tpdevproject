package com.tpdevproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tpdevproject.tab.AdapterTab;
import com.tpdevproject.tab.SlidingTabLayout;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "HomeActivity";

    private FloatingActionButton fab;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    @Override
    public void onStart(){
        super.onStart();
        initializeVars();
        initialiseDrawer();
        checkUser();
    }

    private void initialiseDrawer(){
        DrawerLayout drawer  = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView loginDrawer = (TextView) headerView.findViewById(R.id.login_drawer);
        TextView emailDrawer = (TextView) headerView.findViewById(R.id.email_drawer);
        if(user!=null){
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_favoris).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_profil).setVisible(true);
            loginDrawer.setText("name");
            emailDrawer.setText(user.getEmail());
        }else{
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_favoris).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_profil).setVisible(false);
            loginDrawer.setText("Welcome");
            emailDrawer.setText("");
        }
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.i(TAG, "onPointerCaptureChanged : "+hasCapture);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        Log.i(TAG, "selectItem : "+position);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            startLoginActivity();
        } else if (id == R.id.nav_profil) {
            //TODO
        } else if (id == R.id.nav_favoris) {
            startFavorisActivity();
        } else if (id == R.id.nav_maps) {
            //TODO
        } else if (id == R.id.nav_logout) {
            auth.signOut();
            finish();
            startActivity(getIntent());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeVars(){

        //Firebase
        auth = FirebaseAuth.getInstance();

        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Sliding
        viewPager = (ViewPager) findViewById(R.id.vp_tab);
        viewPager.setAdapter(new AdapterTab(getSupportFragmentManager()));
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tab);
        setupTabs();
        slidingTabLayout.setViewPager(viewPager);

        user = auth.getCurrentUser();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null){
                    startAddAnnonceActivity();
                }else {
                    showSnackBar(view);
                }
            }
        });
    }

    private void setupTabs(){
        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
    }

    private void startAddAnnonceActivity() {
        Intent intent = new Intent(getApplicationContext(), AddAnnonceActivity.class);
        startActivity(intent);
    }

    private void showSnackBar(View view) {
        Snackbar.make(view, getResources().getString(R.string.login_before), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.login), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startLoginActivity();
                    }
                }).show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void startFavorisActivity() {
        Intent intent = new Intent(getApplicationContext(), FavorisActivity.class);
        startActivity(intent);
    }


    private void checkUser(){
        if(user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user.getUid())){
                        startActivity(new Intent(getApplicationContext(), SetupActivity.class));
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG,databaseError.getDetails());
                }
            });
        }
    }
}
