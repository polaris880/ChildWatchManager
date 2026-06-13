package com.childwatch.manager.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.util.Calendar;

public class TimeManager {

    public interface TimeListener {
        void onTimeUpdate(long todayTotalSeconds, long sessionSeconds);
        void onSingleLimitReached();
        void onIntervalReached();
        void onDailyLimitReached();
        void onNearLimit(int remainingMinutes);
    }

    private static TimeManager instance;
    private Context context;
    private ConfigManager configManager;
    private Handler handler;
    private TimeListener listener;

    private long sessionStartTime;
    private long sessionSeconds;
    private boolean isSessionActive;
    private boolean isPaused;
    private long pauseTime;

    private static final int UPDATE_INTERVAL = 1000; // 1秒更新一次

    private TimeManager(Context context) {
        this.context = context.getApplicationContext();
        this.configManager = ConfigManager.getInstance(context);
        this.handler = new Handler(Looper.getMainLooper());
        this.isSessionActive = false;
        this.isPaused = false;
    }

    public static synchronized TimeManager getInstance(Context context) {
        if (instance == null) {
            instance = new TimeManager(context);
        }
        return instance;
    }

    public void setListener(TimeListener listener) {
        this.listener = listener;
    }

    // 开始新会话
    public void startSession() {
        if (!isSessionActive) {
            sessionStartTime = System.currentTimeMillis();
            sessionSeconds = 0;
            isSessionActive = true;
            isPaused = false;
            startTimer();
        }
    }

    // 暂停会话
    public void pauseSession() {
        if (isSessionActive && !isPaused) {
            isPaused = true;
            pauseTime = System.currentTimeMillis();
            stopTimer();
        }
    }

    // 恢复会话
    public void resumeSession() {
        if (isSessionActive && isPaused) {
            long pauseDuration = System.currentTimeMillis() - pauseTime;
            sessionStartTime += pauseDuration;
            isPaused = false;
            startTimer();
        }
    }

    // 停止会话
    public void stopSession() {
        isSessionActive = false;
        isPaused = false;
        stopTimer();
        sessionSeconds = 0;
    }

    // 获取今日总时长（秒）
    public long getTodayTotalSeconds() {
        checkDayReset();
        return configManager.getTodayTotalSeconds();
    }

    // 获取当前会话时长（秒）
    public long getSessionSeconds() {
        return sessionSeconds;
    }

    // 获取今日剩余时长（秒）
    public long getRemainingSeconds() {
        String dayType = getDayType();
        int dailyLimit = configManager.getDailyLimit(dayType);
        long dailyLimitSeconds = dailyLimit * 60L;
        long todayTotal = getTodayTotalSeconds();
        return Math.max(0, dailyLimitSeconds - todayTotal);
    }

    // 检查是否需要锁定
    public boolean shouldLock() {
        return getRemainingSeconds() <= 0;
    }

    // 检查单次限制
    public boolean isSingleLimitReached() {
        return sessionSeconds >= configManager.getSingleLimit() * 60L;
    }

    // 检查间隔限制
    public boolean isIntervalReached() {
        return sessionSeconds >= configManager.getIntervalWatch() * 60L;
    }

    // 获取休息时长（秒）
    public int getRestDurationSeconds() {
        return configManager.getIntervalRest() * 60;
    }

    // 检查日期重置
    private void checkDayReset() {
        long lastResetTime = configManager.getTodayResetTime();
        Calendar lastReset = Calendar.getInstance();
        lastReset.setTimeInMillis(lastResetTime);

        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DAY_OF_YEAR) != lastReset.get(Calendar.DAY_OF_YEAR) ||
            now.get(Calendar.YEAR) != lastReset.get(Calendar.YEAR)) {
            configManager.resetTodayTimer();
        }
    }

    // 获取日期类型
    private String getDayType() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return "weekend";
        } else {
            return "workday";
        }
    }

    // 计时器相关
    private Runnable timerRunnable;
    private boolean isTimerRunning;

    private void startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true;
            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isSessionActive && !isPaused) {
                        updateTimer();
                        handler.postDelayed(this, UPDATE_INTERVAL);
                    }
                }
            };
            handler.post(timerRunnable);
        }
    }

    private void stopTimer() {
        isTimerRunning = false;
        if (timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }

    private void updateTimer() {
        long currentTime = System.currentTimeMillis();
        sessionSeconds = (currentTime - sessionStartTime) / 1000;

        // 累加到今日总时长
        configManager.addTodayTotalSeconds(1);

        long todayTotal = configManager.getTodayTotalSeconds();

        // 通知监听器
        if (listener != null) {
            listener.onTimeUpdate(todayTotal, sessionSeconds);

            // 检查单次限制
            if (isSingleLimitReached()) {
                listener.onSingleLimitReached();
                stopSession();
            }

            // 检查间隔限制
            if (isIntervalReached()) {
                listener.onIntervalReached();
            }

            // 检查每日限制
            if (shouldLock()) {
                listener.onDailyLimitReached();
                stopSession();
            }

            // 接近限制提醒
            long remaining = getRemainingSeconds();
            if (remaining <= 600 && remaining > 590) { // 10分钟
                listener.onNearLimit(10);
            } else if (remaining <= 300 && remaining > 290) { // 5分钟
                listener.onNearLimit(5);
            }
        }
    }

    // 格式化时间显示
    public static String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    public boolean isSessionActive() {
        return isSessionActive;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
