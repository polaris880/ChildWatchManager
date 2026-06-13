package com.childwatch.manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.childwatch.manager.services.WatchdogService;
import com.childwatch.manager.utils.ConfigManager;
import com.childwatch.manager.utils.TimeManager;

public class MainActivity extends AppCompatActivity {

    private ConfigManager configManager;
    private TimeManager timeManager;
    private Handler handler;

    private TextView tvTodayTotal;
    private TextView tvSessionTime;
    private TextView tvRemainingTime;
    private TextView tvStatus;
    private Button btnSettings;
    private Button btnStartStop;

    private boolean isServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configManager = ConfigManager.getInstance(this);
        timeManager = TimeManager.getInstance(this);
        handler = new Handler(Looper.getMainLooper());

        initViews();
        checkPasswordSetup();
        startService();
        startUIUpdate();
    }

    private void initViews() {
        tvTodayTotal = findViewById(R.id.tv_today_total);
        tvSessionTime = findViewById(R.id.tv_session_time);
        tvRemainingTime = findViewById(R.id.tv_remaining_time);
        tvStatus = findViewById(R.id.tv_status);
        btnSettings = findViewById(R.id.btn_settings);
        btnStartStop = findViewById(R.id.btn_start_stop);

        btnSettings.setOnClickListener(v -> openSettings());
        btnStartStop.setOnClickListener(v -> toggleService());
    }

    private void checkPasswordSetup() {
        if (!configManager.hasPassword()) {
            Toast.makeText(this, "请先设置家长密码", Toast.LENGTH_LONG).show();
            openSettings();
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, WatchdogService.class);
        startForegroundService(serviceIntent);
        isServiceRunning = true;
        updateServiceButton();
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, WatchdogService.class);
        stopService(serviceIntent);
        isServiceRunning = false;
        updateServiceButton();
    }

    private void toggleService() {
        if (isServiceRunning) {
            stopService();
        } else {
            startService();
        }
    }

    private void updateServiceButton() {
        if (isServiceRunning) {
            btnStartStop.setText("停止监控");
            tvStatus.setText("监控中");
        } else {
            btnStartStop.setText("启动监控");
            tvStatus.setText("已停止");
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void startUIUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void updateUI() {
        long todayTotal = timeManager.getTodayTotalSeconds();
        long sessionSeconds = timeManager.getSessionSeconds();
        long remaining = timeManager.getRemainingSeconds();

        tvTodayTotal.setText("今日已观看: " + TimeManager.formatTime(todayTotal));
        tvSessionTime.setText("当前会话: " + TimeManager.formatTime(sessionSeconds));
        tvRemainingTime.setText("剩余时间: " + TimeManager.formatTime(remaining));

        if (configManager.isLocked()) {
            tvStatus.setText("已锁定");
        } else if (timeManager.isSessionActive()) {
            tvStatus.setText("观看中");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
