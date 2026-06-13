package com.childwatch.manager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        super(context, R.style.DialogTheme);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置窗口属性
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_set_password);

        // 设置窗口大小和位置
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);

            // 禁用软键盘
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        setCancelable(true);
        setCanceledOnTouchOutside(false);

        initViews();
        setupListeners();
        updateDisplay();

        // 设置初始焦点
        if (btnUp != null) {
            btnUp.requestFocus();
        }
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
        // 增加按钮
        if (btnUp != null) {
            btnUp.setOnClickListener(v -> {
                if (currentDigitValue < 9) {
                    currentDigitValue++;
                    if (tvCurrentDigit != null) {
                        tvCurrentDigit.setText(String.valueOf(currentDigitValue));
                    }
                }
            });
        }

        // 减少按钮
        if (btnDown != null) {
            btnDown.setOnClickListener(v -> {
                if (currentDigitValue > 0) {
                    currentDigitValue--;
                    if (tvCurrentDigit != null) {
                        tvCurrentDigit.setText(String.valueOf(currentDigitValue));
                    }
                }
            });
        }

        // 确认按钮
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> confirmCurrentDigit());
        }

        // 清除按钮
        if (btnClear != null) {
            btnClear.setOnClickListener(v -> clearAll());
        }
    }

    private void confirmCurrentDigit() {
        if (currentDigitIndex >= 0 && currentDigitIndex < 6) {
            passwordDigits[currentDigitIndex] = currentDigitValue;

            if (digitViews[currentDigitIndex] != null) {
                digitViews[currentDigitIndex].setText(String.valueOf(currentDigitValue));
            }

            if (currentDigitIndex < 5) {
                // 还有位数需要设置
                currentDigitIndex++;
                currentDigitValue = 0;

                if (tvCurrentDigit != null) {
                    tvCurrentDigit.setText("0");
                }

                updateDisplay();
            } else {
                // 所有位数设置完成
                StringBuilder password = new StringBuilder();
                for (int digit : passwordDigits) {
                    password.append(digit);
                }

                String passwordStr = password.toString();

                // 回调
                if (callback != null) {
                    callback.onPasswordSet(passwordStr);
                }

                // 关闭对话框
                dismiss();
            }
        }
    }

    private void clearAll() {
        currentDigitIndex = 0;
        currentDigitValue = 0;

        for (int i = 0; i < digitViews.length; i++) {
            if (digitViews[i] != null) {
                digitViews[i].setText("-");
            }
        }

        if (tvCurrentDigit != null) {
            tvCurrentDigit.setText("0");
        }

        updateDisplay();

        if (btnUp != null) {
            btnUp.requestFocus();
        }
    }

    private void updateDisplay() {
        if (tvHint != null) {
            tvHint.setText("第" + (currentDigitIndex + 1) + "位");
        }
    }
}
