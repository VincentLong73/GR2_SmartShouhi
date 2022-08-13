package com.dl.smartshouhi.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.api.LoginDbApi;
import com.dl.smartshouhi.model.UserModel;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dl.smartshouhi.constaint.Constant.DOB_KEY;
import static com.dl.smartshouhi.constaint.Constant.EMAIL_KEY;
import static com.dl.smartshouhi.constaint.Constant.FULLNAME_KEY;
import static com.dl.smartshouhi.constaint.Constant.ID_KEY;
import static com.dl.smartshouhi.constaint.Constant.ISADMIN_KEY;
import static com.dl.smartshouhi.constaint.Constant.PHONE_KEY;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constant.USERNAME_KEY;

public class SignInActivity extends AppCompatActivity {

    private LinearLayout layoutSignUp;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnSignIn;
    private LinearLayout layoutForgotPassword;

    private ProgressDialog mProgressDialog;

    private SharedPreferences sharedpreferences;
    private RequestQueue requestQueue;

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

        requestQueue = Volley.newRequestQueue(this);
    }

    private void initListener() {
        layoutSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener(v -> onClickSignIn());

        layoutForgotPassword.setOnClickListener(v -> onClickForgotPassword());
    }

    private void onClickSignIn() {
        mProgressDialog.show();

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if(email.length() == 0 || password.length() == 0){
            mProgressDialog.dismiss();
            Toast.makeText(SignInActivity.this, "Email hoặc Password chưa được nhập",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        LoginDbApi.databaseApi.login(email, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    result = result.replace("\\", "");
                    result = result.replace("\"{", "{");
                    result = result.replace("}\"", "}");

                    Gson gson = new Gson();
                    String[] listResult = result.split("#");
                    if(listResult[0].equals("200")){
                        UserModel userModel = gson.fromJson(listResult[1], UserModel.class);
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        // below two lines will put values for
                        // email and password in shared preferences.
                        editor.putString(EMAIL_KEY, userModel.getEmail());
                        editor.putString(USERNAME_KEY, userModel.getUserName());
                        editor.putString(FULLNAME_KEY, userModel.getFullName());
                        editor.putInt(ID_KEY, userModel.getId());
                        editor.putString(PHONE_KEY, userModel.getPhone());
                        editor.putBoolean(ISADMIN_KEY, userModel.isAdmin());
                        editor.putString(DOB_KEY, userModel.getDob());


                        // to save our data with key and value.
                        editor.apply();

                        mProgressDialog.dismiss();

                        Intent i;
                        i = new Intent(SignInActivity.this, HomeActivity.class);
                        startActivity(i);

                    }else {
                        mProgressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, listResult[1], Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private void onClickForgotPassword() {
        mProgressDialog.show();
    }
}