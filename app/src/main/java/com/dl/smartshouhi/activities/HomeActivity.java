package com.dl.smartshouhi.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.fragment.HomeFragment;
import com.dl.smartshouhi.fragment.InvoiceInformationFragment;
import com.dl.smartshouhi.fragment.MyProfileFragment;
import com.dl.smartshouhi.fragment.TestInfoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {


    final private MyProfileFragment myProfileFragment = new MyProfileFragment();
    final private InvoiceInformationFragment invoiceInformationFragment = new InvoiceInformationFragment();

    public static final int MY_REQUEST_CODE = 311;
    private static final String FRAGMENT_INVOICE_INFORMATION = "InvoiceInformationFragment";
    private static final String FRAGMENT_PERSON = "PersonFragment";
    private static final String FRAGMENT_HOME = "HomeFragment";
    private static final String FRAGMENT_ABOUT_US = "AboutUsFragment";

    private String currentFragment;


    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private TextView tvTitleToolbar;


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
                    }else if(getCurrentFragment().equals(FRAGMENT_PERSON) && checkVisibleFragment(FRAGMENT_PERSON) ){
                        myProfileFragment.setUri(uri);
                    }

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                        if(getCurrentFragment().equals(FRAGMENT_HOME) && checkVisibleFragment(FRAGMENT_INVOICE_INFORMATION) ){
                            invoiceInformationFragment.setBitmapImageView(bitmap);
                        }else if(getCurrentFragment().equals(FRAGMENT_PERSON) && checkVisibleFragment(FRAGMENT_PERSON) ){
                            myProfileFragment.setBitmapImageView(bitmap);
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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitleToolbar();

        selectItemInBottomNavigation();
    }


    private void initUI() {
        tvTitleToolbar = findViewById(R.id.toolbar_title);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        replaceFragment(new HomeFragment(invoiceInformationFragment) ,FRAGMENT_HOME);
    }

    private void setTitleToolbar(){
        String title = "";
        switch (currentFragment){
            case FRAGMENT_HOME:
                title = "Smart Shouhi";
                break;
            case FRAGMENT_PERSON:
                title = "Thong tin ca nhan";
                break;
            case FRAGMENT_ABOUT_US:
                title = "About us";
                break;
        }

        if(getSupportActionBar() != null){
//            getSupportActionBar().setTitle(title);
            tvTitleToolbar.setText(title);
        }
    }

    private void selectItemInBottomNavigation(){
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.action_home:
                    replaceFragment(new HomeFragment(invoiceInformationFragment), FRAGMENT_HOME);
                    break;
                case R.id.action_person:
                    replaceFragment(myProfileFragment, FRAGMENT_PERSON);
                    break;
                case R.id.action_info:
                    replaceFragment(new TestInfoFragment(), FRAGMENT_ABOUT_US);
                    break;
            }
            return true;
        });

//        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
//            @Override
//            public void onNavigationItemReselected( MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.action_home:
//                        viewPager.setCurrentItem(0);
//                        break;
//                    case R.id.action_person:
//                        viewPager.setCurrentItem(1);
//                        break;
//                    case R.id.action_info:
//                        viewPager.setCurrentItem(2);
//                        break;
//                }
//            }
//        });
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
}
