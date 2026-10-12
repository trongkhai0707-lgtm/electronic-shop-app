package com.example.btl_android.Activity.Settings;

import static com.example.btl_android.LanguageHelper.changeLocale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.Activity.Setting;
import com.example.btl_android.LanguageHelper;
import com.example.btl_android.R;

public class NgonNgu extends AppCompatActivity {

    ImageView ivBack;
    RadioButton rbEnglish, rbVietnamese;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Load ngôn ngữ đã lưu trước khi set layout
        LanguageHelper.loadLocale(this);

        setContentView(R.layout.setting_ngonngu);

        // Lấy các RadioButton từ layout
        rbEnglish = findViewById(R.id.rb_english);
        rbVietnamese = findViewById(R.id.rb_vietnamese);

        // Gọi phương thức LoadLanguage để đặt ngôn ngữ
        LoadLanguage();

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(NgonNgu.this, Setting.class);
            startActivity(intent);
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        int id = view.getId();

        if (id == R.id.rb_english && checked) {
            onLanguageSelected("en"); // Ngôn ngữ tiếng Anh
        } else if (id == R.id.rb_vietnamese && checked) {
            onLanguageSelected("vi"); // Ngôn ngữ tiếng Việt
        }
    }

    public void onLanguageSelected(String languageCode) {
        // Thay đổi ngôn ngữ và khởi động lại Activity để áp dụng thay đổi
        changeLocale(this, languageCode);

        // Lưu ngôn ngữ vào SharedPreferences để sử dụng lại khi ứng dụng được mở lại
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", languageCode);
        editor.apply();


        recreate();
    }

    private void LoadLanguage() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Mặc định là "vi"

        // Thay đổi ngôn ngữ
        changeLocale(this, languageCode);

        // Đặt ngôn ngữ mặc định cho RadioButton
        if (languageCode.equals("en")) {
            rbEnglish.setChecked(true);
        } else {
            rbVietnamese.setChecked(true);
        }
    }
}
