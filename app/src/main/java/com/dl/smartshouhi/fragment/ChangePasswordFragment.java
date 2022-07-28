package com.dl.smartshouhi.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.UserModel;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.dl.smartshouhi.constaint.Constant.EMAIL_KEY;
import static com.dl.smartshouhi.constaint.Constant.FULLNAME_KEY;
import static com.dl.smartshouhi.constaint.Constant.ID_KEY;
import static com.dl.smartshouhi.constaint.Constant.ISADMIN_KEY;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constant.URL_CHANGE_PASSWORD;
import static com.dl.smartshouhi.constaint.Constant.USERNAME_KEY;

public class ChangePasswordFragment extends Fragment {

    private View mView;
    private EditText edtNewPassword, edtOldPassword;
    private Button btnChangePassword;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedpreferences;
    private String emailShared;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_change_password, container, false);
        initUI();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChangePassword();
            }
        });


        return mView;
    }

    private void initUI(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please waiting...");
        edtNewPassword = mView.findViewById(R.id.edt_new_password);
        edtOldPassword = mView.findViewById(R.id.edt_old_password);
        btnChangePassword = mView.findViewById(R.id.btn_change_password);

        sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // in shared prefs inside het string method
        // we are passing key value as EMAIL_KEY and
        // default value is
        // set to null if not present.
        emailShared = sharedpreferences.getString(EMAIL_KEY, null);
        requestQueue = Volley.newRequestQueue(getContext());
    }


    private void onClickChangePassword() {

        String strOldPassword = edtOldPassword.getText().toString().trim();
        String strNewPassword = edtNewPassword.getText().toString().trim();
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHANGE_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                Gson gson = new Gson();

                String result = gson.fromJson(response, String.class);
                String[] listResult = result.split("#");
                if(listResult[0].equals("200")){
                    UserModel userModel = gson.fromJson(listResult[1], UserModel.class);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    // below two lines will put values for
                    // email and password in shared preferences.
                    editor.putString(EMAIL_KEY, userModel.getEmail());
                    editor.putString(USERNAME_KEY, userModel.getUserName());
                    editor.putString(FULLNAME_KEY, userModel.getFullName());
                    editor.putInt(ID_KEY, userModel.getId());
                    editor.putBoolean(ISADMIN_KEY, userModel.isAdmin());

                    //editor.putString(PASSWORD_KEY, passwordEdt.getText().toString());

                    // to save our data with key and value.
                    editor.apply();
                    //finish();
                }else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                }

            }
        }, error -> {

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("email", emailShared);
                params.put("pass_word_old",strOldPassword);
                params.put("pass_word_new",strNewPassword);
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

//     private void reAuthentication(){
//         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//         AuthCredential credential = EmailAuthProvider
//                 .getCredential("user@example.com", "password1234");

// // Prompt the user to re-provide their sign-in credentials
//         user.reauthenticate(credential)
//                 .addOnCompleteListener(new OnCompleteListener<Void>() {
//                     @Override
//                     public void onComplete(@NonNull Task<Void> task) {
//                         if(task.isSuccessful()){
//                             onClickChangePassword();
//                         }else{

//                         }
//                     }
//                 });
//     }
}
