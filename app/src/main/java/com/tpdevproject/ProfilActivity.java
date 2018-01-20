package com.tpdevproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfilActivity extends AppCompatActivity {
    private final static String TAG = "ProfilActivity";

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private Toolbar toolbar;
    private RelativeLayout toolbarImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private EditText username;
    private ImageButton imgBtn;
    private Uri uri;

    private DatabaseReference userRef;
    private FirebaseUser user;

    private Boolean imageLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        initializeVars();
        initializeListeners();
    }

    private void initializeVars() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_profil);
        toolbarImage = (RelativeLayout) findViewById(R.id.header_toolbar_profil);

        toolbar.setTitle("Profil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_profil);
        collapsingToolbarLayout.setTitleEnabled(false);

        imgBtn = (ImageButton) findViewById(R.id.profil_img);
        username = (EditText) findViewById(R.id.profil_username);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initializeListeners() {
        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*if(dataSnapshot.hasChild("thumbnail")) {
                    if(!imageLoaded) {
                        imageLoaded = true;
                        picassoLoader(getApplicationContext(), imgBtn,
                                dataSnapshot.child("thumbnail").getValue().toString());
                        imgBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }*/
                username.setText(dataSnapshot.child("username").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());;
            }
        });
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }
    public void selectImage(){
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Toast.makeText(ProfilActivity.this, "Photo", Toast.LENGTH_SHORT).show();
                    cameraIntent();
                } else if (item == 1) {
                    Toast.makeText(ProfilActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
                    galleryIntent();
                } else if (item == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            uri = data.getData();
            //imgBtn.setImageURI(uri);
            picassoLoader(getApplicationContext(), imgBtn, uri);
        }
        else if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgBtn.setImageBitmap(photo);
        }
    }

    private void picassoLoader(Context context, ImageView imageView, String url) {
        Log.i(TAG, "picassoLoader");
        Picasso.with(context)
                .load(url)
                //.resize(30,30)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }

    private void picassoLoader(Context context, ImageView imageView, Uri url) {
        Log.i(TAG, "picassoLoader");
        Picasso.with(context)
                .load(url)
                //.resize(30,30)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }

}
