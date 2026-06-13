package com.childwatch.manager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.childwatch.manager.utils.ConfigManager;

public class RewardActivity extends AppCompatActivity {

    private ConfigManager configManager;

    private Button btnHomework, btnExercise, btnChores, btnReading;
    private Button btnBack;
    private TextView tvRewardTotal;

    private int totalRewardMinutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        configManager = ConfigManager.getInstance(this);

        initViews();
        loadRewardData();
        setupListeners();
    }

    private void initViews() {
        btnHomework = findViewById(R.id.btn_reward_homework);
        btnExercise = findViewById(R.id.btn_reward_exercise);
        btnChores = findViewById(R.id.btn_reward_chores);
        btnReading = findViewById(R.id.btn_reward_reading);
        btnBack = findViewById(R.id.btn_back);
        tvRewardTotal = findViewById(R.id.tv_reward_total);

        // 初始焦点
        btnHomework.requestFocus();
    }

    private void loadRewardData() {
        // 从配置加载今日已领取的奖励
        totalRewardMinutes = configManager.getTodayRewardMinutes();
        updateRewardDisplay();

        // 检查哪些奖励已领取
        if (configManager.hasClaimedRewardToday("homework")) {
            btnHomework.setEnabled(false);
            btnHomework.setText("已领取");
        }
        if (configManager.hasClaimedRewardToday("exercise")) {
            btnExercise.setEnabled(false);
            btnExercise.setText("已领取");
        }
        if (configManager.hasClaimedRewardToday("chores")) {
            btnChores.setEnabled(false);
            btnChores.setText("已领取");
        }
        if (configManager.hasClaimedRewardToday("reading")) {
            btnReading.setEnabled(false);
            btnReading.setText("已领取");
        }
    }

    private void setupListeners() {
        btnHomework.setOnClickListener(v -> claimReward("homework", 30));
        btnExercise.setOnClickListener(v -> claimReward("exercise", 20));
        btnChores.setOnClickListener(v -> claimReward("chores", 15));
        btnReading.setOnClickListener(v -> claimReward("reading", 20));

        btnBack.setOnClickListener(v -> finish());

        // 遥控器导航
        setupNavigation();
    }

    private void claimReward(String type, int minutes) {
        if (configManager.hasClaimedRewardToday(type)) {
            Toast.makeText(this, "今日已领取过该奖励", Toast.LENGTH_SHORT).show();
            return;
        }

        configManager.claimReward(type, minutes);
        totalRewardMinutes = configManager.getTodayRewardMinutes();

        updateRewardDisplay();
        Toast.makeText(this, "奖励 +" + minutes + " 分钟已领取！", Toast.LENGTH_SHORT).show();

        // 禁用已领取的按钮
        disableRewardButton(type);
    }

    private void disableRewardButton(String type) {
        switch (type) {
            case "homework":
                btnHomework.setEnabled(false);
                btnHomework.setText("已领取");
                break;
            case "exercise":
                btnExercise.setEnabled(false);
                btnExercise.setText("已领取");
                break;
            case "chores":
                btnChores.setEnabled(false);
                btnChores.setText("已领取");
                break;
            case "reading":
                btnReading.setEnabled(false);
                btnReading.setText("已领取");
                break;
        }
    }

    private void updateRewardDisplay() {
        tvRewardTotal.setText("已获得奖励: +" + totalRewardMinutes + " 分钟");
    }

    private void setupNavigation() {
        btnHomework.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                btnExercise.requestFocus();
                return true;
            }
            return false;
        });

        btnExercise.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnHomework.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnChores.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnChores.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnExercise.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnReading.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnReading.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnChores.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnBack.requestFocus();
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
