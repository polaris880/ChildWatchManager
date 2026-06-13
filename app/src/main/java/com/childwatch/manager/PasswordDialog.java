package com.childwatch.manager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_set_password);

        initViews();
        setupListeners();
        updateDisplay();
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

        btnConfirm.setOnClickListener(v -> {
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
        });

        btnClear.setOnClickListener(v -> {
            currentDigitIndex = 0;
            currentDigitValue = 0;
            for (TextView digitView : digitViews) {
                digitView.setText("-");
            }
            tvCurrentDigit.setText("0");
            tvHint.setText("请设置第1位密码");
        });
    }

    private void updateDisplay() {
        tvHint.setText("请设置第" + (currentDigitIndex + 1) + "位密码");
    }
}
