package com.childwatch.manager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.childwatch.manager.utils.ConfigManager;
import com.childwatch.manager.utils.TimeManager;

public class ReportActivity extends AppCompatActivity {

    private ConfigManager configManager;
    private TimeManager timeManager;

    private TextView tvTodayDuration, tvTodayCount, tvTodayRest;
    private TextView tvWeekDuration, tvWeekAverage, tvWeekReward;
    private TextView tvEvaluation, tvSuggestion;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        configManager = ConfigManager.getInstance(this);
        timeManager = TimeManager.getInstance(this);

        initViews();
        loadReportData();
        setupListeners();
    }

    private void initViews() {
        tvTodayDuration = findViewById(R.id.tv_today_duration);
        tvTodayCount = findViewById(R.id.tv_today_count);
        tvTodayRest = findViewById(R.id.tv_today_rest);
        tvWeekDuration = findViewById(R.id.tv_week_duration);
        tvWeekAverage = findViewById(R.id.tv_week_average);
        tvWeekReward = findViewById(R.id.tv_week_reward);
        tvEvaluation = findViewById(R.id.tv_evaluation);
        tvSuggestion = findViewById(R.id.tv_suggestion);
        btnBack = findViewById(R.id.btn_back);

        btnBack.requestFocus();
    }

    private void loadReportData() {
        // 今日统计
        long todaySeconds = configManager.getTodayTotalSeconds();
        int todayCount = configManager.getTodaySessionCount();
        int todayRest = configManager.getTodayRestCount();

        tvTodayDuration.setText(formatMinutes(todaySeconds / 60));
        tvTodayCount.setText(todayCount + "次");
        tvTodayRest.setText(todayRest + "次");

        // 本周统计
        long weekSeconds = configManager.getWeekTotalSeconds();
        long weekDays = configManager.getWeekDays();
        int weekReward = configManager.getWeekRewardUsed();

        tvWeekDuration.setText(formatMinutes(weekSeconds / 60));
        if (weekDays > 0) {
            tvWeekAverage.setText(formatMinutes((weekSeconds / 60) / weekDays));
        } else {
            tvWeekAverage.setText("0分钟");
        }
        tvWeekReward.setText(weekReward + "分钟");

        // 习惯评估
        generateEvaluation(todaySeconds, weekSeconds);
    }

    private void generateEvaluation(long todaySeconds, long weekSeconds) {
        long todayMinutes = todaySeconds / 60;
        long weekMinutes = weekSeconds / 60;

        if (todayMinutes <= 60) {
            tvEvaluation.setText("👍 今日观看时间控制良好！");
            tvSuggestion.setText("继续保持，可以适当奖励孩子");
        } else if (todayMinutes <= 120) {
            tvEvaluation.setText("👌 今日观看时间适中");
            tvSuggestion.setText("建议多安排户外活动时间");
        } else {
            tvEvaluation.setText("⚠️ 今日观看时间较长");
            tvSuggestion.setText("请注意休息，保护视力");
        }
    }

    private String formatMinutes(long minutes) {
        if (minutes < 60) {
            return minutes + "分钟";
        } else {
            long hours = minutes / 60;
            long mins = minutes % 60;
            if (mins == 0) {
                return hours + "小时";
            } else {
                return hours + "小时" + mins + "分钟";
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
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
