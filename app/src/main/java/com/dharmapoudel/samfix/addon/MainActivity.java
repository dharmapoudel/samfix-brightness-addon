package com.dharmapoudel.samfix.addon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PERMISSION = "android.permission.WRITE_SECURE_SETTINGS";
    private static final String COMMAND    = "adb shell pm grant " + BuildConfig.APPLICATION_ID + " " + PERMISSION;

    private SamFixBroadcastReceiver nReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        nReceiver = new SamFixBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.filter_intent));
        registerReceiver(nReceiver, filter);

        if (!hasPermission(this)) {

            Dialog dialog = createTipsDialog(this);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            dialog.show();

        } else {
            //set max brightness warning switch
            findViewById(R.id.brightness_toggle).setBackground(getDrawable(isBrightnessToggled() ? R.drawable.toggle_on  : R.drawable.toggle_off ));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(nReceiver!= null)
            unregisterReceiver(nReceiver);
    }

    public static Dialog createTipsDialog(final Context context) {
        return new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle(R.string.tips_title)
                .setMessage(context.getString(R.string.tips, COMMAND))
                .setNegativeButton(R.string.tips_ok, null)
                .setPositiveButton(R.string.tips_copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipData clipData = ClipData.newPlainText(COMMAND, COMMAND);
                        ClipboardManager manager = (ClipboardManager) context.getSystemService(Service.CLIPBOARD_SERVICE);
                        manager.setPrimaryClip(clipData);
                        Toast.makeText(context, R.string.copy_done, Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
    }

    public static boolean hasPermission(Context context) {
        return context.checkCallingOrSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    public void hideLuncherIcon(View view) {
        view.setBackground(getDrawable(R.drawable.toggle_on));
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, com.dharmapoudel.samfix.addon.MainActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        finish();
    }

    public void toggleBrightness(View view) {
        boolean value ;
        try {
            value = Settings.System.getInt(getContentResolver(), "shown_max_brightness_dialog", 0) == 1;
            Settings.System.putInt(getContentResolver(), "shown_max_brightness_dialog", value ? 0 : 1);

            view.setBackground(getDrawable(!value ? R.drawable.toggle_on  : R.drawable.toggle_off ));
            Log.i(SamFixBroadcastReceiver.class.getSimpleName(), "SamFix Brightness Addon: brightness toggled successfully");
        }catch(Exception e){
            Log.e(SamFixBroadcastReceiver.class.getSimpleName(), "SamFix Exception occured while toggling max brightness "+ e.getMessage());
        }
    }

    private boolean isBrightnessToggled(){
        boolean value = false;
        try {
            value = Settings.System.getInt(getContentResolver(), "shown_max_brightness_dialog", 0) == 1;
            Log.i(MainActivity.class.getSimpleName(), "SamFix Brightness Addon: brightness is toogled : " + value);
        }catch(Exception e){
            Log.e(SamFixBroadcastReceiver.class.getSimpleName(), "SamFix Exception occured while fetching max brightness "+ e.getMessage());
        }
        return value;
    }


}