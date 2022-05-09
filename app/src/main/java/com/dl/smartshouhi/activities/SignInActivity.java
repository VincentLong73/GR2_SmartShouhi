package com.dl.smartshouhi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.dl.smartshouhi.constaint.Constaint.EMAIL_KEY;
import static com.dl.smartshouhi.constaint.Constaint.FULLNAME_KEY;
import static com.dl.smartshouhi.constaint.Constaint.ID_KEY;
import static com.dl.smartshouhi.constaint.Constaint.ISADMIN_KEY;
import static com.dl.smartshouhi.constaint.Constaint.PASSWORD_KEY;
import static com.dl.smartshouhi.constaint.Constaint.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constaint.URL_LOGIN;
import static com.dl.smartshouhi.constaint.Constaint.USERNAME_KEY;

public class SignInActivity extends AppCompatActivity {

    private LinearLayout layoutSignUp;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnSignIn;
    private LinearLayout layoutForgotPassword;

    private ProgressDialog mProgressDialog;

    private SharedPreferences sharedpreferences;
    private RequestQueue requestQueue;
    private String emailShared, passwordShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initUI();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        initListener();
    }

    private void initUI() {
        layoutSignUp = findViewById(R.id.layout_sign_up);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSignIn = findViewById(R.id.btn_sign_up);
        layoutForgotPassword = findViewById(R.id.layout_forgot_password);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // in shared prefs inside het string method
        // we are passing key value as EMAIL_KEY and
        // default value is
        // set to null if not present.
        emailShared = sharedpreferences.getString(EMAIL_KEY, null);
        passwordShared = sharedpreferences.getString(PASSWORD_KEY, null);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initListener() {
        layoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignIn();
            }
        });

        layoutForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickForgotPassword();
            }
        });
    }

    private void onClickSignIn() {
        mProgressDialog.show();

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if(email == null || password == null || email.length() == 0 || password.length() == 0){
            Toast.makeText(SignInActivity.this, "Email and Password is not enterd",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //
//                String s = response.trim();
//
//                if(s.equalsIgnoreCase("false")){
                Gson gson = new Gson();
                UserModel userModel = gson.fromJson(response, UserModel.class);
                //if(s.equalsIgnoreCase("false")){
                if(userModel != null){
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    // below two lines will put values for
                    // email and password in shared preferences.
                    editor.putString(EMAIL_KEY, userModel.getEmail());
                    editor.putString(USERNAME_KEY, userModel.getUserName());
                    editor.putString(FULLNAME_KEY, userModel.getFullName());
                    editor.putInt(ID_KEY, userModel.getId());
                    editor.putBoolean(ISADMIN_KEY, userModel.isAdmin());

                    //editor.putString(PASSWORD_KEY, passwordEdt.getText().toString());

                    // to save our data with key and value.
                    editor.apply();

                    // starting new activity.
                    Intent i = new Intent(SignInActivity.this, HomeActivity.class);
                    startActivity(i);
                    //finish();
                    finishAffinity();
                }else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }

            }
        }, error -> {

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };

        requestQueue.add(stringRequest);



//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        mProgressDialog.dismiss();
//
//                        if (task.isSuccessful()) {
//                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
//                            startActivity(intent);
//                            finishAffinity();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(SignInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }

    private void onClickForgotPassword() {
        mProgressDialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();

//        String emailAddress = "longngodaugo1.1202@gmail.com";
        String emailAddress = auth.getCurrentUser().getEmail();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Email sent", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(SignInActivity.this, "Email didn't send", Toast.LENGTH_LONG).show();
                        }
                        mProgressDialog.dismiss();
                    }
                });
    }
}