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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


public class SignIn extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private TextView dangky;
    private TextInputEditText password;
    private EditText email;
    private Button dangnhap;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

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

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void checkUser() {
        String strEmail = email.getText().toString().trim();
        String strPassword = password.getText().toString().trim();

        // Kiểm tra đầu vào
        if (!validateInput(strEmail, strPassword)) return;

        // Hiển thị thông báo chờ
        showProgressDialog();

        signIn(strEmail, strPassword);
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            dismissProgressDialog();
                            updateUI(user);
                        } else {
                            // If sign in fails
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this,
                                    "Đăng nhập thất bại! Hãy kiểm tra lại thông tin đăng nhập!",
                                    Toast.LENGTH_LONG).show();
                            dismissProgressDialog();
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean validateInput(String strEmail, String strPassword) {
        if (strEmail.isEmpty()) {
            email.setError("Vui lòng nhập email!");
            email.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            email.setError("Vui lòng nhập email hợp lệ");
            email.requestFocus();
            return false;
        }

        if (strPassword.isEmpty()) {
            password.setError("Vui lòng nhập mật khẩu!");
            password.requestFocus();
            return false;
        }

        if (strPassword.length() < 6) {
            password.setError("Mật khẩu phải hơn 6 ký tự!");
            password.requestFocus();
            return false;
        }
        return true;
    }

    private void showProgressDialog() {
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showToast(String message) {
        Toast.makeText(SignIn.this, message, Toast.LENGTH_LONG).show();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Go to MainScreen if the user is logged in
            Intent intent = new Intent(SignIn.this, MainScreen.class);
            startActivity(intent);
            finish();
        } else {
            // Handle UI for the case when sign-in fails or the user is null
            Toast.makeText(this, "User is not signed in", Toast.LENGTH_SHORT).show();
        }
    }
}
