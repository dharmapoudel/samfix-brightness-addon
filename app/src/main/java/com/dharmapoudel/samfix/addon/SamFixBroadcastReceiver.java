package com.dharmapoudel.samfix.addon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class SamFixBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean value = false;

        String act = intent.getAction();
        if(act != null && act.equals(context.getResources().getString(R.string.filter_intent)) ) {

            try {
                value = Settings.System.getInt(context.getContentResolver(), "shown_max_brightness_dialog", 0) == 1;
                Settings.System.putInt(context.getContentResolver(), "shown_max_brightness_dialog", value ? 0 : 1);
                Log.i(SamFixBroadcastReceiver.class.getSimpleName(), "SamFix Brightness broadcast received and updated settings successfully!");
            }catch(Exception e){
                Log.e(SamFixBroadcastReceiver.class.getSimpleName(), "SamFix Exception occured while toggling max brightness "+ e.getMessage());
            }


            //send intent back to the SamFix
            Intent i = new Intent("com.dharmapoudel.samfix.Brightness");
            i.putExtra("brightness_value", !value);
            i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(i);
        }

    }

}