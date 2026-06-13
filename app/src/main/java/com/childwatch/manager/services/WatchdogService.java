package com.childwatch.manager.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.childwatch.manager.ChildWatchApplication;
import com.childwatch.manager.MainActivity;
import com.childwatch.manager.R;
import com.childwatch.manager.utils.ConfigManager;
import com.childwatch.manager.utils.TimeManager;

public class WatchdogService extends Service implements TimeManager.TimeListener {

    private static final String TAG = "WatchdogService";
    private static final int NOTIFICATION_ID = 1001;

    private ConfigManager configManager;
    private TimeManager timeManager;
    private Handler handler;
    private boolean isMonitoring;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "WatchdogService created");

        configManager = ConfigManager.getInstance(this);
        timeManager = TimeManager.getInstance(this);
        handler = new Handler(Looper.getMainLooper());

        // 设置监听器
        timeManager.setListener(this);

        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "WatchdogService started");
        startMonitoring();
        return START_STICKY; // 服务被杀死后自动重启
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "WatchdogService destroyed");
        stopMonitoring();
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    ChildWatchApplication.CHANNEL_ID,
                    "监控服务",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, ChildWatchApplication.CHANNEL_ID)
                .setContentTitle("儿童观看时长管理")
                .setContentText("监控服务运行中")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void startMonitoring() {
        if (!isMonitoring) {
            isMonitoring = true;
            Log.d(TAG, "Monitoring started");

            // 启动计时
            timeManager.startSession();

            // 定期检查任务
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isMonitoring) {
                        checkWatchStatus();
                        handler.postDelayed(this, 5000); // 每5秒检查一次
                    }
                }
            }, 5000);
        }
    }

    private void stopMonitoring() {
        isMonitoring = false;
        timeManager.stopSession();
        Log.d(TAG, "Monitoring stopped");
    }

    private void checkWatchStatus() {
        long todayTotal = timeManager.getTodayTotalSeconds();
        long remaining = timeManager.getRemainingSeconds();

        Log.d(TAG, "Today total: " + todayTotal + "s, Remaining: " + remaining + "s");

        // 检查是否需要锁定
        if (configManager.isLocked()) {
            showLockScreen();
        }
    }

    private void showLockScreen() {
        Intent intent = new Intent(this, com.childwatch.manager.LockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showAlert(String message) {
        Intent intent = new Intent(this, com.childwatch.manager.AlertActivity.class);
        intent.putExtra("message", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // TimeManager.TimeListener 实现
    @Override
    public void onTimeUpdate(long todayTotalSeconds, long sessionSeconds) {
        // 可以在这里更新通知或UI
    }

    @Override
    public void onSingleLimitReached() {
        Log.d(TAG, "Single limit reached");
        showAlert("单次观看时间已到，请休息 " + configManager.getIntervalRest() + " 分钟");
        configManager.setLocked(true);
    }

    @Override
    public void onIntervalReached() {
        Log.d(TAG, "Interval reached");
        showAlert("连续观看时间已到，请休息 " + configManager.getIntervalRest() + " 分钟");
    }

    @Override
    public void onDailyLimitReached() {
        Log.d(TAG, "Daily limit reached");
        configManager.setLocked(true);
        showLockScreen();
    }

    @Override
    public void onNearLimit(int remainingMinutes) {
        Log.d(TAG, "Near limit: " + remainingMinutes + " minutes remaining");
        showAlert("观看时间还剩 " + remainingMinutes + " 分钟");
    }
}
