package com.childwatch.manager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.childwatch.manager.utils.ConfigManager;

public class SettingsActivity extends AppCompatActivity {

    private ConfigManager configManager;

    // 工作日
    private Button btnWorkdayMinus, btnWorkdayPlus;
    private TextView tvWorkdayValue;
    private int workdayValue = 120;

    // 周末
    private Button btnWeekendMinus, btnWeekendPlus;
    private TextView tvWeekendValue;
    private int weekendValue = 180;

    // 单次限制
    private Button btnSingleMinus, btnSinglePlus;
    private TextView tvSingleValue;
    private int singleValue = 30;

    // 观看间隔
    private Button btnIntervalWatchMinus, btnIntervalWatchPlus;
    private TextView tvIntervalWatchValue;
    private int intervalWatchValue = 30;

    // 休息时长
    private Button btnIntervalRestMinus, btnIntervalRestPlus;
    private TextView tvIntervalRestValue;
    private int intervalRestValue = 5;

    // 开关
    private Switch switchAutoStart, switchMonitoring;

    // 按钮
    private Button btnChangePassword, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        configManager = ConfigManager.getInstance(this);

        initViews();
        loadSettings();
        setupListeners();
    }

    private void initViews() {
        // 工作日
        btnWorkdayMinus = findViewById(R.id.btn_workday_minus);
        btnWorkdayPlus = findViewById(R.id.btn_workday_plus);
        tvWorkdayValue = findViewById(R.id.tv_workday_value);

        // 周末
        btnWeekendMinus = findViewById(R.id.btn_weekend_minus);
        btnWeekendPlus = findViewById(R.id.btn_weekend_plus);
        tvWeekendValue = findViewById(R.id.tv_weekend_value);

        // 单次限制
        btnSingleMinus = findViewById(R.id.btn_single_minus);
        btnSinglePlus = findViewById(R.id.btn_single_plus);
        tvSingleValue = findViewById(R.id.tv_single_value);

        // 观看间隔
        btnIntervalWatchMinus = findViewById(R.id.btn_interval_watch_minus);
        btnIntervalWatchPlus = findViewById(R.id.btn_interval_watch_plus);
        tvIntervalWatchValue = findViewById(R.id.tv_interval_watch_value);

        // 休息时长
        btnIntervalRestMinus = findViewById(R.id.btn_interval_rest_minus);
        btnIntervalRestPlus = findViewById(R.id.btn_interval_rest_plus);
        tvIntervalRestValue = findViewById(R.id.tv_interval_rest_value);

        // 开关
        switchAutoStart = findViewById(R.id.switch_auto_start);
        switchMonitoring = findViewById(R.id.switch_monitoring);

        // 按钮
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnSave = findViewById(R.id.btn_save);
    }

    private void loadSettings() {
        workdayValue = configManager.getDailyLimit("workday");
        weekendValue = configManager.getDailyLimit("weekend");
        singleValue = configManager.getSingleLimit();
        intervalWatchValue = configManager.getIntervalWatch();
        intervalRestValue = configManager.getIntervalRest();

        tvWorkdayValue.setText(String.valueOf(workdayValue));
        tvWeekendValue.setText(String.valueOf(weekendValue));
        tvSingleValue.setText(String.valueOf(singleValue));
        tvIntervalWatchValue.setText(String.valueOf(intervalWatchValue));
        tvIntervalRestValue.setText(String.valueOf(intervalRestValue));

        switchAutoStart.setChecked(configManager.isAutoStartEnabled());
        switchMonitoring.setChecked(configManager.isMonitoringEnabled());
    }

    private void setupListeners() {
        // 工作日 -/+
        btnWorkdayMinus.setOnClickListener(v -> {
            if (workdayValue > 10) {
                workdayValue -= 10;
                tvWorkdayValue.setText(String.valueOf(workdayValue));
            }
        });

        btnWorkdayPlus.setOnClickListener(v -> {
            if (workdayValue < 480) {
                workdayValue += 10;
                tvWorkdayValue.setText(String.valueOf(workdayValue));
            }
        });

        // 周末 -/+
        btnWeekendMinus.setOnClickListener(v -> {
            if (weekendValue > 10) {
                weekendValue -= 10;
                tvWeekendValue.setText(String.valueOf(weekendValue));
            }
        });

        btnWeekendPlus.setOnClickListener(v -> {
            if (weekendValue < 480) {
                weekendValue += 10;
                tvWeekendValue.setText(String.valueOf(weekendValue));
            }
        });

        // 单次限制 -/+
        btnSingleMinus.setOnClickListener(v -> {
            if (singleValue > 5) {
                singleValue -= 5;
                tvSingleValue.setText(String.valueOf(singleValue));
            }
        });

        btnSinglePlus.setOnClickListener(v -> {
            if (singleValue < 120) {
                singleValue += 5;
                tvSingleValue.setText(String.valueOf(singleValue));
            }
        });

        // 观看间隔 -/+
        btnIntervalWatchMinus.setOnClickListener(v -> {
            if (intervalWatchValue > 5) {
                intervalWatchValue -= 5;
                tvIntervalWatchValue.setText(String.valueOf(intervalWatchValue));
            }
        });

        btnIntervalWatchPlus.setOnClickListener(v -> {
            if (intervalWatchValue < 120) {
                intervalWatchValue += 5;
                tvIntervalWatchValue.setText(String.valueOf(intervalWatchValue));
            }
        });

        // 休息时长 -/+
        btnIntervalRestMinus.setOnClickListener(v -> {
            if (intervalRestValue > 1) {
                intervalRestValue -= 1;
                tvIntervalRestValue.setText(String.valueOf(intervalRestValue));
            }
        });

        btnIntervalRestPlus.setOnClickListener(v -> {
            if (intervalRestValue < 30) {
                intervalRestValue += 1;
                tvIntervalRestValue.setText(String.valueOf(intervalRestValue));
            }
        });

        // 修改密码
        btnChangePassword.setOnClickListener(v -> showPasswordDialog());

        // 保存设置
        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void showPasswordDialog() {
        PasswordDialog dialog = new PasswordDialog(this, password -> {
            if (configManager.setPassword(password)) {
                Toast.makeText(this, "密码设置成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "密码设置失败", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void saveSettings() {
        configManager.setDailyLimit(workdayValue, "workday");
        configManager.setDailyLimit(weekendValue, "weekend");
        configManager.setSingleLimit(singleValue);
        configManager.setIntervalWatch(intervalWatchValue);
        configManager.setIntervalRest(intervalRestValue);
        configManager.setAutoStartEnabled(switchAutoStart.isChecked());
        configManager.setMonitoringEnabled(switchMonitoring.isChecked());

        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
        finish();
    }
}
