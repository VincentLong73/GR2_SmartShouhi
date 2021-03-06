package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activity.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static com.dl.smartshouhi.constaint.Constant.EMAIL_KEY;
import static com.dl.smartshouhi.constaint.Constant.FULLNAME_KEY;
import static com.dl.smartshouhi.constaint.Constant.USERNAME_KEY;

public class MyProfileFragment extends Fragment {

    public static final int MY_REQUEST_CODE = 311;
    private static final String TAG_IMAGE = "TAG_IMAGE";
    private View mView;

    private ImageView imgAvatar;
    private EditText edtFullName;
    private TextView tvEmail;
    private Button btnUpdateProfile;
    private ImageButton imgButtonCalendar;
    private Uri mUri;
//    private MainActivity mainActivity;
    private HomeActivity homeActivity;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedpreferences;
    private RequestQueue requestQueue;
    private String emailShared, userNameShared, fullNameShared;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI();
//        mainActivity = (MainActivity) getActivity();
        homeActivity = (HomeActivity) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");

        setUserInformation();
        initListener();
        return mView;
    }

    private void initUI(){
        imgAvatar = mView.findViewById(R.id.img_avatar_fragment_profile);
        edtFullName = mView.findViewById(R.id.edt_fullname);
        tvEmail = mView.findViewById(R.id.tv_email);
        imgButtonCalendar = mView.findViewById(R.id.btn_calendar_profile);
        btnUpdateProfile = mView.findViewById(R.id.btn_update_profile);

        emailShared = sharedpreferences.getString(EMAIL_KEY, "abc@gmail.com");
        userNameShared = sharedpreferences.getString(USERNAME_KEY, "Van A");
        fullNameShared = sharedpreferences.getString(FULLNAME_KEY, "Nguyen Van A");
    }

    private void setUserInformation(){


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        Toast.makeText(getActivity(),"Hi "+userNameShared,Toast.LENGTH_LONG).show();

        edtFullName.setText(fullNameShared);
        tvEmail.setText(emailShared);
//        Log.e(TAG_IMAGE, user.getPhotoUrl()+"");
//        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatar);
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
    public void setBitmapImageView(Bitmap bitmapImageView){
        imgAvatar.setImageBitmap(bitmapImageView);
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
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Update Profile Success", Toast.LENGTH_LONG).show();
//                        homeActivity.showUserInformation();
                    }
                });
    }
}
