package com.childwatch.manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
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

        // 设置初始焦点
        btnSettings.requestFocus();
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

        // 遥控器导航支持
        btnSettings.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnStartStop.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnStartStop.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnSettings.requestFocus();
                    return true;
                }
            }
            return false;
        });
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
            btnStartStop.setText("⏹ 停止监控");
            tvStatus.setText("● 监控中");
            tvStatus.setTextColor(getResources().getColor(R.color.accent));
        } else {
            btnStartStop.setText("▶ 启动监控");
            tvStatus.setText("● 已停止");
            tvStatus.setTextColor(getResources().getColor(R.color.error));
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
            tvStatus.setText("● 已锁定");
            tvStatus.setTextColor(getResources().getColor(R.color.error));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 支持遥控器返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 不退出APP，保持后台运行
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
