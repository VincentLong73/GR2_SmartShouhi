package com.dl.smartshouhi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.dl.smartshouhi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.dl.smartshouhi.constaint.Constaint.EMAIL_KEY;
import static com.dl.smartshouhi.constaint.Constaint.SHARED_PREFS;

public class SplashActivity extends AppCompatActivity {

    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(() -> nextActivity(), 2000);
    }

    private void nextActivity() {
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // getting data from shared prefs and
        // storing it in our string variable.
        email = sharedpreferences.getString(EMAIL_KEY, null);

        Intent intent;
        //if(user == null){
        if(email == null){
            intent = new Intent(this, SignInActivity.class);
        }else{
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
        //finishAffinity();
    }
}
