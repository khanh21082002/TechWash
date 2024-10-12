package com.example.techwash.UserProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techwash.R;
import com.example.techwash.UserProfile.SignIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityUpdatePassword extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    ProgressDialog pd;
    Button btn_Back, btn_Update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        btn_Update = findViewById(R.id.btn_Update);
        btn_Back = findViewById(R.id.btn_Back);

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Changing Password...");
                showPasswordChangeDialog();
            }
        });
    }

    private void showPasswordChangeDialog() {
        final TextInputEditText oldpass = findViewById(R.id.oldpasslog);
        final TextInputEditText newpass = findViewById(R.id.newpasslog);
        final TextInputEditText verifypass = findViewById(R.id.verifynewpasslog);

        String oldp = oldpass.getText().toString().trim();
        String newp = newpass.getText().toString().trim();
        String verifyp = verifypass.getText().toString().trim();

        // Validate fields
        if (TextUtils.isEmpty(oldp)) {
            oldpass.setError("Current password can't be empty");
            oldpass.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newp)) {
            newpass.setError("New password can't be empty");
            newpass.requestFocus();
            return;
        }

        if (newp.length() < 6) {
            newpass.setError("Password must be at least 6 characters");
            newpass.requestFocus();
            return;
        }

        if (verifyp.length() < 6) {
            verifypass.setError("Password must be at least 6 characters");
            verifypass.requestFocus();
            return;
        }

        if (!verifyp.equals(newp)) {
            verifypass.setError("Passwords do not match");
            verifypass.requestFocus();
            return;
        }

        if (newp.equals(oldp)) {
            newpass.setError("New password must be different from current password");
            newpass.requestFocus();
            return;
        }

        // If all validations pass, update the password
        updatePassword(oldp, newp);
    }

    private void updatePassword(String oldp, final String newp) {
        pd.show();

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldp);
            user.reauthenticate(authCredential)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Successfully authenticated, update password
                            user.updatePassword(newp)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(ActivityUpdatePassword.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                                            firebaseAuth.signOut();  // Sign out after password change
                                            startActivity(new Intent(ActivityUpdatePassword.this, SignIn.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(ActivityUpdatePassword.this, "Failed to change password: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(ActivityUpdatePassword.this, "Current password is incorrect", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
