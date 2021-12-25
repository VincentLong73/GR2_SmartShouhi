package com.dl.smartshouhi.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dl.smartshouhi.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;




public class BarChartAdapter extends RecyclerView.Adapter<BarChartAdapter.BarChartViewHolder> {



    private final List<BarData> barDataList;
    private String[] xLabels;
    private Context context;


    public BarChartAdapter(List<BarData> barDataList, Context context, String[] xLabels) {
        this.barDataList = barDataList;
        this.xLabels = xLabels;
        this.context = context;
    }


    @NonNull
    @Override
    public BarChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bar_chart, parent, false);
        return new BarChartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarChartAdapter.BarChartViewHolder holder, int position) {

        holder.chart.setTag(holder);
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setDrawValueAboveBar(false);


        XAxis xAxis = holder.chart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabels[(int) value];
            }
        });


        YAxis axisLeft = holder.chart.getAxisLeft();
        axisLeft.setGranularity(10f);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = holder.chart.getAxisRight();
        axisRight.setGranularity(10f);
        axisRight.setAxisMinimum(0);


        //prepareChartData
        BarData data = barDataList.get(position);
        data.setValueTextSize(12f);
        holder.chart.setData(data);
        holder.chart.invalidate();


    }

    @Override
    public int getItemCount() {
        return barDataList.size();
    }


    public static class BarChartViewHolder extends RecyclerView.ViewHolder{
        private final BarChart chart;
        public BarChartViewHolder(@NonNull View itemView) {
            super(itemView);
            chart = itemView.findViewById(R.id.item_bar_chart);
        }
    }
}
