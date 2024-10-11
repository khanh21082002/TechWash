package com.example.techwash.UserProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techwash.Model.User;
import com.example.techwash.R;
import com.example.techwash.UI.MainScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    TextView dangky;
    TextInputEditText password;
    EditText email;
    Button dangnhap;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    private void bindingView() {
        email = findViewById(R.id.edt_Email);
        password = findViewById(R.id.edt_Password);
        dangnhap = findViewById(R.id.btn_Dangnhap);
        dangky = findViewById(R.id.txt_Dangkytaikhoan);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    private void bindingAction() {
        dangnhap.setOnClickListener(this::onClickDangNhap);
        dangky.setOnClickListener(this::onClickDangKy);
    }

    private void onClickDangKy(View view) {
        startActivity(new Intent(this, SignUp.class));
    }

    private void onClickDangNhap(View view) {
        checkUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        bindingView();
        bindingAction();
    }

    private void checkUser() {
        String strEmail = email.getText().toString().trim();
        String strPassword = password.getText().toString().trim();

        if (strEmail.isEmpty()) {
            email.setError("Vui lòng nhập email!");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            email.setError("Vui lòng nhập email hợp lệ");
            email.requestFocus();
            return;
        }

        if (strPassword.isEmpty()) {
            password.setError("Vui lòng nhập mật khẩu!");
            password.requestFocus();
            return;
        }

        if (strPassword.length() < 6) {
            password.setError("Mật khẩu phải hơn 6 ký tự!");
            password.requestFocus();
            return;
        }

        // Hiển thị thông báo chờ
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user(strEmail);
                } else {
                    Toast.makeText(SignIn.this, "Đăng nhập thất bại! Hãy kiểm tra lại thông tin đăng nhập!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void user(String strEmail) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("User");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean exist = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) {
                        Log.d("SignIn", "Kiểm tra người dùng: " + user.getEmail());
                        if (user.getEmail().equals(strEmail)) {
                            Log.d("SignIn", "Người dùng tìm thấy: " + user.getEmail());
                            if (user.getStatus() != null && user.getStatus()) {
                                exist = true;
                                break;
                            } else {
                                Toast.makeText(SignIn.this, "Tài khoản của bạn đã bị khóa!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
                progressDialog.dismiss();
                if (exist) {
                    Log.d("SignIn", "Người dùng hợp lệ, chuyển đến MainScreen.");
                    startActivity(new Intent(SignIn.this, MainScreen.class));
                    finish();
                } else {
                    Toast.makeText(SignIn.this, "Đăng nhập thất bại! Hãy kiểm tra lại thông tin đăng nhập!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });
    }

}
