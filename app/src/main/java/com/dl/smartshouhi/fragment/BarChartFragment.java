package com.dl.smartshouhi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.Invoice;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BarChartFragment extends Fragment {

    private static final String SET_LABEL = "VND";

    private String[] xLabels;
    private int totalInvoice;
    private List<Invoice> invoiceList;
    private int maxXAxis;

    public BarChartFragment(String[] xLabels, int totalInvoice, List<Invoice> invoiceList, int maxXAxis) {
        this.xLabels = xLabels;
        this.totalInvoice = totalInvoice;
        this.invoiceList = invoiceList;
        this.maxXAxis = maxXAxis;
    }
    private View mView;
    private BarChart chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_bar_chart_child, container, false);
        initUI();
        return mView;
    }

    private void initUI() {
        chart = mView.findViewById(R.id.fragment_chart);
        createChart();
    }

    private void createChart(){
        BarData data = createChartData(totalInvoice, invoiceList);
        configureChartAppearance();
        prepareChartData(data);
    }

    private BarData createChartData(int totalInvoice1, List<Invoice> invoices) {

        ArrayList<BarEntry> values = new ArrayList<>();
        for(Invoice invoice : invoices){
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                calendar.setTime(dateFormat.parse(invoice.getTimestamp()));
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int dayOfMonth = calendar.get(Calendar.MONTH);
                invoice.setMonth(dayOfMonth);
                invoice.setDayOfWeek(dayOfWeek);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        for (int i = 0; i < maxXAxis; i++){
            float y=0f;
            if(maxXAxis == 7){
                for(Invoice invoice : invoices){
                    if(invoice.getDayOfWeek()-1 == i){
                        y+=Float.parseFloat(invoice.getTotalCost()+"");
                    }
                }
            }else if(maxXAxis == 12){
                for(Invoice invoice : invoices){
                    if(invoice.getMonth()-1 == i){
                        y+=Float.parseFloat(invoice.getTotalCost()+"");
                    }
                }
            }

            values.add(new BarEntry((float) i, y));
        }

        BarDataSet set1 = new BarDataSet(values, SET_LABEL);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        return new BarData(dataSets);
    }

    private void configureChartAppearance() {
        chart.getDescription().setEnabled(false);
        chart.setDrawValueAboveBar(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabels[(int) value];
            }
        });


        YAxis axisLeft = chart.getAxisLeft();
        axisLeft.setGranularity(10f);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = chart.getAxisRight();
        axisRight.setGranularity(10f);
        axisRight.setAxisMinimum(0);
    }

    private void prepareChartData(BarData data) {
        data.setValueTextSize(12f);
        chart.setData(data);
        chart.invalidate();
    }
}
