package com.dl.smartshouhi.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activity.HomeActivity;

import static com.dl.smartshouhi.constaint.Constant.DOB_KEY;
import static com.dl.smartshouhi.constaint.Constant.EMAIL_KEY;
import static com.dl.smartshouhi.constaint.Constant.FULLNAME_KEY;
import static com.dl.smartshouhi.constaint.Constant.PHONE_KEY;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constant.USERNAME_KEY;

public class HomeFragment extends Fragment {

    private View mView;
    private ImageView imgAvatar;
    private TextView tvUsername;

    private ImageView imgAddInvoice;
    private ImageView imgChart;
    private ImageView imgHistory;
    private ImageView imgFavorite;

    private SharedPreferences sharedpreferences;
    private RequestQueue requestQueue;
    private String emailShared, userNameShared, fullNameShared;
    private String email, fullName, userName, phone, dob;

    private final InvoiceInformationFragment invoiceInformationFragment;
    private HomeActivity homeActivity;


    public HomeFragment(InvoiceInformationFragment invoiceInformationFragment) {
        this.invoiceInformationFragment = invoiceInformationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.activity_dashboard, container, false);

        initUI();
        setUserInformation();
        initListener();

        return mView;
    }

    private void initUI() {
        homeActivity = (HomeActivity) getActivity();

        imgAvatar = mView.findViewById(R.id.img_avatar_dashboard);
        tvUsername = mView.findViewById(R.id.tv_username_dashboard);

        imgAddInvoice = mView.findViewById(R.id.img_add_an_invoice);
        imgChart = mView.findViewById(R.id.img_chart);
        imgHistory = mView.findViewById(R.id.img_history);
//        imgFavorite = mView.findViewById(R.id.img_favorite);


    }

    private void setUserInformation(){

        sharedpreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // getting data from shared prefs and
        // storing it in our string variable.
        email = sharedpreferences.getString(EMAIL_KEY, "");
        fullName = sharedpreferences.getString(FULLNAME_KEY, "");
        userName = sharedpreferences.getString(USERNAME_KEY, "");
        phone = sharedpreferences.getString(PHONE_KEY, "");
        dob = sharedpreferences.getString(DOB_KEY, "");

        tvUsername.setText(userName.trim());
    }

    private void initListener() {
        imgAddInvoice.setOnClickListener(v -> onClickAddInvoice());

        imgChart.setOnClickListener(v -> onClickShowChart());

        imgHistory.setOnClickListener(v -> onClickShowHistory());

//        imgFavorite.setOnClickListener(v -> onClickShowFavorite());

    }

    private void onClickAddInvoice() {

       replaceFragment(invoiceInformationFragment,"InvoiceInformationFragment");
        // replaceFragment(new InvoiceInformationFragment1(), "InvoiceInformationFragment");
    }

    private void onClickShowChart() {
        replaceFragment(new ChartFragment(),"ChartFragment");
    }

    private void onClickShowHistory() {
        replaceFragment(new HistoryFragment(), "HistoryFragment");
    }

    private void onClickShowFavorite() {
    }

    private void replaceFragment(Fragment fragment, String nameFragment){

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_home, fragment, nameFragment);
        transaction.commit();
    }

    public void setBitmapImageView(Bitmap bitmapImageView){
        imgAvatar.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
    }

}
