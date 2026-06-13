package com.childwatch.manager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class ConfigManager {

    private static final String PREF_NAME = "ChildWatchConfig";
    private static final String KEY_PARENT_PASSWORD_HASH = "parent_password_hash";
    private static final String KEY_PASSWORD_SALT = "password_salt";
    private static final String KEY_DAILY_LIMIT_WORKDAY = "daily_limit_workday";
    private static final String KEY_DAILY_LIMIT_WEEKEND = "daily_limit_weekend";
    private static final String KEY_DAILY_LIMIT_HOLIDAY = "daily_limit_holiday";
    private static final String KEY_SINGLE_LIMIT = "single_limit_minutes";
    private static final String KEY_INTERVAL_WATCH = "interval_watch_minutes";
    private static final String KEY_INTERVAL_REST = "interval_rest_minutes";
    private static final String KEY_AUTO_START = "auto_start_enabled";
    private static final String KEY_MONITORING = "monitoring_enabled";
    private static final String KEY_TODAY_TOTAL = "today_total_seconds";
    private static final String KEY_TODAY_RESET_TIME = "today_reset_time";
    private static final String KEY_IS_LOCKED = "is_locked";

    private static ConfigManager instance;
    private SharedPreferences prefs;
    private Context context;

    private ConfigManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        initializeDefaults();
    }

    public static synchronized ConfigManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigManager(context);
        }
        return instance;
    }

    private void initializeDefaults() {
        if (!prefs.contains(KEY_DAILY_LIMIT_WORKDAY)) {
            prefs.edit()
                .putInt(KEY_DAILY_LIMIT_WORKDAY, 120)
                .putInt(KEY_DAILY_LIMIT_WEEKEND, 180)
                .putInt(KEY_DAILY_LIMIT_HOLIDAY, 180)
                .putInt(KEY_SINGLE_LIMIT, 30)
                .putInt(KEY_INTERVAL_WATCH, 30)
                .putInt(KEY_INTERVAL_REST, 5)
                .putBoolean(KEY_AUTO_START, true)
                .putBoolean(KEY_MONITORING, true)
                .putLong(KEY_TODAY_TOTAL, 0)
                .putBoolean(KEY_IS_LOCKED, false)
                .apply();
        }
    }

    // 密码管理
    public boolean setPassword(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return prefs.edit()
                .putString(KEY_PARENT_PASSWORD_HASH, hash)
                .putString(KEY_PASSWORD_SALT, salt)
                .commit();
    }

    public boolean verifyPassword(String password) {
        String storedHash = prefs.getString(KEY_PARENT_PASSWORD_HASH, "");
        String salt = prefs.getString(KEY_PASSWORD_SALT, "");
        if (storedHash.isEmpty() || salt.isEmpty()) {
            return false;
        }
        String inputHash = hashPassword(password, salt);
        return storedHash.equals(inputHash);
    }

    public boolean hasPassword() {
        return !prefs.getString(KEY_PARENT_PASSWORD_HASH, "").isEmpty();
    }

    // 每日时长限制
    public int getDailyLimit(String dayType) {
        switch (dayType) {
            case "workday":
                return prefs.getInt(KEY_DAILY_LIMIT_WORKDAY, 120);
            case "weekend":
                return prefs.getInt(KEY_DAILY_LIMIT_WEEKEND, 180);
            case "holiday":
                return prefs.getInt(KEY_DAILY_LIMIT_HOLIDAY, 180);
            default:
                return prefs.getInt(KEY_DAILY_LIMIT_WORKDAY, 120);
        }
    }

    public void setDailyLimit(int minutes, String dayType) {
        switch (dayType) {
            case "workday":
                prefs.edit().putInt(KEY_DAILY_LIMIT_WORKDAY, minutes).apply();
                break;
            case "weekend":
                prefs.edit().putInt(KEY_DAILY_LIMIT_WEEKEND, minutes).apply();
                break;
            case "holiday":
                prefs.edit().putInt(KEY_DAILY_LIMIT_HOLIDAY, minutes).apply();
                break;
        }
    }

    // 单次时长限制
    public int getSingleLimit() {
        return prefs.getInt(KEY_SINGLE_LIMIT, 30);
    }

    public void setSingleLimit(int minutes) {
        prefs.edit().putInt(KEY_SINGLE_LIMIT, minutes).apply();
    }

    // 间隔时长
    public int getIntervalWatch() {
        return prefs.getInt(KEY_INTERVAL_WATCH, 30);
    }

    public void setIntervalWatch(int minutes) {
        prefs.edit().putInt(KEY_INTERVAL_WATCH, minutes).apply();
    }

    public int getIntervalRest() {
        return prefs.getInt(KEY_INTERVAL_REST, 5);
    }

    public void setIntervalRest(int minutes) {
        prefs.edit().putInt(KEY_INTERVAL_REST, minutes).apply();
    }

    // 系统配置
    public boolean isAutoStartEnabled() {
        return prefs.getBoolean(KEY_AUTO_START, true);
    }

    public void setAutoStartEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_START, enabled).apply();
    }

    public boolean isMonitoringEnabled() {
        return prefs.getBoolean(KEY_MONITORING, true);
    }

    public void setMonitoringEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_MONITORING, enabled).apply();
    }

    // 计时管理
    public long getTodayTotalSeconds() {
        return prefs.getLong(KEY_TODAY_TOTAL, 0);
    }

    public void setTodayTotalSeconds(long seconds) {
        prefs.edit().putLong(KEY_TODAY_TOTAL, seconds).apply();
    }

    public void addTodayTotalSeconds(long seconds) {
        long current = getTodayTotalSeconds();
        prefs.edit().putLong(KEY_TODAY_TOTAL, current + seconds).apply();
    }

    public long getTodayResetTime() {
        return prefs.getLong(KEY_TODAY_RESET_TIME, 0);
    }

    public void setTodayResetTime(long time) {
        prefs.edit().putLong(KEY_TODAY_RESET_TIME, time).apply();
    }

    public boolean isLocked() {
        return prefs.getBoolean(KEY_IS_LOCKED, false);
    }

    public void setLocked(boolean locked) {
        prefs.edit().putBoolean(KEY_IS_LOCKED, locked).apply();
    }

    // 辅助方法
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hash = md.digest(password.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 重置今日计时
    public void resetTodayTimer() {
        prefs.edit()
            .putLong(KEY_TODAY_TOTAL, 0)
            .putLong(KEY_TODAY_RESET_TIME, System.currentTimeMillis())
            .putBoolean(KEY_IS_LOCKED, false)
            .apply();
    }
}
