package com.example.btl_android.Activity.Settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.btl_android.R;

public class ThongBao extends AppCompatActivity {

    public static final String CHANNEL_ID = "thong_bao";
    public static final String PREFS_NAME = "ThongBaoPrefs";
    public static final String KEY_APP_SWITCH = "app_switch";
    public static final String KEY_MAIL_SWITCH = "mail_switch";

    ImageView btnBack;
    SwitchCompat swcApp, swcMail;
    SharedPreferences sharedPreferences;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.setting_thongbao);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        btnBack = findViewById(R.id.ivBack);
        swcApp = findViewById(R.id.swApp);
        swcMail = findViewById(R.id.swEmail);

        btnBack.setOnClickListener(v -> onBackPressed());

        saveSwitchState();

        createNotificationChannel();

        swcApp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_APP_SWITCH, isChecked).apply();

            if (isChecked) {
                Toast.makeText(this, "Bật thông báo", Toast.LENGTH_SHORT).show();

                // Tạo và gửi thông báo
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("My notification")
                        .setContentText("Đã bật thông báo")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            }
            else {
                Toast.makeText(this, "Tắt thông báo", Toast.LENGTH_SHORT).show();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.cancelAll();
            }
        });

        swcMail.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_MAIL_SWITCH, isChecked).apply();

            if (isChecked)
                Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "ThongBao";
            String description = "ThongBao";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected void saveSwitchState() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isAppSwitchOn = sharedPreferences.getBoolean(KEY_APP_SWITCH, false);
        boolean isMailSwitchOn = sharedPreferences.getBoolean(KEY_MAIL_SWITCH, false);

        swcApp.setChecked(isAppSwitchOn);
        swcMail.setChecked(isMailSwitchOn);
    }

}