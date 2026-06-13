package com.childwatch.manager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.childwatch.manager.utils.ConfigManager;
import com.childwatch.manager.utils.TimeManager;

public class LockActivity extends AppCompatActivity {

    private ConfigManager configManager;
    private TimeManager timeManager;

    private TextView tvLockMessage;
    private TextView tvTodayTotal;
    private EditText etPassword;
    private Button btnUnlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        configManager = ConfigManager.getInstance(this);
        timeManager = TimeManager.getInstance(this);

        initViews();
        updateLockInfo();
    }

    private void initViews() {
        tvLockMessage = findViewById(R.id.tv_lock_message);
        tvTodayTotal = findViewById(R.id.tv_today_total);
        etPassword = findViewById(R.id.et_password);
        btnUnlock = findViewById(R.id.btn_unlock);

        btnUnlock.setOnClickListener(v -> attemptUnlock());
    }

    private void updateLockInfo() {
        long todayTotal = timeManager.getTodayTotalSeconds();
        tvTodayTotal.setText("今日已观看: " + TimeManager.formatTime(todayTotal));
        tvLockMessage.setText("观看时间已到，请输入家长密码解锁");
    }

    private void attemptUnlock() {
        String password = etPassword.getText().toString();

        if (password.length() != 6) {
            Toast.makeText(this, "请输入6位数字密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (configManager.verifyPassword(password)) {
            configManager.setLocked(false);
            Toast.makeText(this, "解锁成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        // 禁用返回键
    }
}
