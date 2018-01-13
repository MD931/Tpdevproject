package com.tpdevproject;

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
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ProfilActivity extends AppCompatActivity {
    private final static String TAG = "ProfilActivity";

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private Toolbar toolbar;
    private RelativeLayout toolbarImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ImageButton imgBtn;
    private Uri uri;

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

        imgBtn = (ImageButton) findViewById(R.id.add_img_profil);
    }

    private void initializeListeners() {
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
            imgBtn.setImageURI(uri);
        }
        else if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgBtn.setImageBitmap(photo);
        }
    }

}
