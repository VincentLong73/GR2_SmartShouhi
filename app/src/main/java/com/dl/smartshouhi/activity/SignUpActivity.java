package com.dl.smartshouhi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.api.LoginDbApi;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dl.smartshouhi.constaint.Constant.URL_REGIS;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail, edtUsername, edtDob, edtPassword;
    private Button btnSignUp;
    private ImageButton imgButtonCalendar;

    private ProgressDialog mProgressDialog;
    private RequestQueue requestQueue;

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
        edtUsername = findViewById(R.id.edt_username_signup);
        edtDob = findViewById(R.id.edt_dob_signup);
        edtPassword = findViewById(R.id.edt_password);
        imgButtonCalendar = findViewById(R.id.btn_calendar_signup);
        btnSignUp = findViewById(R.id.btn_sign_up);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initListener() {
        btnSignUp.setOnClickListener(v -> onClickSignUp());
        imgButtonCalendar.setOnClickListener(v -> displayCalendar());
    }

    private void onClickSignUp() {
        mProgressDialog.show();

        String email = edtEmail.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String dateModified = DateFormat.format("yyyy-MM-dd", new Date()).toString();

        LoginDbApi.databaseApi.register(email, password, username, dob, dateModified).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    String[] listResult = result.split("#");
                    if(listResult[0].equals("200")){
                        mProgressDialog.dismiss();
                    }

                    Toast.makeText(SignUpActivity.this, listResult[1], Toast.LENGTH_SHORT).show();

                    Intent i;
                    i = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private void displayCalendar() {
        final View dialogView = View.inflate(SignUpActivity.this, R.layout.layout_date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {

            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);

            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                    datePicker.getMonth(),
                    datePicker.getDayOfMonth());

            String currentDate = DateFormat.format("yyyy-MM-dd", calendar).toString();
            edtDob.setText(currentDate);

            alertDialog.dismiss();
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }


}