package com.dl.smartshouhi.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.fragment.HomeFragment1;
import com.dl.smartshouhi.fragment.InvoiceInformationFragment;
import com.dl.smartshouhi.fragment.MyProfileFragment;
import com.dl.smartshouhi.fragment.TestInfoFragment;
import com.dl.smartshouhi.fragment.TestPersonFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class HomeActivity1 extends AppCompatActivity {

    final private MyProfileFragment myProfileFragment = new MyProfileFragment();
    final private InvoiceInformationFragment invoiceInformationFragment = new InvoiceInformationFragment();

    public static final int MY_REQUEST_CODE = 311;
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_FAVORITE = 1;
    private static final int FRAGMENT_HISTORY = 2;
    private static final int FRAGMENT_PROFILE = 3;
    private static final int FRAGMENT_CHANGE_PASSWORD = 4;
    private static final int FRAGMENT_INVOICE_INFORMATION = 5;
    private static final String FRAGMENT_INVOICE_INFORMATION1 = "InvoiceInformationFragment";
    private static final String FRAGMENT_HOME1 = "HomeFragment";
    private static final String FRAGMENT_PERSON = "PersonFragment";
    private static final String FRAGMENT_INFO = "InfoFragment";

    private static int currentFragment = FRAGMENT_HOME;
    private String currentFragment1;

    private static final String TAG_IMAGE = "TAG_IMAGE";

    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;


    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    if(intent == null){
                        return;
                    }
                    Uri uri = intent.getData();
//                    if(currentFragment == FRAGMENT_PROFILE){
//                        myProfileFragment.setUri(uri);
//                    }else if(currentFragment == FRAGMENT_INVOICE_INFORMATION){
//                        invoiceInformationFragment.setUri(uri);
//                    }
                    if(getCurrentFragment1().equals(FRAGMENT_INVOICE_INFORMATION1) && checkVisibleFragment(FRAGMENT_INVOICE_INFORMATION1) ){
                        invoiceInformationFragment.setUri(uri);
                    }

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                        if(currentFragment == FRAGMENT_PROFILE){
//                            myProfileFragment.setBitmapImageView(bitmap);
//                        }else if(currentFragment == FRAGMENT_INVOICE_INFORMATION){
//                            invoiceInformationFragment.setBitmapImageView(bitmap);
//                        }
                        if(getCurrentFragment1().equals(FRAGMENT_INVOICE_INFORMATION1) && checkVisibleFragment(FRAGMENT_INVOICE_INFORMATION1) ){
                            invoiceInformationFragment.setBitmapImageView(bitmap);
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
        setContentView(R.layout.activity_home1);

        initUI();

        selectItemInBottomNavigation();
//        setUpViewPaper();
    }


    private void initUI() {

        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.view_paper);
        replaceFragment(new HomeFragment1() ,"HomeFragment");
    }

    private void selectItemInBottomNavigation(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
//                        viewPager.setCurrentItem(0);
                        replaceFragment(new HomeFragment1(), "HomeFragment");
                        break;
                    case R.id.action_person:
//                        viewPager.setCurrentItem(1);
                        replaceFragment(new MyProfileFragment(), "PersonFragment");
                        break;
                    case R.id.action_info:
//                        viewPager.setCurrentItem(2);
                        replaceFragment(new TestInfoFragment(), "InfoFragment");
                        break;
                }
                return true;
            }
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

//    private void setUpViewPaper() {
//        ViewPaperAdapter viewPaperAdapter = new ViewPaperAdapter(getSupportFragmentManager(), getLifecycle());
//        viewPager.setAdapter(viewPaperAdapter);
//
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position){
//                    case 0:
//                        bottomNavigationView.getMenu().findItem(R.id.action_home);
//                        break;
//                    case 1:
//                        bottomNavigationView.getMenu().findItem(R.id.action_person);
//                        break;
//                    case 2:
//                        bottomNavigationView.getMenu().findItem(R.id.action_info);
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                super.onPageScrollStateChanged(state);
//            }
//        });
//    }

    private void replaceFragment(Fragment fragment, String nameFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_home, fragment, nameFragment);
        setCurrentFragment1(nameFragment);
        transaction.commit();
    }



    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    public boolean checkVisibleFragment(String nameFragment){
        return getSupportFragmentManager().findFragmentByTag(nameFragment).isVisible();
    }

    public String getCurrentFragment1() {
        return currentFragment1;
    }

    public void setCurrentFragment1(String currentFragment1) {
        this.currentFragment1 = currentFragment1;
    }
}
