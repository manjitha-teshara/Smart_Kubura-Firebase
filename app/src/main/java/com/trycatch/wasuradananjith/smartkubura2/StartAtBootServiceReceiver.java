package com.trycatch.wasuradananjith.smartkubura2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartAtBootServiceReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent arg1)
    {
        Intent intent = new Intent(context,NotificationService.class);
        context.startService(intent);
        Log.d("Autostart", "StartAtBootServiceReceiver");
    }
}
