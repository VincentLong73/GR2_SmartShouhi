package com.dl.smartshouhi.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.fragment.ChangePasswordFragment;
import com.dl.smartshouhi.fragment.FavoriteFragment;
import com.dl.smartshouhi.fragment.HistoryFragment;
import com.dl.smartshouhi.fragment.HomeFragment;
import com.dl.smartshouhi.fragment.InvoiceInformationFragment;
import com.dl.smartshouhi.fragment.MyProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ImageView imgAvatar;
    private TextView tvName, tvEmail;
    private NavigationView mNavigationView;
    final private MyProfileFragment myProfileFragment = new MyProfileFragment();
    final private InvoiceInformationFragment invoiceInformationFragment = new InvoiceInformationFragment();

    public static final int MY_REQUEST_CODE = 311;
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_FAVORITE = 1;
    private static final int FRAGMENT_HISTORY = 2;
    private static final int FRAGMENT_PROFILE = 3;
    private static final int FRAGMENT_CHANGE_PASSWORD = 4;
    private static final int FRAGMENT_INVOICE_INFORMATION = 5;

    private static int currentFragment = FRAGMENT_HOME;

    private static final String TAG_IMAGE = "TAG_IMAGE";

    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    if(intent == null){
                        return;
                    }
                    Uri uri = intent.getData();
                    if(currentFragment == FRAGMENT_PROFILE){
                        myProfileFragment.setUri(uri);
                    }else if(currentFragment == FRAGMENT_INVOICE_INFORMATION){
                        invoiceInformationFragment.setUri(uri);
                    }

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        if(currentFragment == FRAGMENT_PROFILE){
                            myProfileFragment.setBitmapImageView(bitmap);
                        }else if(currentFragment == FRAGMENT_INVOICE_INFORMATION){
                            invoiceInformationFragment.setBitmapImageView(bitmap);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                                                                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        mNavigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new HomeFragment());
        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        showUserInformation();
    }

    private void initUI(){
        mNavigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        imgAvatar = mNavigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        tvName = mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tvEmail = mNavigationView.getHeaderView(0).findViewById(R.id.tv_email);
    }

    public void showUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }

        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUri = user.getPhotoUrl();

        if(name == null){
            tvName.setVisibility(View.GONE);
        }else{
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(name);
        }
        tvEmail.setText(email);
        Log.e(TAG_IMAGE,user.getPhotoUrl()+"");
        Glide.with(this).load(photoUri).error(R.drawable.ic_avatar_default).into(imgAvatar);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            if(currentFragment != FRAGMENT_HOME){
                replaceFragment(new HomeFragment());
                currentFragment = FRAGMENT_HOME;
            }
        }else if(id == R.id.nav_history){
            if(currentFragment != FRAGMENT_HISTORY){
                replaceFragment(new HistoryFragment());
                currentFragment = FRAGMENT_HISTORY;
            }
        }else if(id == R.id.nav_favorite){
            if(currentFragment != FRAGMENT_FAVORITE){
                replaceFragment(new FavoriteFragment());
                currentFragment = FRAGMENT_FAVORITE;
            }
        }else if(id == R.id.nav_profile){
            if(currentFragment != FRAGMENT_PROFILE){
                replaceFragment(myProfileFragment);
                currentFragment = FRAGMENT_PROFILE;
            }
        }else if(id == R.id.nav_change_password){
            if(currentFragment != FRAGMENT_CHANGE_PASSWORD){
                replaceFragment(new ChangePasswordFragment());
                currentFragment = FRAGMENT_CHANGE_PASSWORD;
            }
        }else if(id == R.id.nav_post_add){
            if(currentFragment != FRAGMENT_INVOICE_INFORMATION){
                replaceFragment(invoiceInformationFragment);
                currentFragment = FRAGMENT_INVOICE_INFORMATION;
            }
        }else if(id == R.id.nav_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}