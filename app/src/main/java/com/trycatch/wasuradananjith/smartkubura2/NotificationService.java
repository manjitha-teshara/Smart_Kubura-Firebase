package com.trycatch.wasuradananjith.smartkubura2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trycatch.wasuradananjith.smartkubura2.Model.PaddyField;

public class NotificationService extends IntentService {

    DatabaseReference mDatabase;
    String phone,field_name;
    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public NotificationService() {
        super("NotificationService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        SharedPreferences pref = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        phone = pref.getString("phone", null);
        field_name = pref.getString("field_name",null);

        mDatabase = FirebaseDatabase.getInstance().getReference("paddy_fields/"+phone+"/"+field_name);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PaddyField paddyField= dataSnapshot.getValue(PaddyField.class);
                String required_water_level = paddyField.getRequiredWaterLevel().toString();
                String water_level = paddyField.getWaterLevel().toString();

                if (Integer.parseInt(water_level)>=Integer.parseInt(required_water_level)){
                    // Notification goes here
                }
                Log.d("NotificationService",required_water_level);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}