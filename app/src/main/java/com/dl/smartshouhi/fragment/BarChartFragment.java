package com.dl.smartshouhi.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.util.Calendar;
import java.util.List;

public class BarChartFragment extends Fragment {

    private static final String SET_LABEL = "VND";
    private final String[] xLabels;
    private final int maxXAxis;
    private final Float[][] totalCostOfListInvoice;
    private final int yearSelected;


    public BarChartFragment(String[] xLabels, int maxXAxis, Float[][] totalCostOfListInvoice, int yearSelected) {
        this.xLabels = xLabels;
        this.maxXAxis = maxXAxis;
        this.totalCostOfListInvoice = totalCostOfListInvoice;
        this.yearSelected = yearSelected;
    }

    private View mView;
    private ViewPager2 viewPager;

    @RequiresApi(api = Build.VERSION_CODES.O)
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


        int position;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearSelected);
        if(maxXAxis == 7){
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            Log.e("Year ",  weekOfYear+"");
            if(weekOfYear == 1){
                weekOfYear = 52;
            }else {
                weekOfYear -= 1;
            }
            position = weekOfYear -1;
        }else{
            position = yearSelected - 2020;
        }
        viewPager.setCurrentItem(position);
    }



    private List<BarData> generateData() {

        List<BarData> barDataList = new ArrayList<>();

        for (Float[] floats : totalCostOfListInvoice) {

            ArrayList<BarEntry> values = new ArrayList<>();
            for (int j = 0; j < maxXAxis; j++) {
                float y = 0f;
                if (floats[j] > 0f) {
                    y += floats[j];
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

        BarChartAdapter barChartAdapter = new BarChartAdapter(generateData(), xLabels, yearSelected);
        viewPager.setAdapter(barChartAdapter);
    }

}
