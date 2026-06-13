package com.childwatch.manager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.childwatch.manager.services.WatchdogService;
import com.childwatch.manager.utils.ConfigManager;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Boot completed received");

            ConfigManager configManager = ConfigManager.getInstance(context);

            if (configManager.isAutoStartEnabled() && configManager.isMonitoringEnabled()) {
                Log.d(TAG, "Starting watchdog service on boot");
                Intent serviceIntent = new Intent(context, WatchdogService.class);
                context.startForegroundService(serviceIntent);
            }
        }
    }
}
