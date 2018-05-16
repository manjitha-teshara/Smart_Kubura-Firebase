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
import com.trycatch.wasuradananjith.smartkubura2.Model.User;

public class AddPaddyFieldActivity extends AppCompatActivity {
    EditText waterLevel,farmName,imeiNumber;
    ImageView btnBackButton;
    ImageView imgAddNewFarm;
    TextView txtAddVewFarm;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_farm);

        waterLevel = (EditText)findViewById(R.id.edtWaterLevelNewFarm);
        farmName = (EditText)findViewById(R.id.edtFarmName);
        imgAddNewFarm = (ImageView)findViewById(R.id.imgEnterNewFarm);
        txtAddVewFarm =(TextView)findViewById(R.id.txtEnterNewFarm);
        imeiNumber = (EditText)findViewById(R.id.edtImeiNumber);
        btnBackButton = (ImageView)findViewById(R.id.imgBackButton);

        mDatabase = FirebaseDatabase.getInstance().getReference("paddy_fields");

        btnBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        imgAddNewFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                String phone = pref.getString("phone", null);

                String level = waterLevel.getText().toString();
                String paddyFieldName = farmName.getText().toString();
                String imei = imeiNumber.getText().toString();

                if (level.isEmpty() && paddyFieldName.isEmpty() && imei.isEmpty()){
                    Toast.makeText(getApplicationContext(), "සියලු ක්ෂේත්ර පුරවන්න",
                            Toast.LENGTH_LONG).show();
                }
                else{

                    final PaddyField paddy = new PaddyField("0",paddyFieldName,imei,phone,0,level);

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(paddy.getPhone()).child(paddy.getPaddyFieldName()).exists()){
                                Toast.makeText(AddPaddyFieldActivity.this,"මෙම නම මීට පෙර ලියාපදිංචි කර ඇත!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                mDatabase.child(paddy.getPhone()).child(paddy.getPaddyFieldName()).setValue(paddy);

                                final ProgressDialog progressDialog = new ProgressDialog(AddPaddyFieldActivity.this,
                                        R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("මදක් රැඳෙන්න ...");
                                progressDialog.show();

                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                Toast.makeText(AddPaddyFieldActivity.this,"ලියාපදිංචි වීම සාර්ථකයි!",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                progressDialog.dismiss();
                                            }
                                        }, 3000);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });
    }
}
