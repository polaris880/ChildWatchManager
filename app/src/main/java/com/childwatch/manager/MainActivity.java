package com.childwatch.manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.childwatch.manager.models.ChildProfile;
import com.childwatch.manager.services.WatchdogService;
import com.childwatch.manager.utils.ConfigManager;
import com.childwatch.manager.utils.TimeManager;

public class MainActivity extends AppCompatActivity {

    private ConfigManager configManager;
    private TimeManager timeManager;
    private Handler handler;

    // UI组件
    private TextView tvChildName;
    private TextView tvStatus;
    private TextView tvTodayTotal;
    private TextView tvRemainingTime;
    private TextView tvSessionTime;
    private TextView tvProgressText;
    private ProgressBar progressTime;

    // 按钮
    private Button btnPrevChild, btnNextChild;
    private Button btnSettings, btnReward, btnReport, btnStartStop;

    private boolean isServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configManager = ConfigManager.getInstance(this);
        timeManager = TimeManager.getInstance(this);
        handler = new Handler(Looper.getMainLooper());

        initViews();
        checkPasswordSetup();
        startMonitoringService();
        startUIUpdate();
        updateChildDisplay();
    }

    private void initViews() {
        // 孩子选择
        tvChildName = findViewById(R.id.tv_child_name);
        btnPrevChild = findViewById(R.id.btn_prev_child);
        btnNextChild = findViewById(R.id.btn_next_child);

        // 状态显示
        tvStatus = findViewById(R.id.tv_status);
        tvTodayTotal = findViewById(R.id.tv_today_total);
        tvRemainingTime = findViewById(R.id.tv_remaining_time);
        tvSessionTime = findViewById(R.id.tv_session_time);
        tvProgressText = findViewById(R.id.tv_progress_text);
        progressTime = findViewById(R.id.progress_time);

        // 操作按钮
        btnSettings = findViewById(R.id.btn_settings);
        btnReward = findViewById(R.id.btn_reward);
        btnReport = findViewById(R.id.btn_report);
        btnStartStop = findViewById(R.id.btn_start_stop);

        // 设置点击事件
        btnPrevChild.setOnClickListener(v -> switchToPreviousChild());
        btnNextChild.setOnClickListener(v -> switchToNextChild());
        btnSettings.setOnClickListener(v -> openSettings());
        btnReward.setOnClickListener(v -> openReward());
        btnReport.setOnClickListener(v -> openReport());
        btnStartStop.setOnClickListener(v -> toggleService());

        // 遥控器导航
        setupNavigation();

        // 初始焦点
        btnSettings.requestFocus();
    }

    private void setupNavigation() {
        // 简化的导航设置
        btnSettings.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnReward.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnNextChild.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnReward.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnSettings.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnReport.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnReport.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnReward.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnStartStop.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnStartStop.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnReport.requestFocus();
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

    private void switchToNextChild() {
        configManager.switchToNextChild();
        updateChildDisplay();
        updateUI();
    }

    private void switchToPreviousChild() {
        configManager.switchToPreviousChild();
        updateChildDisplay();
        updateUI();
    }

    private void updateChildDisplay() {
        ChildProfile child = configManager.getCurrentChild();
        tvChildName.setText(child.getName());
    }

    private void startMonitoringService() {
        try {
            Intent serviceIntent = new Intent(this, WatchdogService.class);
            startForegroundService(serviceIntent);
            isServiceRunning = true;
            updateServiceButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMonitoringService() {
        try {
            Intent serviceIntent = new Intent(this, WatchdogService.class);
            stopService(serviceIntent);
            isServiceRunning = false;
            updateServiceButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleService() {
        if (isServiceRunning) {
            stopMonitoringService();
        } else {
            startMonitoringService();
        }
    }

    private void updateServiceButton() {
        if (isServiceRunning) {
            btnStartStop.setText("⏹ 停止");
            tvStatus.setText("● 监控中");
            tvStatus.setTextColor(getResources().getColor(R.color.accent));
        } else {
            btnStartStop.setText("▶ 启动");
            tvStatus.setText("● 已停止");
            tvStatus.setTextColor(getResources().getColor(R.color.error));
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openReward() {
        Intent intent = new Intent(this, RewardActivity.class);
        startActivity(intent);
    }

    private void openReport() {
        Intent intent = new Intent(this, ReportActivity.class);
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
        try {
            // 获取今日总时长
            long todayTotal = configManager.getTodayTotalSeconds();
            long sessionSeconds = timeManager.getSessionSeconds();
            long remaining = configManager.getDailyLimit(isWeekend() ? "weekend" : "workday") * 60L - todayTotal;
            remaining = Math.max(0, remaining);

            // 更新时间显示
            tvTodayTotal.setText(TimeManager.formatTime(todayTotal));
            tvSessionTime.setText(TimeManager.formatTime(sessionSeconds));
            tvRemainingTime.setText(TimeManager.formatTime(remaining));

            // 更新进度条
            long dailyLimit = configManager.getDailyLimit(isWeekend() ? "weekend" : "workday") * 60L;
            if (dailyLimit > 0) {
                int progress = (int) (todayTotal * 100 / dailyLimit);
                progress = Math.min(progress, 100);
                progress = Math.max(0, progress);
                progressTime.setProgress(progress);
                tvProgressText.setText("已使用 " + progress + "%");
            } else {
                progressTime.setProgress(0);
                tvProgressText.setText("已使用 0%");
            }

            // 更新锁定状态
            if (configManager.isLocked()) {
                tvStatus.setText("● 已锁定");
                tvStatus.setTextColor(getResources().getColor(R.color.error));
            } else if (isServiceRunning) {
                tvStatus.setText("● 监控中");
                tvStatus.setTextColor(getResources().getColor(R.color.accent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isWeekend() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        return dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新所有显示
        updateChildDisplay();
        updateUI();
        updateServiceButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 可以在这里保存状态
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
