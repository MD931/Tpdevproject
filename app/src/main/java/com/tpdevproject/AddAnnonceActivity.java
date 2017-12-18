package com.tpdevproject;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddAnnonceActivity extends AppCompatActivity {
    final Calendar myBeginDate = Calendar.getInstance();
    DatabaseReference ref;
    ImageButton imgBtn;
    Uri uri;
    StorageReference storageReference;

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_annonce);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference().child("annonce");
        final EditText title = (EditText) findViewById(R.id.add_title);
        final EditText description = (EditText) findViewById(R.id.add_description);
        final EditText dateBegin = (EditText) findViewById(R.id.add_date_begin);
        final EditText dateEnd = (EditText) findViewById(R.id.add_date_end);
        Button btnAdd = (Button) findViewById(R.id.add_btn);
        imgBtn = (ImageButton) findViewById(R.id.add_img);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);*/
                selectImage();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference tmp = ref.push();
                Map<String, Object> value = new HashMap<>();
                value.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                value.put("title", title.getText().toString());
                value.put("description", description.getText().toString());
                value.put("dateBegin", dateBegin.getText().toString());
                value.put("dateEnd", dateEnd.getText().toString());
                value.put("datePost", ServerValue.TIMESTAMP);
                tmp.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        /*
                            Inserer le timestamp en négatif pour récuperer les posts
                            du plus récent au plus ancien
                         */
                        if(dataSnapshot.getKey().equals("datePost")) {
                            Long timestamp =(Long) dataSnapshot.getValue();
                            dataSnapshot.getRef().setValue(timestamp*-1);
                        }
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                tmp.setValue(value);
                if(uri != null) {

                    StorageReference str = storageReference.child(tmp.getKey());
                    UploadTask ut = str.putFile(uri);
                    ut.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            finish();
                        }
                    });
                }else {
                    finish();
                }
            }
        });



        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
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
                // TODO Auto-generated method stub
                myBeginDate.set(Calendar.YEAR, year);
                myBeginDate.set(Calendar.MONTH, monthOfYear);
                myBeginDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateEnd);
            }
        };

        dateBegin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddAnnonceActivity.this, date, myBeginDate
                        .get(Calendar.YEAR), myBeginDate.get(Calendar.MONTH),
                        myBeginDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dateEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddAnnonceActivity.this, date2, myBeginDate
                        .get(Calendar.YEAR), myBeginDate.get(Calendar.MONTH),
                        myBeginDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel(EditText v){
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        v.setText(sdf.format(myBeginDate.getTime()));
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
            imgBtn.setImageURI(data.getData());
            uri = data.getData();
        }
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
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
