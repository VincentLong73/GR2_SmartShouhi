package com.dl.smartshouhi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dl.smartshouhi.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnSignUp;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initUI();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        initListener();
    }

    private void initUI() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
    }

    private void initListener() {
        btnSignUp.setOnClickListener(v -> onClickSignUp());
    }

    private void onClickSignUp() {
        mProgressDialog.show();

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    mProgressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SignUpActivity.this, InvoiceInformationActivity.class);
                        startActivity(intent);
                        finishAffinity();// dong tat ca Activity sau thang MainActivity
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


}