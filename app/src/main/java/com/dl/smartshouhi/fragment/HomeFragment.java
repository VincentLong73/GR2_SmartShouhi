package com.dl.smartshouhi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dl.smartshouhi.R;

public class HomeFragment extends Fragment {

    private View mView;
    private ImageView imgAddInvoice;
    private ImageView imgChart;
    private ImageView imgHistory;
    private ImageView imgFavorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.activity_dashboard, container, false);

        initUI();
        initListener();

        return mView;
    }

    private void initUI() {
        imgAddInvoice = mView.findViewById(R.id.img_add_an_invoice);
        imgChart = mView.findViewById(R.id.img_chart);
        imgHistory = mView.findViewById(R.id.img_history);
        imgFavorite = mView.findViewById(R.id.img_favorite);
    }

    private void initListener() {
        imgAddInvoice.setOnClickListener(v -> onClickAddInvoice());

        imgChart.setOnClickListener(v -> onClickShowChart());

        imgHistory.setOnClickListener(v -> onClickShowHistory());

        imgFavorite.setOnClickListener(v -> onClickShowFavorite());

    }

    private void onClickAddInvoice() {
//        Intent intent = new Intent(getActivity(), InvoiceInformationActivity.class);
//        startActivity(intent);
        replaceFragment(new InvoiceInformationFragment(),"InvoiceInformationFragment");
    }

    private void onClickShowChart() {
        replaceFragment(new ChartFragment(),"ChartFragment");
    }

    private void onClickShowHistory() {
    }

    private void onClickShowFavorite() {
    }

    private void replaceFragment(Fragment fragment, String nameFragment){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_home, fragment, nameFragment);
        transaction.commit();
//        FragmentManager fragmentManager = getParentFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.hide(HomeFragment1.this);
//        fragmentTransaction.add(R.id.fragment_home, fragment);
//        fragmentTransaction.commit();
    }

}
