package com.childwatch.manager.models;

import java.io.Serializable;

public class ChildProfile implements Serializable {

    private String id;
    private String name;
    private int dailyLimitWorkday;  // 分钟
    private int dailyLimitWeekend;
    private int singleLimit;
    private int intervalWatch;
    private int intervalRest;
    private int bonusMinutes;       // 奖励时间

    public ChildProfile() {
        this.id = "";
        this.name = "";
        this.dailyLimitWorkday = 120;
        this.dailyLimitWeekend = 180;
        this.singleLimit = 30;
        this.intervalWatch = 30;
        this.intervalRest = 5;
        this.bonusMinutes = 0;
    }

    public ChildProfile(String id, String name) {
        this.id = id;
        this.name = name;
        this.dailyLimitWorkday = 120;
        this.dailyLimitWeekend = 180;
        this.singleLimit = 30;
        this.intervalWatch = 30;
        this.intervalRest = 5;
        this.bonusMinutes = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDailyLimitWorkday() { return dailyLimitWorkday; }
    public void setDailyLimitWorkday(int minutes) { this.dailyLimitWorkday = minutes; }

    public int getDailyLimitWeekend() { return dailyLimitWeekend; }
    public void setDailyLimitWeekend(int minutes) { this.dailyLimitWeekend = minutes; }

    public int getSingleLimit() { return singleLimit; }
    public void setSingleLimit(int minutes) { this.singleLimit = minutes; }

    public int getIntervalWatch() { return intervalWatch; }
    public void setIntervalWatch(int minutes) { this.intervalWatch = minutes; }

    public int getIntervalRest() { return intervalRest; }
    public void setIntervalRest(int minutes) { this.intervalRest = minutes; }

    public int getBonusMinutes() { return bonusMinutes; }
    public void setBonusMinutes(int minutes) { this.bonusMinutes = minutes; }
    public void addBonusMinutes(int minutes) { this.bonusMinutes += minutes; }
    public void useBonusMinutes(int minutes) {
        this.bonusMinutes = Math.max(0, this.bonusMinutes - minutes);
    }

    public int getTotalDailyLimit(boolean isWeekend) {
        int base = isWeekend ? dailyLimitWeekend : dailyLimitWorkday;
        return base + bonusMinutes;
    }
}
