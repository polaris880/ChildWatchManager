package com.childwatch.manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    public void onBackPressed() {
        // 禁用返回键
    }
}
