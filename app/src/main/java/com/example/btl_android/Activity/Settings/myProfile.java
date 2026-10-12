package com.example.btl_android.Activity.Settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class myProfile extends AppCompatActivity {

    // Request code cho việc chọn ảnh từ thư viện
    private static final int PICK_IMAGE_REQUEST = 1;

    // Khai báo các biến giao diện
    TextView viewUser;
    TextInputEditText editUser, editEmail, editPhone, editAddress;
    LinearLayout linearLayout;
    Button btnCancel, btnSave;
    ImageView btnBack, btnAvatar;

    // Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("taikhoan");

    // Firebase Storage để lưu ảnh
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    // Firebase Authentication
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Biến kiểm tra dữ liệu thay đổi
    boolean isDataChanged = false;
    boolean isAvatarChanged = false;
    String avatarUrl, currentUser, currentEmail, currentPhone, currentAddress;

    // URI của ảnh đã chọn từ thư viện
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.setting_myprofile);

        // Ánh xạ các thành phần giao diện
        Mapping();

        // Ban đầu ẩn LinearLayout
        linearLayout.setVisibility(View.GONE);

        // Lấy dữ liệu từ Firebase
        GetData();

        // Lắng nghe sự thay đổi dữ liệu
        WatchForChanges();

        // Sự kiện khi nhấn nút "Save"
        btnSave.setOnClickListener(v -> {
            clearFocus();
            if (isDataChanged) {
                showConfirm();
            }
        });

        // Sự kiện khi nhấn nút "Cancel"
        btnCancel.setOnClickListener(v -> {
            clearFocus();
            GetData();  // Lấy lại dữ liệu từ Firebase nếu người dùng hủy
        });

        // Sự kiện khi nhấn nút "Back"
        btnBack.setOnClickListener(v -> {
            clearFocus();
            if (isDataChanged) {
                showConfirmBack();
            } else {
                finish();
            }
        });

        // Sự kiện khi nhấn vào avatar để chọn ảnh
        btnAvatar.setOnClickListener(v -> {
            // Mở thư viện ảnh của điện thoại
            openGallery();
        });
    }

    // Phương thức ánh xạ các thành phần giao diện
    private void Mapping() {
        viewUser = findViewById(R.id.tv_user);
        editUser = findViewById(R.id.et_user);
        editEmail = findViewById(R.id.et_email);
        editPhone = findViewById(R.id.et_numPhone);
        editAddress = findViewById(R.id.et_address);
        linearLayout = findViewById(R.id.ll_btn);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        btnAvatar = findViewById(R.id.iv_avatar);
        btnBack = findViewById(R.id.ivBack);
    }

    // Lấy dữ liệu từ Firebase
    private void GetData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);

        if (uid == null) {
            Toast.makeText(this, "Không thể xác thực người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                avatarUrl = task.getResult().child("avatarUrl").getValue(String.class);
                currentUser = task.getResult().child("username").getValue(String.class);
                currentEmail = task.getResult().child("email").getValue(String.class);
                currentPhone = task.getResult().child("sdt").getValue(String.class);
                currentAddress = task.getResult().child("diachi").getValue(String.class);

                viewUser.setText(currentUser);
                editUser.setText(currentUser);
                editEmail.setText(currentEmail);
                editPhone.setText(currentPhone);
                editAddress.setText(currentAddress);

                // Hiển thị ảnh nếu có URL
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    setAvatarImage(Uri.parse(avatarUrl));
                }
            } else {
                Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức lưu dữ liệu sau khi nhấn "Save"
    private void SetData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);

        if (uid == null) {
            Toast.makeText(this, "Không thể xác thực người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation trước khi lưu
        if (editUser.getText().toString().isEmpty() || editEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        String newEmail = editEmail.getText().toString().trim();
        String newUsername = editUser.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();
        String newAddress = editAddress.getText().toString().trim();

        // Cập nhật email trong Firebase Authentication
        firebaseAuth.getCurrentUser().updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Cập nhật email thành công
                dbRef.child(uid).child("username").setValue(newUsername);
                dbRef.child(uid).child("email").setValue(newEmail);
                dbRef.child(uid).child("sdt").setValue(newPhone);
                dbRef.child(uid).child("diachi").setValue(newAddress);

                // Nếu người dùng đã chọn ảnh mới thì upload ảnh lên Firebase
                if (isAvatarChanged && imageUri != null) {
                    uploadImageToFirebase(imageUri, uid);
                }

                Toast.makeText(myProfile.this, "Thông tin đã được cập nhật!", Toast.LENGTH_SHORT).show();
                isDataChanged = false;
                linearLayout.setVisibility(View.GONE); // Ẩn LinearLayout sau khi lưu
                GetData();
            } else {
                // Thất bại khi cập nhật email
                Toast.makeText(myProfile.this, "Lỗi khi cập nhật email!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nếu người dùng đã chọn ảnh mới thì upload ảnh lên Firebase
        if (isAvatarChanged && imageUri != null) {
            uploadImageToFirebase(imageUri, uid);
        }
    }

    // Phương thức kiểm tra sự thay đổi của dữ liệu
    private void WatchForChanges() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasChanges = !editUser.getText().toString().equals(currentUser) ||
                        !editEmail.getText().toString().equals(currentEmail) ||
                        !editPhone.getText().toString().equals(currentPhone) ||
                        !editAddress.getText().toString().equals(currentAddress);

                if (hasChanges) {
                    isDataChanged = true;
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    isDataChanged = false;
                    linearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        editUser.addTextChangedListener(textWatcher);
        editEmail.addTextChangedListener(textWatcher);
        editPhone.addTextChangedListener(textWatcher);
        editAddress.addTextChangedListener(textWatcher);
    }

    // Hiển thị dialog xác nhận lưu dữ liệu
    private void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Thiết lập tiêu đề và nội dung
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có chắc chắn muốn lưu?");

        // Nút "Yes"
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            SetData();
            Toast.makeText(myProfile.this, "Đã lưu", Toast.LENGTH_SHORT).show();
            isDataChanged = false;
            linearLayout.setVisibility(View.GONE); // Ẩn LinearLayout sau khi lưu
            GetData();
        });

        // Nút "No"
        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Hiển thị dialog xác nhận lưu trước khi quay lại trang trước đó
    public void showConfirmBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Thiết lập tiêu đề và nội dung
        builder.setTitle("Xác nhận");
        builder.setMessage("Có dư liệu chưa lưu. Bạn có muốn lưu?");

        // Nút "Yes"
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            SetData();
            Toast.makeText(myProfile.this, "Đã lưu", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Nút "No"
        builder.setNegativeButton("Không", (dialog, which) -> {
            finish();
        });

        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Ẩn bàn phím và bỏ focus khỏi các trường nhập liệu
    private void clearFocus() {
        editUser.clearFocus();
        editEmail.clearFocus();
        editPhone.clearFocus();
        editAddress.clearFocus();

        // Ẩn bàn phím ảo nếu có
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
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
            setAvatarImage(imageUri);

            // Đánh dấu rằng ảnh avatar đã thay đổi
            isAvatarChanged = true;

            // Hiển thị nút lưu và hủy
            linearLayout.setVisibility(View.VISIBLE);
            isDataChanged = true;
        }
    }

    // Đặt ảnh đã chọn vào btnAvatar và làm nó hình tròn
    private void setAvatarImage(Uri uri) {
        // Sử dụng Glide để load ảnh và làm hình tròn
        Glide.with(this)
                .load(uri)
                .circleCrop() // Chuyển đổi ảnh thành hình tròn
                .into(btnAvatar); // Đặt ảnh vào btnAvatar
    }

    // Phương thức upload ảnh lên Firebase Storage
    private void uploadImageToFirebase(Uri imageUri, String uid) {
        // Tham chiếu tới thư mục chứa ảnh avatar của người dùng
        StorageReference avatarRef = storageRef.child("avatars/" + uid + "/avatar.jpg");

        // Thực hiện upload file
        avatarRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Sau khi upload thành công, lưu URL của ảnh vào Firebase Database
                    dbRef.child(uid).child("avatarUrl").setValue(uri.toString());
                    Toast.makeText(myProfile.this, "Đã tải lên hình ảnh", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> Toast.makeText(myProfile.this, "Lỗi khi tải lên hình ảnh", Toast.LENGTH_SHORT).show());

        dbRef.child(uid).child("avatarUrl").setValue(imageUri.toString());
    }
}
