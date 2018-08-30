package com.example.sabari.csedept;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class view_profile extends AppCompatActivity {

    private TextView mname;
    private TextView mdept;
    private TextView myear;
    private TextView mnumber;
    private TextView mblood;
    private TextView mrollnumber;
    private ImageView mprofileimage;
    private String rollno;

    private ProgressDialog mprogress_dialog;

    FirebaseFirestore mfirestore;
    StorageReference mstorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Bundle bundle = getIntent().getExtras();

        rollno = bundle.getString("qrcode");
        String year = bundle.getString("year");

        mprogress_dialog = new ProgressDialog(this);
        mfirestore = FirebaseFirestore.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference();
        mprogress_dialog.setMessage("Loading profile please wait");
        mprogress_dialog.setCanceledOnTouchOutside(false);
        mprogress_dialog.setButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mprogress_dialog.dismiss();
            }
        });
        mprogress_dialog.show();

        mprofileimage = findViewById(R.id.vp_profile_image);
        mname=findViewById(R.id.pv_name);
        mdept = findViewById(R.id.pv_department);
        myear = findViewById(R.id.pv_year);
        mnumber = findViewById(R.id.pv_mobile_no);
        mblood = findViewById(R.id.pv_bloodgroup);
        mrollnumber = findViewById(R.id.pv_rollno);

        mfirestore.collection("profile").document("cse").collection(year).document(rollno).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists() && dataSnapshot != null){
                    String name = dataSnapshot.getString("name");
                String year = dataSnapshot.getString("year");
                String imageurl = dataSnapshot.getString("image");
                String number = dataSnapshot.getString("phone");
                String blood = dataSnapshot.getString("blood");
                String dept ="CSE";
                mname.setText(name);
                mrollnumber.setText(rollno);
                myear.setText(year);
                mdept.setText(dept);
                mnumber.setText(number);
                mblood.setText(blood);
                Picasso.with(view_profile.this).load(imageurl).into(mprofileimage);
                mprogress_dialog.dismiss();
            }
            else{
                    Toast.makeText(view_profile.this, "error no doc", Toast.LENGTH_SHORT).show();
                    mprogress_dialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(view_profile.this, "No such roll no exists", Toast.LENGTH_SHORT).show();
                mprogress_dialog.dismiss();
            }
        });


    }
}
