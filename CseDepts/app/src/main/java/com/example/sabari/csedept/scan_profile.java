package com.example.sabari.csedept;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class scan_profile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Button qrscanner;
    private Button mnew_profile;
    private EditText mrollno;
    private Button msearch;
    private Spinner myear_spinner;
    private String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_profile);

        qrscanner = findViewById(R.id.scan_btn);
        mnew_profile = findViewById(R.id.new_profile_btn);
        mrollno = findViewById(R.id.editText);
        msearch = findViewById(R.id.search_btn);
        myear_spinner = (Spinner) findViewById(R.id.sp_add_year);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.year,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myear_spinner.setAdapter(adapter);
        myear_spinner.setOnItemSelectedListener(this);

        qrscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(scan_profile.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        msearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String roll_no = mrollno.getText().toString();

                starttrans(roll_no);

            }
        });

        mnew_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_profile = new Intent(scan_profile.this, new_profile.class);
                startActivity(new_profile);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("scan_profile", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("scan_profile", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                String qrresult = result.getContents();

                starttrans(qrresult);

            }
        }
    }

    private void starttrans(String aqrresult) {

        Intent scanedintent = new Intent(scan_profile.this, view_profile.class);
        Bundle mbundle = new Bundle();
        mbundle.putString("qrcode",aqrresult);
        mbundle.putString("year",year);
        scanedintent.putExtras(mbundle);
        startActivity(scanedintent);
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
