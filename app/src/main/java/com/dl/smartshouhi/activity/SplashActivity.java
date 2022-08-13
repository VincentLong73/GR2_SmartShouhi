package com.dl.smartshouhi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import static com.dl.smartshouhi.constaint.Constant.EMAIL_KEY;
import static com.dl.smartshouhi.constaint.Constant.ISADMIN_KEY;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;

public class SplashActivity extends AppCompatActivity {

    // variable for shared preferences.
    private SharedPreferences sharedpreferences;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);

        Handler handler = new Handler();
        handler.postDelayed(() -> nextActivity(), 2000);
    }

    private void nextActivity() {
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // getting data from shared prefs and
        // storing it in our string variable.
        email = sharedpreferences.getString(EMAIL_KEY, null);

        Intent intent;
        if(email == null){
            intent = new Intent(this, SignInActivity.class);
        }else{
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
