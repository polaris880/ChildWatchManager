package com.childwatch.manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlertActivity extends AppCompatActivity {

    private TextView tvMessage;
    private Button btnDismiss;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        handler = new Handler(Looper.getMainLooper());

        initViews();
        loadMessage();
        autoDismiss();
    }

    private void initViews() {
        tvMessage = findViewById(R.id.tv_alert_message);
        btnDismiss = findViewById(R.id.btn_dismiss);

        btnDismiss.setOnClickListener(v -> finish());

        // 初始焦点
        btnDismiss.requestFocus();
    }

    private void loadMessage() {
        String message = getIntent().getStringExtra("message");
        if (message != null) {
            tvMessage.setText(message);
        }
    }

    private void autoDismiss() {
        handler.postDelayed(this::finish, 5000); // 5秒后自动关闭
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 按任意键关闭
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ENTER ||
            keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
