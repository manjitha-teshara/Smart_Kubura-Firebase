package com.trycatch.wasuradananjith.smartkubura2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trycatch.wasuradananjith.smartkubura2.Model.PaddyField;

public class StopWaterPassActivity extends AppCompatActivity {
    TextView txtWaterLevel;
    ImageView btnBackButton,stopButton;
    DatabaseReference mDatabase;
    String phone;
    EditText txtRequiredWaterLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_water_pass);

        txtWaterLevel =(TextView)findViewById(R.id.lblcurrentWaterLevel);
        btnBackButton = (ImageView)findViewById(R.id.imgBackButton);
        stopButton = (ImageView)findViewById(R.id.imgStopFillWater);
        txtRequiredWaterLevel = (EditText)findViewById(R.id.txtNeededWaterLevel);

        // get the data passed from the previous activity
        final Bundle bundle = getIntent().getExtras();
        final String water_level = bundle.getString("water_level");
        final String field_name = bundle.getString("paddy_field_name");
        final String required_water_level = bundle.getString("required_water_level");
        txtWaterLevel.setText(water_level);
        txtRequiredWaterLevel.setText(required_water_level);
        txtRequiredWaterLevel.setEnabled(false);

        // load the Home Activity on back arrow button pressed
        btnBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        // get data for the particular logged in user from Shared Preferences (local storage of the app)
        SharedPreferences pref = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        phone = pref.getString("phone", null);

        // get the database reference "paddy_fields" in firebase realtime database
        mDatabase = FirebaseDatabase.getInstance().getReference("paddy_fields/"+phone+"/"+field_name);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PaddyField paddyField= dataSnapshot.getValue(PaddyField.class);
                txtWaterLevel.setText(paddyField.getWaterLevel().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // when the stop water fill button is clicked
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mDatabase.child("isFilling").setValue(0); // update the isFilling state of the database entry to 0

                        final ProgressDialog progressDialog = new ProgressDialog(StopWaterPassActivity.this,
                                R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("මදක් රැඳෙන්න ...");
                        progressDialog.show();

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        Toast.makeText(StopWaterPassActivity.this,"ජලය පිරීම නතර වුණි",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),StartWaterPassActivity.class);
                                        intent.putExtra("paddy_field_name", field_name);
                                        intent.putExtra("water_level", water_level);
                                        intent.putExtra("required_water_level", required_water_level);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
