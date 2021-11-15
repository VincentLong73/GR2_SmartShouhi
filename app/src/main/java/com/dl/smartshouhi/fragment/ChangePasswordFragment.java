package com.dl.smartshouhi.fragment;

import android.app.ProgressDialog;
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

import com.dl.smartshouhi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private View mView;
    private EditText edtNewPassword;
    private Button btnChangePassword;
    private ProgressDialog progressDialog;

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
        btnChangePassword = mView.findViewById(R.id.btn_change_password);
    }


    private void onClickChangePassword() {
        String strNewPassword = edtNewPassword.getText().toString().trim();
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(strNewPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Change Password success", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }else{
                            //show dialog re-Authenticate
                        }
                    }
                });
    }

    private void reAuthentication(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234");

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            onClickChangePassword();
                        }else{

                        }
                    }
                });
    }
}
