package com.dl.smartshouhi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.fragment.AboutUsFragment;
import com.dl.smartshouhi.fragment.HomeAdminFragment;
import com.dl.smartshouhi.fragment.HomeFragment;
import com.dl.smartshouhi.fragment.InvoiceInformationFragment;
import com.dl.smartshouhi.fragment.PersonFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Objects;

import static com.dl.smartshouhi.constaint.Constant.ISADMIN_KEY;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;

public class HomeActivity extends AppCompatActivity {

    final private PersonFragment personFragment = new PersonFragment();
    final private InvoiceInformationFragment invoiceInformationFragment = new InvoiceInformationFragment();
    final private HomeFragment homeFragment = new HomeFragment(invoiceInformationFragment);
    final private HomeAdminFragment homeAdminFragment = new HomeAdminFragment();

    public static final int MY_REQUEST_CODE = 311;
    private static final String FRAGMENT_INVOICE_INFORMATION = "InvoiceInformationFragment";
    private static final String FRAGMENT_PERSON = "PersonFragment";
    private static final String FRAGMENT_HOME = "HomeFragment";
    private static final String FRAGMENT_ABOUT_US = "AboutUsFragment";
    private static final String FRAGMENT_HOME_ADMIN = "HomeAdminFragment";

    private String currentFragment;


    private BottomNavigationView bottomNavigationView;
    private Boolean isAdmin = false;



    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    if(intent == null){
                        return;
                    }
                    Uri uri = intent.getData();

                    if(getCurrentFragment().equals(FRAGMENT_HOME) && checkVisibleFragment(FRAGMENT_INVOICE_INFORMATION)){
                        invoiceInformationFragment.setUri(uri);
                    }else if(getCurrentFragment().equals(FRAGMENT_HOME) && checkVisibleFragment(FRAGMENT_HOME) ){
                        homeFragment.setUri(uri);
                    }else if(getCurrentFragment().equals(FRAGMENT_PERSON) && checkVisibleFragment(FRAGMENT_PERSON) ){
                        personFragment.setUri(uri);
                    }

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                        if(getCurrentFragment().equals(FRAGMENT_HOME) && checkVisibleFragment(FRAGMENT_INVOICE_INFORMATION) ){
                            invoiceInformationFragment.setBitmapImageView(bitmap);
                        }else if(getCurrentFragment().equals(FRAGMENT_HOME) && checkVisibleFragment(FRAGMENT_HOME) ){
                            homeFragment.setBitmapImageView(bitmap);
                        }else if(getCurrentFragment().equals(FRAGMENT_PERSON) && checkVisibleFragment(FRAGMENT_PERSON) ){
                            personFragment.setBitmapImageView(bitmap);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initUI();
        setTitleToolbar();
        selectItemInBottomNavigation();
    }


    private void initUI() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottom_nav);

        SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        isAdmin = sharedpreferences.getBoolean(ISADMIN_KEY, false);
        if(isAdmin) {
            replaceFragment(new HomeAdminFragment(), FRAGMENT_HOME_ADMIN);
        }else{
            replaceFragment(homeFragment ,FRAGMENT_HOME);
        }
    }

    private void setTitleToolbar(){
        String title = "";
        switch (currentFragment){
            case FRAGMENT_HOME_ADMIN:
                title = "Trang Admin";
                break;
            case FRAGMENT_HOME:
                title = "Smart Shouhi";
                break;
            case FRAGMENT_PERSON:
                title = "Thông tin cá nhân";
                break;
            case FRAGMENT_ABOUT_US:
                title = "About us";
                break;
        }

        if(getSupportActionBar() != null){

            getSupportActionBar().setTitle(title);
        }
    }

    private void selectItemInBottomNavigation(){
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.action_home:
                    if(isAdmin) {
                        replaceFragment(homeAdminFragment, FRAGMENT_HOME_ADMIN);
                    }else{
                        replaceFragment(homeFragment ,FRAGMENT_HOME);
                    }
                    break;
                case R.id.action_person:
                    replaceFragment(personFragment, FRAGMENT_PERSON);
                    break;
                case R.id.action_info:
                    replaceFragment(new AboutUsFragment(), FRAGMENT_ABOUT_US);
                    break;
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment, String nameFragment){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_home, fragment, nameFragment);
        setCurrentFragment(nameFragment);
        setTitleToolbar();
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    public boolean checkVisibleFragment(String nameFragment){
        return Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(nameFragment)).isVisible();
    }

    public String getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(String currentFragment) {
        this.currentFragment = currentFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notice, menu);
        return true;
    }
}
