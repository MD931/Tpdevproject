package com.tpdevproject;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tpdevproject.models.Database;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddAnnonceActivity extends AppCompatActivity {

    private static final String TAG = "AddAnnonceActivity";

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private final Calendar myBeginDate = Calendar.getInstance();
    private DatabaseReference ref;
    private StorageReference storageReference;

    private Uri uri;

    private ImageButton imgBtn;
    private EditText title, description, priceDeal, price,
            link, address, dateBegin, dateEnd;
    private Button btnAdd;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_annonce);
        initializeVars();
        initializeListeners();
    }

    private void initializeVars(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference().child("annonce");
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

                final DatabaseReference tmp = ref.push();

                if(!verifyRequiredField()){
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
                                .equals(Database.COLUMN_DATE_POST)) {
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
                if(uri != null) {

                    StorageReference str = storageReference.child(Database.STORAGE_FOLDER_IMG_DEAL)
                            .child(tmp.getKey());
                    UploadTask ut = str.putFile(uri);
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
                new DatePickerDialog(AddAnnonceActivity.this, date, myBeginDate
                        .get(Calendar.YEAR), myBeginDate.get(Calendar.MONTH),
                        myBeginDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dateEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddAnnonceActivity.this, date2, myBeginDate
                        .get(Calendar.YEAR), myBeginDate.get(Calendar.MONTH),
                        myBeginDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private HashMap<String, Object> generateMapOfField() {
        HashMap<String, Object> value = new HashMap<>();


        value.put(Database.COLUMN_USER_ID,
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        value.put(Database.COLUMN_DATE_POST,
                ServerValue.TIMESTAMP);

        if(!TextUtils.isEmpty(title.getText().toString()))
            value.put(Database.COLUMN_TITLE,
                    title.getText().toString());

        if(!TextUtils.isEmpty(description.getText().toString()))
            value.put(Database.COLUMN_DESCRIPTION,
                    description.getText().toString());

        if(!TextUtils.isEmpty(priceDeal.getText().toString()))
            value.put(Database.COLUMN_PRICE_DEAL,
                    Double.parseDouble(priceDeal.getText().toString()));

        if(!TextUtils.isEmpty(price.getText().toString()))
            value.put(Database.COLUMN_PRICE,
                    Double.parseDouble(price.getText().toString()));

        if(!TextUtils.isEmpty(link.getText().toString()))
            value.put(Database.COLUMN_LINK,
                    link.getText().toString());

        if(!TextUtils.isEmpty(address.getText().toString()))
            value.put(Database.COLUMN_ADDRESS,
                    address.getText().toString());

        if(!TextUtils.isEmpty(dateBegin.getText().toString()))
            value.put(Database.COLUMN_DATE_BEGIN,
                    dateBegin.getText().toString());

        if(!TextUtils.isEmpty(dateEnd.getText().toString()))
            value.put(Database.COLUMN_DATE_END,
                    dateEnd.getText().toString());

        value.put(Database.COLUMN_ORDER, 0);

        return value;
    }

    private void updateLabel(EditText v){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        v.setText(sdf.format(myBeginDate.getTime()));
    }

    public boolean verifyRequiredField(){
        if(TextUtils.isEmpty(title.getText().toString())){
            title.setError("Title required");
            return false;
        }
        if(TextUtils.isEmpty(description.getText().toString())){
            description.setError("Description required");
            return false;
        }
        if(TextUtils.isEmpty(priceDeal.getText().toString())){
            priceDeal.setError("Price Deal required");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Toast.makeText(this, "Yeahhhh !!!!!", Toast.LENGTH_SHORT).show();
            imgBtn.setImageURI(data.getData());
            uri = data.getData();
        }
        else if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            Toast.makeText(this, "Yeahhhh !!!!!", Toast.LENGTH_SHORT).show();
            imgBtn.setImageURI(uri);
            //uri = data.getData();
        }
    }

    String mCurrentPhotoPath;

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
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddAnnonceActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Toast.makeText(AddAnnonceActivity.this, "Photo", Toast.LENGTH_SHORT).show();
                    cameraIntent();
                } else if (item == 1) {
                    Toast.makeText(AddAnnonceActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
                    galleryIntent();
                } else if (item == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
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

    private void galleryIntent()
    {
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
}
