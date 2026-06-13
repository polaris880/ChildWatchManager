package com.childwatch.manager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class PasswordDialog extends Dialog {

    public interface PasswordCallback {
        void onPasswordSet(String password);
    }

    private PasswordCallback callback;
    private TextView[] digitViews = new TextView[6];
    private TextView tvCurrentDigit;
    private TextView tvHint;
    private Button btnUp, btnDown, btnConfirm, btnClear;

    private int[] passwordDigits = new int[6];
    private int currentDigitIndex = 0;
    private int currentDigitValue = 0;

    public PasswordDialog(Context context, PasswordCallback callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_set_password);

        // 设置窗口属性
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        // 禁用软键盘自动弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initViews();
        setupListeners();
        setupNavigation();
        updateDisplay();

        // 设置初始焦点
        btnUp.requestFocus();
    }

    private void initViews() {
        digitViews[0] = findViewById(R.id.tv_digit_1);
        digitViews[1] = findViewById(R.id.tv_digit_2);
        digitViews[2] = findViewById(R.id.tv_digit_3);
        digitViews[3] = findViewById(R.id.tv_digit_4);
        digitViews[4] = findViewById(R.id.tv_digit_5);
        digitViews[5] = findViewById(R.id.tv_digit_6);

        tvCurrentDigit = findViewById(R.id.tv_current_digit);
        tvHint = findViewById(R.id.tv_hint);

        btnUp = findViewById(R.id.btn_digit_up);
        btnDown = findViewById(R.id.btn_digit_down);
        btnConfirm = findViewById(R.id.btn_confirm_digit);
        btnClear = findViewById(R.id.btn_clear);
    }

    private void setupListeners() {
        btnUp.setOnClickListener(v -> {
            if (currentDigitValue < 9) {
                currentDigitValue++;
                tvCurrentDigit.setText(String.valueOf(currentDigitValue));
            }
        });

        btnDown.setOnClickListener(v -> {
            if (currentDigitValue > 0) {
                currentDigitValue--;
                tvCurrentDigit.setText(String.valueOf(currentDigitValue));
            }
        });

        btnConfirm.setOnClickListener(v -> confirmCurrentDigit());

        btnClear.setOnClickListener(v -> clearAll());
    }

    private void setupNavigation() {
        // 上按钮 -> 数字显示
        btnUp.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    tvCurrentDigit.requestFocus();
                    return true;
                }
            }
            return false;
        });

        // 数字显示 -> 下按钮 或 确认按钮
        tvCurrentDigit.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnUp.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnDown.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnConfirm.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnClear.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    // 确认键也可以增加数字
                    if (currentDigitValue < 9) {
                        currentDigitValue++;
                        tvCurrentDigit.setText(String.valueOf(currentDigitValue));
                    }
                    return true;
                }
            }
            return false;
        });

        // 下按钮 -> 确认按钮
        btnDown.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    tvCurrentDigit.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    btnConfirm.requestFocus();
                    return true;
                }
            }
            return false;
        });

        // 确认按钮 -> 清除按钮
        btnConfirm.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnDown.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnClear.requestFocus();
                    return true;
                }
            }
            return false;
        });

        // 清除按钮 -> 上按钮
        btnClear.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    btnDown.requestFocus();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnConfirm.requestFocus();
                    return true;
                }
            }
            return false;
        });
    }

    private void confirmCurrentDigit() {
        passwordDigits[currentDigitIndex] = currentDigitValue;
        digitViews[currentDigitIndex].setText(String.valueOf(currentDigitValue));

        if (currentDigitIndex < 5) {
            currentDigitIndex++;
            currentDigitValue = 0;
            tvCurrentDigit.setText("0");
            tvHint.setText("请设置第" + (currentDigitIndex + 1) + "位密码");
        } else {
            // 密码设置完成
            StringBuilder password = new StringBuilder();
            for (int digit : passwordDigits) {
                password.append(digit);
            }
            if (callback != null) {
                callback.onPasswordSet(password.toString());
            }
            dismiss();
        }
    }

    private void clearAll() {
        currentDigitIndex = 0;
        currentDigitValue = 0;
        for (TextView digitView : digitViews) {
            digitView.setText("-");
        }
        tvCurrentDigit.setText("0");
        tvHint.setText("请设置第1位密码");
        btnUp.requestFocus();
    }

    private void updateDisplay() {
        tvHint.setText("请设置第" + (currentDigitIndex + 1) + "位密码");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 处理遥控器按键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
