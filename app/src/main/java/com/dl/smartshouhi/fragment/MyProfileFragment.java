package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MyProfileFragment extends Fragment {

    public static final int MY_REQUEST_CODE = 311;
    private View mView;

    private ImageView imgAvatar;
    private EditText edtFullName;
    private TextView tvEmail;
    private Button btnUpdateProfile;
    private Uri mUri;
    private MainActivity mainActivity;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI();
        mainActivity = (MainActivity) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");

        setUserInformation();
        initListener();
        return mView;
    }

    private void initUI(){
        imgAvatar = mView.findViewById(R.id.img_avatar);
        edtFullName = mView.findViewById(R.id.edt_fullname);
        tvEmail = mView.findViewById(R.id.tv_email);
        btnUpdateProfile = mView.findViewById(R.id.btn_update_profile);
    }

    private void setUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        Toast.makeText(getActivity(),user.getDisplayName(),Toast.LENGTH_LONG).show();

        edtFullName.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default);
    }


    private void initListener() {

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateProfile();
            }
        });
    }

    private void onClickRequestPermission() {

        if(mainActivity == null){
            return;
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            mainActivity.openGallery();
            return;
        }

        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            mainActivity.openGallery();
        }else{
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }
    public void setBitmapImageView(Bitmap bitmapImageView){
        Toast.makeText(getActivity(), bitmapImageView.getGenerationId()+"", Toast.LENGTH_LONG).show();
        if(bitmapImageView != null){
            imgAvatar.setImageBitmap(bitmapImageView);
        }
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

    private void onClickUpdateProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            return;
        }

        progressDialog.show();
        String strFullName = edtFullName.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(strFullName)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Update Profile Success", Toast.LENGTH_LONG).show();
                            mainActivity.showUserInformation();
                        }
                    }
                });
    }
}
