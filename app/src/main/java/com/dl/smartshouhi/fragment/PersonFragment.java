package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activity.HomeActivity;
import com.dl.smartshouhi.activity.SignInActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static com.dl.smartshouhi.constaint.Constant.DOB_KEY;
import static com.dl.smartshouhi.constaint.Constant.EMAIL_KEY;
import static com.dl.smartshouhi.constaint.Constant.FULLNAME_KEY;
import static com.dl.smartshouhi.constaint.Constant.ID_KEY;
import static com.dl.smartshouhi.constaint.Constant.ISADMIN_KEY;
import static com.dl.smartshouhi.constaint.Constant.PASSWORD_KEY;
import static com.dl.smartshouhi.constaint.Constant.PHONE_KEY;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constant.URL_CHANGE_PASSWORD;
import static com.dl.smartshouhi.constaint.Constant.URL_CHANGE_PROFILE;
import static com.dl.smartshouhi.constaint.Constant.USERNAME_KEY;

public class PersonFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MY_REQUEST_CODE = 311;

    private DrawerLayout drawerLayout;
    private View view;
    private NavigationView mNavigationView;
    private View viewDialogUpdate;
    private View viewDialogChangePassword;
    private ProgressDialog progressDialog;
    private ImageButton imgButtonCalendar;

    private Dialog dialogUpdate;
    private Dialog dialogChangePassword;


    private ImageView imgAvatar;
    private TextView tvFullName;
    private TextView tvEmail;
    private TextView tvUserName;
    private TextView tvDob;
    private TextView tvPhone;
    private ImageView imgAvatarHeader;
    private TextView tvEmailHeader;
    private TextView tvUsernameHeader;
    private Button btnUpdateProfile;
    private Button btnChangePassword;


    private ImageView imgAvatarUpdate;
    private ImageButton btnSelectImageUpdate;
    private EditText edtPhoneUpdate;
    private EditText edtFullNameUpdate;
    private EditText edtUserNameUpdate;
    private EditText edtDob;
    private Button btnCancelUpdate;
    private Button btnYesUpdate;

    private EditText edtNewPassword;
    private EditText edtOldPassword;
    private EditText edtConfirmPassword;
    private Button btnCancelChange;
    private Button btnYesChange;

    private Uri uri;
    private HomeActivity homeActivity;

    private String email, fullName, userName, phone, dob;

    private RequestQueue requestQueue;
    private SharedPreferences sharedpreferences;


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

            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.remove(EMAIL_KEY);
            editor.remove(USERNAME_KEY);
            editor.remove(FULLNAME_KEY);
            editor.remove(PHONE_KEY);
            editor.remove(DOB_KEY);
            editor.remove(ISADMIN_KEY);
            editor.remove(ID_KEY);
            editor.remove(PASSWORD_KEY);
            editor.clear();
            editor.commit();


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
        tvFullName = view.findViewById(R.id.tv_fullName_profile_show);
        tvEmail = view.findViewById(R.id.tv_email_profile_show);
        tvUserName = view.findViewById(R.id.tv_username_profile_show);
        tvPhone = view.findViewById(R.id.tv_phone_profile_show);
        tvDob = view.findViewById(R.id.tv_dob_profile_show);

        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        btnChangePassword = view.findViewById(R.id.btn_change_password);

        /*S- Bat dau khoi tao UI trong Dialog Change Password */

        dialogChangePassword = new Dialog(getActivity());
        dialogChangePassword.setCancelable(true);
        viewDialogChangePassword  = getActivity().getLayoutInflater().inflate(R.layout.layout_dialog_change_password, null);
        dialogChangePassword.setContentView(viewDialogChangePassword);

        edtOldPassword = viewDialogChangePassword.findViewById(R.id.edt_old_password);
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
        imgButtonCalendar = viewDialogUpdate.findViewById(R.id.btn_calendar_profile);
        edtFullNameUpdate = viewDialogUpdate.findViewById(R.id.edt_fullName_update);
        edtUserNameUpdate = viewDialogUpdate.findViewById(R.id.edt_username_update);
        edtDob = viewDialogUpdate.findViewById(R.id.edt_dob_update);
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

        requestQueue = Volley.newRequestQueue(getActivity());
    }

    private void setUserInformation(){

        // getting data from shared prefs and
        // storing it in our string variable.
        sharedpreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedpreferences.getString(EMAIL_KEY, "");
        fullName = sharedpreferences.getString(FULLNAME_KEY, "");
        userName = sharedpreferences.getString(USERNAME_KEY, "");
        phone = sharedpreferences.getString(PHONE_KEY, "");
        dob = sharedpreferences.getString(DOB_KEY, "");


        if(email == null){
            return;
        }

        tvFullName.setText(fullName);
        tvEmail.setText(email);
        tvEmailHeader.setText(email);
        tvUsernameHeader.setText(userName);
        tvUserName.setText(userName);
        tvDob.setText(dob);
        tvPhone.setText(phone);



//        tvFullName.setText(user.getDisplayName());
//        tvEmail.setText(user.getEmail());
//        tvEmailHeader.setText(user.getEmail());
//        tvUsernameHeader.setText(user.getDisplayName());

//        uri = user.getPhotoUrl();
//
//        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatar);
//        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatarHeader);
    }

    private void initListener() {

        btnUpdateProfile.setOnClickListener(v -> {
//            onClickUpdateProfile();
            showDialogUpdate();
        });

        btnChangePassword.setOnClickListener(v -> {
            showDialogChangePassword();
        });

        imgButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCalendar();
            }
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

        String strFullName = edtFullNameUpdate.getText().toString().trim();
        String strUsername = edtUserNameUpdate.getText().toString().trim();
        String strPhone = edtPhoneUpdate.getText().toString().trim();
        String strDob = edtDob.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHANGE_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.substring(1, response.length()-1);
                Gson gson = new Gson();
                String[] listResult = response.split("#");
                if(listResult[0].equals("200")){
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    // below two lines will put values for
                    // email and password in shared preferences.
                    editor.putString(FULLNAME_KEY, strFullName);
                    editor.putString(USERNAME_KEY, strUsername);
                    editor.putString(PHONE_KEY, strPhone);
                    editor.putString(DOB_KEY, strDob);

                    // to save our data with key and value.
                    editor.apply();

                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                    setUserInformation();
                    dialogUpdate.dismiss();

                }else {
                    dialogUpdate.dismiss();
                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                }

            }
        }, error -> {
            dialogUpdate.dismiss();
            Toast.makeText(getActivity(), "Update Error", Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("email", email);
                params.put("userName",strUsername);
                params.put("fullName",strFullName);
                params.put("phone",strPhone);
                params.put("dob",strDob);
                return params;
            }
        };

        requestQueue.add(stringRequest);



//         UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                 .setDisplayName(strFullName)
//                 .setPhotoUri(uri)
//                 .build();

//         user.updateProfile(profileUpdates)
//                 .addOnCompleteListener(task -> {
//                     progressDialog.dismiss();
//                     if (task.isSuccessful()) {
//                         Toast.makeText(getActivity(), "Update Profile Success", Toast.LENGTH_LONG).show();
// //                        homeActivity.showUserInformation();
//                         setUserInformation();

//                         dialogUpdate.dismiss();
//                     }
//                 });
    }


    public void setBitmapImageView(Bitmap bitmapImageView){
        imgAvatarUpdate.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
        this.uri = mUri;
    }

    private void showDialogUpdate(){

        if( phone == null){
            edtPhoneUpdate.setText("");
        }else {
            edtPhoneUpdate.setText(phone.trim());
        }
        if(fullName == null){
            edtFullNameUpdate.setText("");
        }else {
            edtFullNameUpdate.setText(fullName.trim());
        }
        if(userName == null){
            edtUserNameUpdate.setText("");
        }else {
            edtUserNameUpdate.setText(userName.trim());
        }

        edtDob.setText(dob.trim());

//        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatarUpdate);

        initListenerDialogUpdate();

        dialogUpdate.show();
    }


    private void initListenerDialogUpdate(){
        btnSelectImageUpdate.setOnClickListener(v -> onClickRequestPermission());

        btnCancelUpdate.setOnClickListener(view -> dialogUpdate.dismiss());

        btnYesUpdate.setOnClickListener(view -> onClickUpdateProfile());
    }

    private void onClickChangePassword() {

        String strOldPassword = edtOldPassword.getText().toString().trim();
        String strNewPassword = edtNewPassword.getText().toString().trim();
        String strConfirmPassword = edtConfirmPassword.getText().toString().trim();

        if(strNewPassword.equals(strConfirmPassword)){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHANGE_PASSWORD, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    response = response.replace("\\", "");
                    response = response.replace("\"{", "{");
                    response = response.replace("}\"", "}");
                    response = response.substring(1, response.length()-1);
                    Gson gson = new Gson();
                    String[] listResult = response.split("#");
                    if(listResult[0].equals("200")){
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        // below two lines will put values for
                        // email and password in shared preferences.
                        editor.putString(PASSWORD_KEY, strNewPassword);
                        // to save our data with key and value.
                        editor.apply();

                    }
                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                    dialogChangePassword.dismiss();

                }
            }, error -> {
                Toast.makeText(getActivity(), "Update Password Error", Toast.LENGTH_SHORT).show();
                dialogChangePassword.dismiss();
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();

                    params.put("email", email);
                    params.put("pass_word_old",strOldPassword);
                    params.put("pass_word_new",strNewPassword);
                    return params;
                }
            };

            requestQueue.add(stringRequest);
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

    private void displayCalendar() {
        final View dialogView = viewDialogUpdate.inflate(getActivity(), R.layout.layout_date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {

            DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);

            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                    datePicker.getMonth(),
                    datePicker.getDayOfMonth());

            String currentDate = DateFormat.format("yyyy-MM-dd", calendar).toString();
            edtDob.setText(currentDate);

            alertDialog.dismiss();
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }


}
