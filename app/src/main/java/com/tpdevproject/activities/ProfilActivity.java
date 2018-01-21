package com.tpdevproject.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tpdevproject.R;
import com.tpdevproject.utils.GlobalVars;

import java.util.HashMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfilActivity extends AppCompatActivity {
    private final static String TAG = "ProfilActivity";

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private EditText username;
    private ImageButton imgBtn;

    private Uri uri;
    private byte[] picture;

    private DatabaseReference userRef;
    private FirebaseUser user;

    private Boolean imageLoaded = false;

    private Button editBtn;
    private StorageReference storageReference;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        initializeVars();
        initializeListeners();
    }

    private void initializeVars() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profil);
        RelativeLayout toolbarImage = (RelativeLayout) findViewById(R.id.header_toolbar_profil);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_profil);
        collapsingToolbarLayout.setTitleEnabled(false);

        imgBtn = (ImageButton) findViewById(R.id.profil_img);
        username = (EditText) findViewById(R.id.profil_username);
        editBtn = (Button) findViewById(R.id.edit_profil_button);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initializeListeners() {
        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("thumbnail")) {
                    if(!imageLoaded) {
                        imageLoaded = true;
                        picassoLoader(getApplicationContext(), imgBtn,
                                dataSnapshot.child("thumbnail").getValue().toString());
                        imgBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
                username.setText(dataSnapshot.child("username").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(GlobalVars.COLUMN_USERNAME, username.getText().toString());
                userRef.child(user.getUid()).updateChildren(map);
                if(picture != null) {
                    //if(picture != null) {
                    StorageReference str = storageReference.child(GlobalVars.STORAGE_FOLDER_IMG_PROFILE)
                            .child(user.getUid());
                    //UploadTask ut = str.putFile(uri);
                    UploadTask ut = str.putBytes(picture);
                    ut.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            finish();
                        }
                    });
                }
            }
        });
    }
    private void selectImage() {
        final CharSequence[] items = { getResources().getString(R.string.take_photo).toString(),
                getResources().getString(R.string.choose_gallery).toString(),
                getResources().getString(R.string.cancel).toString(), };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.add_photo).toString());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    cameraIntent();
                    //dispatchTakePictureIntent();
                } else if (item == 1) {
                    galleryIntent();
                } else if (item == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /*
            Ouvrir la caméra
         */
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.e(TAG, "cameraIntent : "+ex.getMessage());
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Log.i(TAG, "photoFile not null");
            uri = FileProvider.getUriForFile(this,
                    "com.tpdevproject",
                    photoFile);
            if(android.os.Build.VERSION.SDK_INT > 22) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            if(intent.resolveActivity(getPackageManager()) != null)
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
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                picture = baos.toByteArray();
                imgBtn.setImageBitmap(bitmap);
                imgBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            int rotation = 0;
            try {
                try {
                    ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
                    //ExifInterface exif = new ExifInterface(uri.getPath());
                    rotation = exifToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
                    Log.i(TAG, "rotation : " + rotation);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    //imgBtn.setImageURI(data.getData());
                    Bitmap rBmp = rotateImage(bitmap, rotation);
                    //imgBtn.setImageURI(uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rBmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    picture = baos.toByteArray();
                    imgBtn.setImageBitmap(rBmp);
                    imgBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }catch(RuntimeException e){
                    Bundle extras = data.getExtras();
                    Bitmap rBmp = (Bitmap) extras.get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    picture = baos.toByteArray();
                    imgBtn.setImageBitmap(rBmp);
                }

                //uri = data.getData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        Faire une rotation de l'image selon un angle
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /*
        Retourne l'angle d'après un ExifOrientation
     */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    /*
        Crée un fichier temporaire ou sauvegarder l'image
     */
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