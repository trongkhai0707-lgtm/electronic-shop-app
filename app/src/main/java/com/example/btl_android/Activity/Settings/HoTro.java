package com.example.btl_android.Activity.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.Activity.Setting;
import com.example.btl_android.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HoTro extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView btnBack, ivImage;
    EditText InputDescription;
    Button btnSendReport;
    LinearLayout btnChooseImage;

    String currentMail;

    // URI của ảnh đã chọn từ thư viện
    Uri imageUri;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference taikhoanRef = database.getReference("taikhoan");
    DatabaseReference reportRef = database.getReference("reports");

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.setting_hotro);

        Mapping();

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(HoTro.this, Setting.class);
            startActivity(intent);
        });

        btnChooseImage.setOnClickListener(v -> {
            // Mở thư viện ảnh của điện thoại
            openGallery();
        });

        btnSendReport.setOnClickListener(v -> {
            if (InputDescription.getText().toString().isEmpty()) {
                Toast.makeText(HoTro.this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
                return;
            }
            if (imageUri == null) {
                Toast.makeText(HoTro.this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi báo cáo
            sendReport();
            Toast.makeText(HoTro.this, "Báo cáo đã được gửi", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HoTro.this, String.class);
            startActivity(intent);
        });
    }

    protected void Mapping() {
        btnBack = findViewById(R.id.ivBack);
        InputDescription = findViewById(R.id.et_description);
        btnChooseImage = findViewById(R.id.ll_selectImage);
        btnSendReport = findViewById(R.id.btn_send_report);
        ivImage = findViewById(R.id.iv_image);
    }

    protected void sendReport() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);

        taikhoanRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentMail = task.getResult().child("email").getValue(String.class);
            }
        });

        String descrip =  InputDescription.getText().toString();

        if (imageUri != null) {
            UploadImage(imageUri, uid);
        }

        reportRef.child(uid).child("description").setValue(descrip);
        reportRef.child(uid).child("email").setValue(currentMail);
    }

    // Mở thư viện ảnh trên điện thoại
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sử dụng setDataAndType để đồng thời thiết lập URI và MIME type
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Lắng nghe kết quả khi chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Lấy URI của ảnh đã chọn
            imageUri = data.getData();

            // Hiển thị ảnh đã chọn lên ImageView
            ivImage.setImageURI(imageUri);
        }
    }

    private void UploadImage(Uri imageUri, String uid) {
        // Tham chiếu tới thư mục chứa ảnh của người dùng
        StorageReference storage = storageRef.child("reports/" + uid + "/" + System.currentTimeMillis() + ".jpg");

        // Thực hiện upload file
        storage.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storage.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Sau khi upload thành công, lưu URL của ảnh vào Firebase Database
                    reportRef.child(uid).child("imageUrl").setValue(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(HoTro.this, "Lỗi khi tải lên hình ảnh", Toast.LENGTH_SHORT).show());
    }
}