package com.dl.smartshouhi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.adapter.BarChartAdapter;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class BarChartFragment extends Fragment {

    private static final String SET_LABEL = "VND";
    private String[] xLabels;
    private int maxXAxis;
    private Float[][] totalCostOfListInvoice;

    public BarChartFragment(String[] xLabels, Float[][] totalCostOfListInvoice, int maxXAxis) {
        this.xLabels = xLabels;
        this.totalCostOfListInvoice = totalCostOfListInvoice;
        this.maxXAxis = maxXAxis;
    }
    private View mView;
    private ViewPager2 viewPager;
    private BarChartAdapter barChartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_bar_chart_child, container, false);
        initUI();
        return mView;
    }

    private void initUI() {
        viewPager = mView.findViewById(R.id.view_pager_show_bar_chart_in_fragment);
        setBarChartAdapter();
    }

    private List<BarData> generateData() {

        List<BarData> barDataList = new ArrayList<>();

        for (int i = 0; i < totalCostOfListInvoice.length; i++) {

            ArrayList<BarEntry> values = new ArrayList<>();
            for (int j = 0; j < maxXAxis; j++) {
                float y = 0f;
                if (totalCostOfListInvoice[i][j] > 0f) {
                    y += totalCostOfListInvoice[i][j];
                }
                values.add(new BarEntry((float) j, y));
            }

            BarDataSet dataSet = new BarDataSet(values, SET_LABEL);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);
            barDataList.add(new BarData(dataSets));
        }


        return barDataList;
    }

    private void setBarChartAdapter(){
        viewPager.setOffscreenPageLimit(3);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager.setPageTransformer(compositePageTransformer);

        barChartAdapter = new BarChartAdapter(generateData(), getContext(), xLabels);
        viewPager.setAdapter(barChartAdapter);
    }

}
