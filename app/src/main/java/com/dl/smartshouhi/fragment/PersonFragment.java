package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class PersonFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MY_REQUEST_CODE = 311;

    private DrawerLayout drawerLayout;
    private View view;
    private NavigationView mNavigationView;
    private View viewDialogUpdate;
    private View viewDialogChangePassword;
    private ProgressDialog progressDialog;

    private Dialog dialogUpdate;
    private Dialog dialogChangePassword;


    private ImageView imgAvatar;
    private TextView tvFullName;
    private TextView tvEmail;
    private ImageView imgAvatarHeader;
    private TextView tvEmailHeader;
    private TextView tvUsernameHeader;
    private Button btnUpdateProfile;
    private Button btnChangePassword;


    private ImageView imgAvatarUpdate;
    private ImageButton btnSelectImageUpdate;
    private EditText edtPhoneUpdate;
    private EditText edtFullNameUpdate;
    private Button btnCancelUpdate;
    private Button btnYesUpdate;

    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private Button btnCancelChange;
    private Button btnYesChange;

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

        /*S- Bat dau khoi tao UI trong Profile */
        imgAvatar = view.findViewById(R.id.img_avatar_profile);
        tvFullName = view.findViewById(R.id.tv_fullName_profile);
        tvEmail = view.findViewById(R.id.tv_email_profile);

        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        btnChangePassword = view.findViewById(R.id.btn_change_password);

        /*S- Bat dau khoi tao UI trong Dialog Change Password */

        dialogChangePassword = new Dialog(getActivity());
        dialogChangePassword.setCancelable(true);
        viewDialogChangePassword  = getActivity().getLayoutInflater().inflate(R.layout.layout_dialog_change_password, null);
        dialogChangePassword.setContentView(viewDialogChangePassword);

        edtNewPassword = viewDialogChangePassword.findViewById(R.id.edt_new_password);
        edtConfirmPassword = viewDialogChangePassword.findViewById(R.id.edt_confirm_password);

        btnCancelChange = viewDialogChangePassword.findViewById(R.id.btn_cancel_change_password);
        btnYesChange = viewDialogChangePassword.findViewById(R.id.btn_yes_change_password);

        /*E- Ket thuc khoi tao UI trong Dialog Change Password */


        /*E- Ket thuc khoi tao UI trong Profile */

        /*S- Bat dau khoi tao UI trong Dialog Update */
        dialogUpdate = new Dialog(getActivity());
        dialogUpdate.setCancelable(true);

        viewDialogUpdate  = homeActivity.getLayoutInflater().inflate(R.layout.layout_dialog_edit_profile, null);
        dialogUpdate.setContentView(viewDialogUpdate);

        imgAvatarUpdate = viewDialogUpdate.findViewById(R.id.img_avatar_profile_update);
        btnSelectImageUpdate = viewDialogUpdate.findViewById(R.id.btn_image_update);
        edtFullNameUpdate = viewDialogUpdate.findViewById(R.id.edt_fullName_update);
        edtPhoneUpdate = viewDialogUpdate.findViewById(R.id.edt_phone_update);

        btnCancelUpdate = viewDialogUpdate.findViewById(R.id.btn_cancel_update);
        btnYesUpdate = viewDialogUpdate.findViewById(R.id.btn_yes_update);

        /*E- Ket thuc khoi tao UI trong Dialog Update */



        /*S- Bat dau khoi tao UI trong nav drawable */
        drawerLayout = view.findViewById(R.id.drawer_layout);
        mNavigationView = view.findViewById(R.id.navigation_view);

        tvEmailHeader = mNavigationView.getHeaderView(0).findViewById(R.id.tv_email_nav_person);
        tvUsernameHeader = mNavigationView.getHeaderView(0).findViewById(R.id.tv_name_nav_person);
        imgAvatarHeader = mNavigationView.getHeaderView(0).findViewById(R.id.img_avatar_nav_person);

        mNavigationView.setNavigationItemSelectedListener(this);
        setHasOptionsMenu(true);
        /*E- Ket thuc khoi tao UI trong nav drawable */
    }

    private void setUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        Toast.makeText(getActivity(),"Hi "+user.getDisplayName(),Toast.LENGTH_LONG).show();

        tvFullName.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());
        tvEmailHeader.setText(user.getEmail());
        tvUsernameHeader.setText(user.getDisplayName());

        uri = user.getPhotoUrl();

        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatar);
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatarHeader);
    }

    private void initListener() {

        btnUpdateProfile.setOnClickListener(v -> {
//            onClickUpdateProfile();
            showDialogUpdate();
        });

        btnChangePassword.setOnClickListener(v -> {
            showDialogChangePassword();
        });
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
        String strFullName = edtFullNameUpdate.getText().toString().trim();
        String strPhone = edtPhoneUpdate.getText().toString().trim();



        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(strFullName)
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Update Profile Success", Toast.LENGTH_LONG).show();
//                        homeActivity.showUserInformation();
                        setUserInformation();

                        dialogUpdate.dismiss();
                    }
                });
    }


    public void setBitmapImageView(Bitmap bitmapImageView){
        imgAvatarUpdate.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
        this.uri = mUri;
    }

    private void showDialogUpdate(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        if(user.getPhoneNumber() == null){
            edtPhoneUpdate.setText("");
        }else {
            edtPhoneUpdate.setText(user.getPhoneNumber());
        }
        if(user.getDisplayName() == null){
            edtFullNameUpdate.setText("");
        }else {
            edtFullNameUpdate.setText(user.getDisplayName());
        }

        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatarUpdate);

        initListenerDialogUpdate();

        dialogUpdate.show();
    }


    private void initListenerDialogUpdate(){
        btnSelectImageUpdate.setOnClickListener(v -> onClickRequestPermission());

        btnCancelUpdate.setOnClickListener(view -> dialogUpdate.dismiss());

        btnYesUpdate.setOnClickListener(view -> onClickUpdateProfile());
    }

    private void onClickChangePassword() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String strNewPassword = edtNewPassword.getText().toString().trim();
        String strConfirmPassword = edtConfirmPassword.getText().toString().trim();

        if(strNewPassword.equals(strConfirmPassword)){
            user.updatePassword(strNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Change Password Success", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(), "Change Password Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
            dialogChangePassword.dismiss();
        }else {
            Toast.makeText(getActivity(), "Confirm Password miss matching", Toast.LENGTH_LONG).show();
        }

    }

    private void showDialogChangePassword(){

        initListenerDialogChangePassword();
        dialogChangePassword.show();
    }


    private void initListenerDialogChangePassword(){

        btnCancelChange.setOnClickListener(view -> dialogChangePassword.dismiss());

        btnYesChange.setOnClickListener(view -> onClickChangePassword());
    }


}
