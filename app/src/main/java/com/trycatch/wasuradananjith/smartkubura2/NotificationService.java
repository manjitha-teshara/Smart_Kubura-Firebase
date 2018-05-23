package com.trycatch.wasuradananjith.smartkubura2;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trycatch.wasuradananjith.smartkubura2.Model.PaddyField;

public class NotificationService extends Service {

    DatabaseReference mDatabase;
    String phone,field_name;
    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        SharedPreferences pref = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        phone = pref.getString("phone", null);
        field_name = pref.getString("field_name",null);

        mDatabase = FirebaseDatabase.getInstance().getReference("paddy_fields/"+phone+"/"+field_name);
        Log.d("phone",phone);
        Log.d("field_name",field_name);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!field_name.equals("කුඹුරක් තෝරාගෙන නැත")){
                    PaddyField paddyField= dataSnapshot.getValue(PaddyField.class);
                    String required_water_level = paddyField.getRequiredWaterLevel();
                    String water_level = paddyField.getWaterLevel();

                    if (Integer.parseInt(water_level)>=Integer.parseInt(required_water_level)){

                        mDatabase.child("isFilling").setValue(0); // update the isFilling state of the database entry to 0

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.icon)
                                        .setContentTitle("සාර්ථකයි!")
                                        .setContentText(field_name+"හි ජලය පිරී අවසන්");
                        NotificationManager mNotifyMgr =
                                (NotificationManager) getApplicationContext().getSystemService(android.content.Context.NOTIFICATION_SERVICE);
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        mBuilder.setSound(alarmSound);
                        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                        mBuilder.setLights(Color.RED, 3000, 3000);
                        mNotifyMgr.notify(0, mBuilder.build());
                    }
                    else{
                        Log.d("NotificationService",Integer.parseInt(water_level) + ""+ water_level);
                    }
                    Log.d("NotificationService",required_water_level);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }
}