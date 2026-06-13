package com.childwatch.manager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.childwatch.manager.utils.ConfigManager;

public class SettingsActivity extends AppCompatActivity {

    private ConfigManager configManager;

    private EditText etDailyWorkday;
    private EditText etDailyWeekend;
    private EditText etDailyHoliday;
    private EditText etSingleLimit;
    private EditText etIntervalWatch;
    private EditText etIntervalRest;
    private Switch switchAutoStart;
    private Switch switchMonitoring;
    private Button btnSave;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        configManager = ConfigManager.getInstance(this);

        initViews();
        loadSettings();
    }

    private void initViews() {
        etDailyWorkday = findViewById(R.id.et_daily_workday);
        etDailyWeekend = findViewById(R.id.et_daily_weekend);
        etDailyHoliday = findViewById(R.id.et_daily_holiday);
        etSingleLimit = findViewById(R.id.et_single_limit);
        etIntervalWatch = findViewById(R.id.et_interval_watch);
        etIntervalRest = findViewById(R.id.et_interval_rest);
        switchAutoStart = findViewById(R.id.switch_auto_start);
        switchMonitoring = findViewById(R.id.switch_monitoring);
        btnSave = findViewById(R.id.btn_save);
        btnChangePassword = findViewById(R.id.btn_change_password);

        btnSave.setOnClickListener(v -> saveSettings());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void loadSettings() {
        etDailyWorkday.setText(String.valueOf(configManager.getDailyLimit("workday")));
        etDailyWeekend.setText(String.valueOf(configManager.getDailyLimit("weekend")));
        etDailyHoliday.setText(String.valueOf(configManager.getDailyLimit("holiday")));
        etSingleLimit.setText(String.valueOf(configManager.getSingleLimit()));
        etIntervalWatch.setText(String.valueOf(configManager.getIntervalWatch()));
        etIntervalRest.setText(String.valueOf(configManager.getIntervalRest()));
        switchAutoStart.setChecked(configManager.isAutoStartEnabled());
        switchMonitoring.setChecked(configManager.isMonitoringEnabled());
    }

    private void saveSettings() {
        try {
            int workday = Integer.parseInt(etDailyWorkday.getText().toString());
            int weekend = Integer.parseInt(etDailyWeekend.getText().toString());
            int holiday = Integer.parseInt(etDailyHoliday.getText().toString());
            int single = Integer.parseInt(etSingleLimit.getText().toString());
            int intervalWatch = Integer.parseInt(etIntervalWatch.getText().toString());
            int intervalRest = Integer.parseInt(etIntervalRest.getText().toString());

            if (workday <= 0 || weekend <= 0 || holiday <= 0 || single <= 0 ||
                intervalWatch <= 0 || intervalRest <= 0) {
                Toast.makeText(this, "请输入大于0的数值", Toast.LENGTH_SHORT).show();
                return;
            }

            configManager.setDailyLimit(workday, "workday");
            configManager.setDailyLimit(weekend, "weekend");
            configManager.setDailyLimit(holiday, "holiday");
            configManager.setSingleLimit(single);
            configManager.setIntervalWatch(intervalWatch);
            configManager.setIntervalRest(intervalRest);
            configManager.setAutoStartEnabled(switchAutoStart.isChecked());
            configManager.setMonitoringEnabled(switchMonitoring.isChecked());

            Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改密码");

        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etOldPassword = view.findViewById(R.id.et_old_password);
        EditText etNewPassword = view.findViewById(R.id.et_new_password);
        EditText etConfirmPassword = view.findViewById(R.id.et_confirm_password);

        builder.setView(view);
        builder.setPositiveButton("确认", (dialog, which) -> {
            String oldPassword = etOldPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (!configManager.verifyPassword(oldPassword)) {
                Toast.makeText(this, "旧密码错误", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() != 6) {
                Toast.makeText(this, "密码必须为6位数字", Toast.LENGTH_SHORT).show();
                return;
            }

            if (configManager.setPassword(newPassword)) {
                Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "密码修改失败", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
