package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activities.HomeActivity;
import com.dl.smartshouhi.activities.SignInActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class PersonFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MY_REQUEST_CODE = 311;

    private DrawerLayout drawerLayout;
    private View view;
    private ProgressDialog progressDialog;

    private ImageView imgAvatar;
    private EditText edtUsername;
    private TextView tvEmail;
    private Button btnUpdateProfile;

    private Uri uri;
    private HomeActivity homeActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_profile, container, false);

        initUI();
        setUserInformation();
        initListener();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_in_person, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_in_person){
            drawerLayout.openDrawer(GravityCompat.END);
        }
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_notice) {

        } else if (id == R.id.nav_sign_out) {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    private void initUI() {
        homeActivity = (HomeActivity) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");

        drawerLayout = view.findViewById(R.id.drawer_layout);
        NavigationView navigationView = view.findViewById(R.id.navigation_view);

        imgAvatar = view.findViewById(R.id.img_avatar_profile);
        edtUsername = view.findViewById(R.id.edt_username_profile);
        tvEmail = view.findViewById(R.id.tv_email_profile);
        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        
        navigationView.setNavigationItemSelectedListener(this);
        setHasOptionsMenu(true);
    }

    private void setUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        Toast.makeText(getActivity(),"Hi "+user.getDisplayName(),Toast.LENGTH_LONG).show();

        edtUsername.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatar);
    }

    private void initListener() {

        imgAvatar.setOnClickListener(v -> onClickRequestPermission());

        btnUpdateProfile.setOnClickListener(v -> onClickUpdateProfile());
    }

    private void onClickRequestPermission() {

        if(homeActivity == null){
            return;
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            homeActivity.openGallery();
            return;
        }

        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            homeActivity.openGallery();
        }else{
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    private void onClickUpdateProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            return;
        }

        progressDialog.show();
        String strUsername = edtUsername.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(strUsername)
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Update Profile Success", Toast.LENGTH_LONG).show();
//                        homeActivity.showUserInformation();
                    }
                });
    }

    public void setBitmapImageView(Bitmap bitmapImageView){
        imgAvatar.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
        this.uri = mUri;
    }

}
