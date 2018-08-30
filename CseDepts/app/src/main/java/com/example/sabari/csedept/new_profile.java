package com.example.sabari.csedept;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class new_profile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private CircleImageView mprofile_image;
    private Button mprofile_img_btn;
    private EditText mname_field;
    private EditText mroll_no_field;
    private Spinner myear_spinner;
    private Button msubmit_btn;
    private Button mcancel_btn;
    private EditText mphone;
    private Uri mimageUri;
    private EditText mblood;

    private FirebaseFirestore mfirestore;
    private StorageReference mstorage;
    private ProgressDialog mprogress_dialog;

    private String mdocument_rollno;
    private String name;
    private String roll_no;
    private String phone;
    private String year;
    private String downloadurl;
    private String blood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        mfirestore = FirebaseFirestore.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference();

        mprofile_image = findViewById(R.id.addprofile_image);
        mprofile_img_btn = findViewById(R.id.add_profile_image_btn);
        mname_field = findViewById(R.id.add_name_field);
        mroll_no_field = findViewById(R.id.add_Roll_no_field);
        myear_spinner = (Spinner) findViewById(R.id.np_add_year);
        msubmit_btn = findViewById(R.id.new_p_submit_btn);
        mcancel_btn = findViewById(R.id.cancel_new_profile_btn);
        mphone = findViewById(R.id.np_phone);
        mblood = findViewById(R.id.np_blood);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.year,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myear_spinner.setAdapter(adapter);
        myear_spinner.setOnItemSelectedListener(this);

        mprogress_dialog = new ProgressDialog(this);

        mprofile_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(new_profile.this);
            }
        });

        mcancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(new_profile.this, "you have selected" + year, Toast.LENGTH_SHORT).show();
            }
        });

        msubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = mname_field.getText().toString();
                roll_no = mroll_no_field.getText().toString().toLowerCase();
                phone = mphone.getText().toString();
                blood = mblood.getText().toString();

                mprogress_dialog.setMessage("Uploading please wait");
                mprogress_dialog.setCanceledOnTouchOutside(false);
                mprogress_dialog.setButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mprogress_dialog.dismiss();
                    }
                });
                mprogress_dialog.show();

                mdocument_rollno = roll_no;

                StorageReference mprofileimg = mstorage.child("profileimg")
                        .child(mdocument_rollno + ".jpg");

                mprofileimg.putFile(mimageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadurl = taskSnapshot.getDownloadUrl().toString();
                        Toast.makeText(new_profile.this, "image uploaded", Toast.LENGTH_SHORT).show();

                        Map<String, String> profilemap = new HashMap<>();

                        profilemap.put("name", name);
                        profilemap.put("roll", roll_no);
                        profilemap.put("phone", phone);
                        profilemap.put("year", year);
                        profilemap.put("image", downloadurl);
                        profilemap.put("blood",blood);

                        mfirestore.collection("profile").document("cse").collection(year).document(roll_no).set(profilemap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(new_profile.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                mprogress_dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(new_profile.this, "Error on firestore upload", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(new_profile.this, "error while uploading image", Toast.LENGTH_LONG).show();
                            }
                        });


                Toast.makeText(new_profile.this, "nice", Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mimageUri != null) {
            mprofile_image.setImageURI(mimageUri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                    mimageUri = result.getUri();
                mprofile_image.setImageURI(mimageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        year = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "selected is" +year, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
