package com.tpdevproject.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tpdevproject.R;
import com.tpdevproject.utils.GlobalVars;
import com.tpdevproject.utils.SnackBarUtils;
import com.tpdevproject.tab.AdapterTab;
import com.tpdevproject.tab.SlidingTabLayout;

/*
    Activité de la page d'accueil
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "HomeActivity";

    private SlidingTabLayout slidingTabLayout;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private GoogleSignInClient mGoogleSignInClient;

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

    /*
        Initilisation du drawer
     */
    private void initialiseDrawer(){
        DrawerLayout drawer  = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final TextView loginDrawer = (TextView) headerView.findViewById(R.id.login_drawer);
        final TextView emailDrawer = (TextView) headerView.findViewById(R.id.email_drawer);
        final ImageView imgUser = (ImageView) headerView.findViewById(R.id.image_drawer);
        if(user!=null){
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            // [END config_signin]

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_favoris).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_profil).setVisible(true);

            /*
                Récuperer les infos de l'utilisateur et l'afficher
            */
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(GlobalVars.TABLE_USERS)
                    .child(user.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "onDataChange :" + dataSnapshot.child(GlobalVars.COLUMN_USERNAME));
                    if(dataSnapshot.hasChild(GlobalVars.COLUMN_USERNAME))
                        loginDrawer.setText(dataSnapshot.child(GlobalVars.COLUMN_USERNAME).getValue().toString());
                    if(dataSnapshot.hasChild(GlobalVars.COLUMN_THUMBNAIL)){
                        picassoLoader(getApplicationContext(), imgUser,dataSnapshot.child(GlobalVars.COLUMN_THUMBNAIL).getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i(TAG, "oncCancelled ValueEventListener");
                }
            });
            //loginDrawer.setText("name");
            emailDrawer.setText(user.getEmail());
        }else{
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_favoris).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_profil).setVisible(false);
            /*loginDrawer.setText("Welcome");
            emailDrawer.setText("");*/
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
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.i(TAG, "onPointerCaptureChanged : "+hasCapture);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Log.i(TAG, "selectItem : "+position);
    }


    /*
        Naviguer vers les différentes activités à partir du drawer
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_login) {
            startLoginActivity();
        } else if (id == R.id.nav_profil) {
            startProfilActivity();
        } else if (id == R.id.nav_favoris) {
            startFavorisActivity();
        } else if (id == R.id.nav_maps) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            auth.signOut();
            // Google sign out
            mGoogleSignInClient.signOut();
            finish();
            startActivity(getIntent());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    /*
        Initialisation des variables
     */
    private void initializeVars(){

        //Firebase
        auth = FirebaseAuth.getInstance();

        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Sliding
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_tab);
        String[] titles = {
                getResources().getString(R.string.tab_new),
                getResources().getString(R.string.tab_best)
        };

        viewPager.setAdapter(new AdapterTab(getSupportFragmentManager(), titles));
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tab);
        setupTabs();
        slidingTabLayout.setViewPager(viewPager);

        user = auth.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null){
                    startAddAnnonceActivity();
                }else {
                    SnackBarUtils.showSnackBarLogin(view, getApplicationContext());
                }
            }
        });
    }

    private void setupTabs(){
        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
    }

    private void startAddAnnonceActivity() {
        Intent intent = new Intent(getApplicationContext(), AddDealActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void startProfilActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
        startActivity(intent);
    }


    private void startFavorisActivity() {
        Intent intent = new Intent(getApplicationContext(), FavorisActivity.class);
        startActivity(intent);
    }


    /*
        Vérifie si un utilisateur possède un username ou pas
     */
    private void checkUser(){
        if(user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                    .child(GlobalVars.TABLE_USERS);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user.getUid())){
                        startActivity(new Intent(getApplicationContext(), CompleteProfilActivity.class));
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG,databaseError.getDetails());
                }
            });
        }
    }

    private void picassoLoader(Context context, ImageView imageView, String url) {
        Log.i(TAG, "picassoLoader");
        Picasso.with(context)
                .load(url)
                .into(imageView);
    }
}
