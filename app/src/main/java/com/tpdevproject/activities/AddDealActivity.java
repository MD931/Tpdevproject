package com.tpdevproject.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tpdevproject.R;
import com.tpdevproject.utils.GlobalVars;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/*
    Activité pour l'ajout de deals
 */
public class AddDealActivity extends AppCompatActivity {

    private static final String TAG = "AddDealActivity";

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_REQUEST2 = 3;

    private final Calendar myBeginDate = Calendar.getInstance();
    private DatabaseReference ref;
    private StorageReference storageReference;

    private Uri uri;
    private byte[] picture;

    private ImageButton imgBtn;
    private EditText title, description, priceDeal, price,
            link, address, dateBegin, dateEnd;
    private Button btnAdd;
    private ProgressBar progressBar;


    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);
        initializeVars();
        initializeListeners();
    }

    @Override
    public void onStart(){
        super.onStart();
        checkUser();
    }

    /*
        Initialisation des variables
     */
    private void initializeVars(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference().child(GlobalVars.TABLE_DEALS);
        title = (EditText) findViewById(R.id.add_title);
        description = (EditText) findViewById(R.id.add_description);
        description.setSingleLine(false);
        priceDeal = (EditText) findViewById(R.id.add_price_deal);
        price = (EditText) findViewById(R.id.add_price);
        link = (EditText) findViewById(R.id.add_link);
        address = (EditText) findViewById(R.id.add_address);
        dateBegin = (EditText) findViewById(R.id.add_date_begin);
        dateEnd = (EditText) findViewById(R.id.add_date_end);
        btnAdd = (Button) findViewById(R.id.add_btn);
        imgBtn = (ImageButton) findViewById(R.id.add_img);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    /*
        Faire un set des listeners
     */
    private void initializeListeners() {
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAdd.setEnabled(false);
                final DatabaseReference tmp = ref.push();

                if(!verifyRequiredField()){
                    btnAdd.setEnabled(true);
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                HashMap<String, Object> map = generateMapOfField();

                tmp.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        /*
                            Inserer le timestamp en négatif pour récuperer les posts
                            du plus récent au plus ancien
                         */
                        if(dataSnapshot.getKey()
                                .equals(GlobalVars.COLUMN_DATE_POST)) {
                            Long timestamp =(Long) dataSnapshot.getValue();
                            dataSnapshot.getRef().setValue(timestamp*-1);
                        }
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.i(TAG, "onChildChanged : "+s);
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.i(TAG, "onChildRemoved : "+dataSnapshot.toString());
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Log.i(TAG, "onChildMoved : "+s);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled : "+databaseError.toString());
                    }
                });

                tmp.setValue(map);
                if(picture != null) {
                    //if(picture != null) {
                    StorageReference str = storageReference.child(GlobalVars.STORAGE_FOLDER_IMG_DEAL)
                            .child(tmp.getKey());
                    //UploadTask ut = str.putFile(uri);
                    UploadTask ut = str.putBytes(picture);
                    ut.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            finish();
                        }
                    });
                }else {
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            }
        });



        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myBeginDate.set(Calendar.YEAR, year);
                myBeginDate.set(Calendar.MONTH, monthOfYear);
                myBeginDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateBegin);
            }
        };

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myBeginDate.set(Calendar.YEAR, year);
                myBeginDate.set(Calendar.MONTH, monthOfYear);
                myBeginDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateEnd);
            }
        };

        dateBegin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(AddDealActivity.this, date, myBeginDate
                        .get(Calendar.YEAR), myBeginDate.get(Calendar.MONTH),
                        myBeginDate.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMinDate(System.currentTimeMillis());
                dpd.show();
            }
        });

        dateEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(AddDealActivity.this, date2, myBeginDate
                        .get(Calendar.YEAR), myBeginDate.get(Calendar.MONTH),
                        myBeginDate.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMinDate(System.currentTimeMillis());
                dpd.show();
            }
        });

    }

    /*
        Génerer une hashmap avec les columns pour les champs
     */
    private HashMap<String, Object> generateMapOfField() {
        HashMap<String, Object> value = new HashMap<>();


        value.put(GlobalVars.COLUMN_USER_ID,
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        value.put(GlobalVars.COLUMN_DATE_POST,
                ServerValue.TIMESTAMP);

        if(!TextUtils.isEmpty(title.getText().toString()))
            value.put(GlobalVars.COLUMN_TITLE,
                    title.getText().toString());

        if(!TextUtils.isEmpty(description.getText().toString()))
            value.put(GlobalVars.COLUMN_DESCRIPTION,
                    description.getText().toString());

        if(!TextUtils.isEmpty(priceDeal.getText().toString()))
            value.put(GlobalVars.COLUMN_PRICE_DEAL,
                    Double.parseDouble(priceDeal.getText().toString()));

        if(!TextUtils.isEmpty(price.getText().toString()))
            value.put(GlobalVars.COLUMN_PRICE,
                    Double.parseDouble(price.getText().toString()));

        if(!TextUtils.isEmpty(link.getText().toString()))
            value.put(GlobalVars.COLUMN_LINK,
                    link.getText().toString());

        if(!TextUtils.isEmpty(address.getText().toString()))
            value.put(GlobalVars.COLUMN_ADDRESS,
                    address.getText().toString());

        if(!TextUtils.isEmpty(dateBegin.getText().toString()))
            value.put(GlobalVars.COLUMN_DATE_BEGIN,
                    dateBegin.getText().toString());

        if(!TextUtils.isEmpty(dateEnd.getText().toString()))
            value.put(GlobalVars.COLUMN_DATE_END,
                    dateEnd.getText().toString());

        value.put(GlobalVars.COLUMN_ORDER, 0);

        return value;
    }

    /*
        Mets en forme la date ex : 20/12/17
     */
    private void updateLabel(EditText v){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        v.setText(sdf.format(myBeginDate.getTime()));
    }

    /*
        Vérifie que les champs sont valides
     */
    private boolean verifyRequiredField(){
        if(TextUtils.isEmpty(title.getText().toString())){
            title.setError(
                    getResources().getString(R.string.title_required).toString()
            );
            return false;
        }
        if(TextUtils.isEmpty(description.getText().toString())){
            description.setError(
                    getResources().getString(R.string.description_required).toString()
            );
            return false;
        }
        if(TextUtils.isEmpty(priceDeal.getText().toString())){
            priceDeal.setError(
                    getResources().getString(R.string.price_deal_required).toString()
            );
            return false;
        }
        if (!TextUtils.isEmpty(link.getText().toString())) {
            String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            if(!link.getText().toString().matches(regex)) {
                link.setError(
                        getResources().getString(R.string.link_field_error).toString()
                );
                return false;
            }
        }
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
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
                    rBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
        }else if (requestCode == CAMERA_REQUEST2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            imgBtn.setImageBitmap(imageBitmap);

            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            //Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            //Toast.makeText(AddDealActivity.this,"Here "+ getRealPathFromURI(tempUri),                 Toast.LENGTH_LONG).show();
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


    private void selectImage() {
        final CharSequence[] items = { getResources().getString(R.string.take_photo).toString(),
                getResources().getString(R.string.choose_gallery).toString(),
                getResources().getString(R.string.cancel).toString(), };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddDealActivity.this);
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

    /*
        Ouvrir la gallerie
     */
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getResources().getString(R.string.select_picture).toString()), GALLERY_REQUEST);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST2);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /*
        Vérifie si un utilisateur possède un username ou pas
     */
    private void checkUser(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                    .child(GlobalVars.TABLE_USERS);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
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
}
