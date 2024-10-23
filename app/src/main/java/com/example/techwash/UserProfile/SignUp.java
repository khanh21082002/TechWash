package com.example.techwash.UserProfile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techwash.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    TextInputEditText edt_Password, edt_Nhaplaipassword;
    Button DangKy;
    EditText edt_Hoten, edt_Sdt, edt_Email;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    FirebaseFirestore firestore;

    private void bingdingView() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        edt_Hoten = findViewById(R.id.edt_Hoten);
        edt_Email = findViewById(R.id.edt_Email);
        edt_Sdt = findViewById(R.id.edt_Sdt);
        edt_Password = findViewById(R.id.edt_Password);
        edt_Nhaplaipassword = findViewById(R.id.edt_Nhaplaipassword);
        DangKy = findViewById(R.id.btn_Dangky);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tạo tài khoản...");
    }

    private void bingdingAction() {
        DangKy.setOnClickListener(this::onClickDangKy);
    }

    private void onClickDangKy(View view) {
        DangKy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        bingdingView();
        bingdingAction();
    }

    // Hàm hiển thị ProgressDialog
    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    // Hàm ẩn ProgressDialog
    private void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @SuppressLint("NotConstructor")
    private void DangKy() {
        String username = edt_Hoten.getText().toString().trim();
        String email = edt_Email.getText().toString().trim();
        String password = edt_Password.getText().toString().trim();
        String confirmpassword = edt_Nhaplaipassword.getText().toString().trim();
        String phone = edt_Sdt.getText().toString().trim();

        // Kiểm tra tính hợp lệ của các trường nhập
            if (username.isEmpty()) {
                edt_Hoten.setError("Vui lòng nhập họ tên!");
                edt_Hoten.requestFocus();
                return;
            }

            if (phone.isEmpty()) {
                edt_Sdt.setError("Vui lòng nhập số điện thoại");
                edt_Sdt.requestFocus();
                return;
            }

            if (phone.length() > 12 || phone.length() < 9) {
                edt_Sdt.setError("Số điện thoại phải từ 9 đến 12 ký tự!");
                edt_Sdt.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                edt_Email.setError("Vui lòng nhập email!");
                edt_Email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edt_Email.setError("Email chưa hợp lệ!");
                edt_Email.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                edt_Password.setError("Vui lòng nhập mật khẩu mới!");
                edt_Password.requestFocus();
                return;
            }

            if (password.length() < 6) {
                edt_Password.setError("Mật khẩu phải hơn 6 ký tự!");
                edt_Password.requestFocus();
                return;
            }

            if (confirmpassword.isEmpty()) {
                edt_Nhaplaipassword.setError("Vui lòng nhập lại mật khẩu mới!");
                edt_Nhaplaipassword.requestFocus();
                return;
            }

            if (!confirmpassword.equals(password)) {
                edt_Nhaplaipassword.setError("Không trùng với mật khẩu mới!");
                edt_Nhaplaipassword.requestFocus();
                return;
            }

        // Hiển thị ProgressDialog
        showProgressDialog();

        // Đăng ký tài khoản với Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Lấy UID của người dùng hiện tại
                            String userId = mAuth.getCurrentUser().getUid();

                            // Tạo dữ liệu người dùng
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("username", username);
                            user.put("phone", phone);
                            user.put("status", true);
                            user.put("image", "1");

                            // Lưu thông tin người dùng vào Firestore
                            firestore.collection("User").document(userId).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Lưu dữ liệu thành công");
                                        Toast.makeText(SignUp.this, "Lưu vào Firestore thành công", Toast.LENGTH_SHORT).show();
                                        dismissProgressDialog();  // Tắt ProgressDialog khi lưu thành công
                                        startActivity(new Intent(SignUp.this, SignIn.class));
                                        finishAffinity();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Lỗi khi lưu dữ liệu: " + e.getMessage());
                                        Toast.makeText(SignUp.this, "Lỗi khi lưu vào Firestore", Toast.LENGTH_SHORT).show();
                                        dismissProgressDialog();  // Tắt ProgressDialog khi gặp lỗi
                                    });
                        } else {
                            // Hiển thị thông báo lỗi từ Firebase
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Tạo tài khoản thất bại.";
                            Toast.makeText(SignUp.this, errorMessage, Toast.LENGTH_SHORT).show();
                            dismissProgressDialog();  // Tắt ProgressDialog khi gặp lỗi
                        }
                    }
                });
    }
}
