package com.childwatch.manager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
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

        // 禁用软键盘自动弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        configManager = ConfigManager.getInstance(this);

        initViews();
        loadSettings();
        setupListeners();
        setupNavigation();

        // 设置初始焦点
        btnWorkdayMinus.requestFocus();
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

        // 修改密码 - 使用数字选择器
        btnChangePassword.setOnClickListener(v -> {
            showPasswordDialog();
        });

        // 保存设置
        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void setupNavigation() {
        // 工作日区域
        btnWorkdayMinus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                btnWorkdayPlus.requestFocus();
                return true;
            } else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                btnWeekendMinus.requestFocus();
                return true;
            }
            return false;
        });

        btnWorkdayPlus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnWorkdayMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnWeekendPlus.requestFocus();
                    return true;
                }
            }
            return false;
        });

        // 周末区域
        btnWeekendMinus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnWeekendPlus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnWorkdayMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnSingleMinus.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnWeekendPlus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnWeekendMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnWorkdayPlus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnSinglePlus.requestFocus();
                    return true;
                }
            }
            return false;
        });

        // 单次限制区域
        btnSingleMinus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnSinglePlus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnWeekendMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnIntervalWatchMinus.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnSinglePlus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnSingleMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnWeekendPlus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnIntervalWatchPlus.requestFocus();
                    return true;
                }
            }
            return false;
        });

        // 观看间隔区域
        btnIntervalWatchMinus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnIntervalWatchPlus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnSingleMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnIntervalRestMinus.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnIntervalWatchPlus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnIntervalWatchMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnSinglePlus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnIntervalRestPlus.requestFocus();
                    return true;
                }
            }
            return false;
        });

        // 休息时长区域
        btnIntervalRestMinus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnIntervalRestPlus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnIntervalWatchMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    switchAutoStart.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnIntervalRestPlus.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnIntervalRestMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnIntervalWatchPlus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    switchMonitoring.requestFocus();
                    return true;
                }
            }
            return false;
        });

        // 开关区域
        switchAutoStart.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnIntervalRestMinus.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    switchMonitoring.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    switchMonitoring.requestFocus();
                    return true;
                }
            }
            return false;
        });

        switchMonitoring.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    switchAutoStart.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnChangePassword.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    switchAutoStart.requestFocus();
                    return true;
                }
            }
            return false;
        });

        // 按钮区域
        btnChangePassword.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    switchMonitoring.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnSave.requestFocus();
                    return true;
                }
            }
            return false;
        });

        btnSave.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    switchMonitoring.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnChangePassword.requestFocus();
                    return true;
                }
            }
            return false;
        });
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
