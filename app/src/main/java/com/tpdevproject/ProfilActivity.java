package com.tpdevproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfilActivity extends AppCompatActivity {
    private final static String TAG = "ProfilActivity";

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private Toolbar toolbar;
    private RelativeLayout toolbarImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ImageButton imgBtn;
    private Uri uri;
    String mCurrentPhotoPath;

    private ImageView editeUsername;
    private TextView username;
    private EditText usernameEdit;

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

        editeUsername  = (ImageView) findViewById(R.id.edit_username);
        username = (TextView) findViewById(R.id.username);
        usernameEdit = (EditText) findViewById(R.id.username_edit);
    }

    private void initializeListeners() {
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        editeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getVisibility()==View.VISIBLE){
                    editeUsername.setImageResource(android.R.drawable.ic_delete);
                    username.setVisibility(View.INVISIBLE);
                    usernameEdit.setVisibility(View.VISIBLE);
                } else{
                    editeUsername.setImageResource(R.drawable.ic_edit_name);
                    usernameEdit.setText(username.getText());
                    username.setVisibility(View.VISIBLE);
                    usernameEdit.setVisibility(View.INVISIBLE);
                }
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            uri = FileProvider.getUriForFile(this,
                    "com.tpdevproject",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
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
            imgBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            imgBtn.setImageURI(uri);
            imgBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

}
