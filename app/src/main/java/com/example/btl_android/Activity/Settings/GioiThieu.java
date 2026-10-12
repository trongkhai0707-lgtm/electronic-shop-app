package com.example.btl_android.Activity.Settings;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GioiThieu extends AppCompatActivity {

    TextView tvTerms;
    ImageView btnBack;
    private static final String FILE_NAME = "gioi_thieu.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.setting_gioithieu);

        btnBack = findViewById(R.id.ivBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        tvTerms = findViewById(R.id.tvTerms);
        String fileContent = readFromAssets();
        tvTerms.setText(fileContent);
        tvTerms.setTextColor(getResources().getColor(R.color.black));
    }

    private String readFromAssets() {
        StringBuilder text = new StringBuilder();

        try {
            InputStream is = getAssets().open(GioiThieu.FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while (true) {
                try {
                    if ((line = reader.readLine()) == null) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                text.append(line).append("\n");
            }

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }
}