package com.childwatch.manager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.childwatch.manager.models.ChildProfile;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    private static final String PREF_NAME = "ChildWatchConfig";
    private static final String KEY_PARENT_PASSWORD_HASH = "parent_password_hash";
    private static final String KEY_PASSWORD_SALT = "password_salt";
    private static final String KEY_AUTO_START = "auto_start_enabled";
    private static final String KEY_MONITORING = "monitoring_enabled";
    private static final String KEY_IS_LOCKED = "is_locked";
    private static final String KEY_CURRENT_CHILD_INDEX = "current_child_index";
    private static final String KEY_CHILDREN_LIST = "children_list";

    // 每日统计
    private static final String KEY_TODAY_TOTAL = "today_total_seconds";
    private static final String KEY_TODAY_RESET_TIME = "today_reset_time";
    private static final String KEY_TODAY_SESSION_COUNT = "today_session_count";
    private static final String KEY_TODAY_REST_COUNT = "today_rest_count";

    // 本周统计
    private static final String KEY_WEEK_TOTAL = "week_total_seconds";
    private static final String KEY_WEEK_START = "week_start_time";
    private static final String KEY_WEEK_REWARD_USED = "week_reward_used";

    // 奖励相关
    private static final String KEY_TODAY_REWARDS = "today_rewards";
    private static final String KEY_BONUS_MINUTES = "bonus_minutes";

    private static ConfigManager instance;
    private SharedPreferences prefs;
    private Context context;
    private List<ChildProfile> children;
    private int currentChildIndex;

    private ConfigManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.children = new ArrayList<>();
        initializeDefaults();
        loadChildren();
    }

    public static synchronized ConfigManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigManager(context);
        }
        return instance;
    }

    private void initializeDefaults() {
        if (!prefs.contains(KEY_CHILDREN_LIST)) {
            // 默认创建一个孩子
            ChildProfile defaultChild = new ChildProfile("child_1", "宝宝");
            List<ChildProfile> defaultList = new ArrayList<>();
            defaultList.add(defaultChild);
            saveChildren(defaultList);

            prefs.edit()
                .putBoolean(KEY_AUTO_START, true)
                .putBoolean(KEY_MONITORING, true)
                .putLong(KEY_TODAY_TOTAL, 0)
                .putBoolean(KEY_IS_LOCKED, false)
                .putInt(KEY_CURRENT_CHILD_INDEX, 0)
                .apply();
        }
    }

    // ==================== 孩子管理 ====================

    private void loadChildren() {
        String childrenStr = prefs.getString(KEY_CHILDREN_LIST, "");
        children = new ArrayList<>();

        if (childrenStr.isEmpty()) {
            // 默认孩子
            children.add(new ChildProfile("child_1", "宝宝"));
        } else {
            // 简单解析（实际应用中应使用JSON）
            String[] parts = childrenStr.split("\\|");
            for (int i = 0; i < parts.length; i += 2) {
                if (i + 1 < parts.length) {
                    children.add(new ChildProfile(parts[i], parts[i + 1]));
                }
            }
        }

        currentChildIndex = prefs.getInt(KEY_CURRENT_CHILD_INDEX, 0);
        if (currentChildIndex >= children.size()) {
            currentChildIndex = 0;
        }
    }

    private void saveChildren(List<ChildProfile> children) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) sb.append("|");
            sb.append(children.get(i).getId());
            sb.append("|");
            sb.append(children.get(i).getName());
        }
        prefs.edit().putString(KEY_CHILDREN_LIST, sb.toString()).apply();
    }

    public List<ChildProfile> getChildren() {
        return children;
    }

    public ChildProfile getCurrentChild() {
        if (children.isEmpty()) {
            return new ChildProfile("child_1", "宝宝");
        }
        return children.get(currentChildIndex);
    }

    public int getCurrentChildIndex() {
        return currentChildIndex;
    }

    public void switchToNextChild() {
        if (!children.isEmpty()) {
            currentChildIndex = (currentChildIndex + 1) % children.size();
            prefs.edit().putInt(KEY_CURRENT_CHILD_INDEX, currentChildIndex).apply();
        }
    }

    public void switchToPreviousChild() {
        if (!children.isEmpty()) {
            currentChildIndex = (currentChildIndex - 1 + children.size()) % children.size();
            prefs.edit().putInt(KEY_CURRENT_CHILD_INDEX, currentChildIndex).apply();
        }
    }

    public void addChild(String name) {
        String id = "child_" + System.currentTimeMillis();
        ChildProfile newChild = new ChildProfile(id, name);
        children.add(newChild);
        saveChildren(children);
    }

    public void updateChild(int index, ChildProfile profile) {
        if (index >= 0 && index < children.size()) {
            children.set(index, profile);
            saveChildren(children);
        }
    }

    // ==================== 每日限制 ====================

    public int getDailyLimit(String dayType) {
        ChildProfile child = getCurrentChild();
        if ("weekend".equals(dayType)) {
            return child.getDailyLimitWeekend() + child.getBonusMinutes();
        } else {
            return child.getDailyLimitWorkday() + child.getBonusMinutes();
        }
    }

    public void setDailyLimit(int minutes, String dayType) {
        ChildProfile child = getCurrentChild();
        if ("weekend".equals(dayType)) {
            child.setDailyLimitWeekend(minutes);
        } else {
            child.setDailyLimitWorkday(minutes);
        }
        updateChild(currentChildIndex, child);
    }

    public int getSingleLimit() {
        return getCurrentChild().getSingleLimit();
    }

    public void setSingleLimit(int minutes) {
        ChildProfile child = getCurrentChild();
        child.setSingleLimit(minutes);
        updateChild(currentChildIndex, child);
    }

    public int getIntervalWatch() {
        return getCurrentChild().getIntervalWatch();
    }

    public void setIntervalWatch(int minutes) {
        ChildProfile child = getCurrentChild();
        child.setIntervalWatch(minutes);
        updateChild(currentChildIndex, child);
    }

    public int getIntervalRest() {
        return getCurrentChild().getIntervalRest();
    }

    public void setIntervalRest(int minutes) {
        ChildProfile child = getCurrentChild();
        child.setIntervalRest(minutes);
        updateChild(currentChildIndex, child);
    }

    // ==================== 奖励系统 ====================

    public int getBonusMinutes() {
        return getCurrentChild().getBonusMinutes();
    }

    public void addBonusMinutes(int minutes) {
        ChildProfile child = getCurrentChild();
        child.addBonusMinutes(minutes);
        updateChild(currentChildIndex, child);
    }

    public boolean hasClaimedRewardToday(String rewardType) {
        Set<String> rewards = prefs.getStringSet(KEY_TODAY_REWARDS, new HashSet<>());
        return rewards.contains(rewardType);
    }

    public void claimReward(String rewardType, int minutes) {
        Set<String> rewards = new HashSet<>(prefs.getStringSet(KEY_TODAY_REWARDS, new HashSet<>()));
        rewards.add(rewardType);
        prefs.edit().putStringSet(KEY_TODAY_REWARDS, rewards).apply();

        addBonusMinutes(minutes);
    }

    public int getTodayRewardMinutes() {
        ChildProfile child = getCurrentChild();
        return child.getBonusMinutes();
    }

    // ==================== 每日统计 ====================

    public long getTodayTotalSeconds() {
        checkDayReset();
        return prefs.getLong(KEY_TODAY_TOTAL + "_" + getCurrentChild().getId(), 0);
    }

    public void addTodayTotalSeconds(long seconds) {
        String key = KEY_TODAY_TOTAL + "_" + getCurrentChild().getId();
        long current = prefs.getLong(key, 0);
        prefs.edit().putLong(key, current + seconds).apply();
    }

    public int getTodaySessionCount() {
        return prefs.getInt(KEY_TODAY_SESSION_COUNT + "_" + getCurrentChild().getId(), 0);
    }

    public void incrementSessionCount() {
        String key = KEY_TODAY_SESSION_COUNT + "_" + getCurrentChild().getId();
        int current = prefs.getInt(key, 0);
        prefs.edit().putInt(key, current + 1).apply();
    }

    public int getTodayRestCount() {
        return prefs.getInt(KEY_TODAY_REST_COUNT + "_" + getCurrentChild().getId(), 0);
    }

    public void incrementRestCount() {
        String key = KEY_TODAY_REST_COUNT + "_" + getCurrentChild().getId();
        int current = prefs.getInt(key, 0);
        prefs.edit().putInt(key, current + 1).apply();
    }

    // ==================== 本周统计 ====================

    public long getWeekTotalSeconds() {
        checkWeekReset();
        return prefs.getLong(KEY_WEEK_TOTAL + "_" + getCurrentChild().getId(), 0);
    }

    public void addWeekTotalSeconds(long seconds) {
        String key = KEY_WEEK_TOTAL + "_" + getCurrentChild().getId();
        long current = prefs.getLong(key, 0);
        prefs.edit().putLong(key, current + seconds).apply();
    }

    public long getWeekDays() {
        long weekStart = prefs.getLong(KEY_WEEK_START, 0);
        if (weekStart == 0) return 1;
        long diff = System.currentTimeMillis() - weekStart;
        return Math.max(1, diff / (24 * 60 * 60 * 1000));
    }

    public int getWeekRewardUsed() {
        return prefs.getInt(KEY_WEEK_REWARD_USED + "_" + getCurrentChild().getId(), 0);
    }

    public void addWeekRewardUsed(int minutes) {
        String key = KEY_WEEK_REWARD_USED + "_" + getCurrentChild().getId();
        int current = prefs.getInt(key, 0);
        prefs.edit().putInt(key, current + minutes).apply();
    }

    // ==================== 日期重置 ====================

    private void checkDayReset() {
        long lastResetTime = prefs.getLong(KEY_TODAY_RESET_TIME, 0);
        Calendar lastReset = Calendar.getInstance();
        lastReset.setTimeInMillis(lastResetTime);

        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DAY_OF_YEAR) != lastReset.get(Calendar.DAY_OF_YEAR) ||
            now.get(Calendar.YEAR) != lastReset.get(Calendar.YEAR)) {
            // 重置每日数据
            String childId = getCurrentChild().getId();
            prefs.edit()
                .putLong(KEY_TODAY_TOTAL + "_" + childId, 0)
                .putInt(KEY_TODAY_SESSION_COUNT + "_" + childId, 0)
                .putInt(KEY_TODAY_REST_COUNT + "_" + childId, 0)
                .putLong(KEY_TODAY_RESET_TIME, System.currentTimeMillis())
                .putStringSet(KEY_TODAY_REWARDS, new HashSet<>())
                .apply();

            // 重置奖励
            ChildProfile child = getCurrentChild();
            child.setBonusMinutes(0);
            updateChild(currentChildIndex, child);
        }
    }

    private void checkWeekReset() {
        long weekStart = prefs.getLong(KEY_WEEK_START, 0);
        Calendar weekStartCal = Calendar.getInstance();
        weekStartCal.setTimeInMillis(weekStart);

        Calendar now = Calendar.getInstance();

        // 检查是否是新的一周
        if (now.get(Calendar.WEEK_OF_YEAR) != weekStartCal.get(Calendar.WEEK_OF_YEAR) ||
            now.get(Calendar.YEAR) != weekStartCal.get(Calendar.YEAR)) {
            String childId = getCurrentChild().getId();
            prefs.edit()
                .putLong(KEY_WEEK_TOTAL + "_" + childId, 0)
                .putInt(KEY_WEEK_REWARD_USED + "_" + childId, 0)
                .putLong(KEY_WEEK_START, System.currentTimeMillis())
                .apply();
        }
    }

    public void resetTodayTimer() {
        String childId = getCurrentChild().getId();
        prefs.edit()
            .putLong(KEY_TODAY_TOTAL + "_" + childId, 0)
            .putLong(KEY_TODAY_RESET_TIME, System.currentTimeMillis())
            .putBoolean(KEY_IS_LOCKED, false)
            .putStringSet(KEY_TODAY_REWARDS, new HashSet<>())
            .apply();
    }

    // ==================== 密码管理 ====================

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

    // ==================== 系统配置 ====================

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

    public boolean isLocked() {
        return prefs.getBoolean(KEY_IS_LOCKED, false);
    }

    public void setLocked(boolean locked) {
        prefs.edit().putBoolean(KEY_IS_LOCKED, locked).apply();
    }

    // ==================== 辅助方法 ====================

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
}
