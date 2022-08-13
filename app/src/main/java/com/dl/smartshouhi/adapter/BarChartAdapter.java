package com.dl.smartshouhi.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dl.smartshouhi.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class BarChartAdapter extends RecyclerView.Adapter<BarChartAdapter.BarChartViewHolder> {



    private final List<BarData> barDataList;
    private final String[] xLabels;
    private final int yearSelected;


    public BarChartAdapter(List<BarData> barDataList, String[] xLabels, int yearSelected) {
        this.barDataList = barDataList;
        this.xLabels = xLabels;
        this.yearSelected = yearSelected;
    }


    @NonNull
    @Override
    public BarChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bar_chart, parent, false);
        return new BarChartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarChartAdapter.BarChartViewHolder holder, int position) {


        if(xLabels.length == 7){
            String tvTime;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Calendar dateSunday = Calendar.getInstance();

            if (position == 51) {
                position = 1;
            } else {
                position += 1;
            }


            dateSunday.set(Calendar.YEAR,yearSelected);
            dateSunday.set(Calendar.WEEK_OF_YEAR,position);
            Log.e("Sunday WEEK_OF_YEAR", dateSunday.get(Calendar.WEEK_OF_YEAR)+"");
            dateSunday.set(Calendar.DAY_OF_WEEK,1);


            Calendar dateSaturday = Calendar.getInstance();

            dateSaturday.set(Calendar.YEAR,yearSelected);
            dateSaturday.set(Calendar.WEEK_OF_YEAR,position);
            Log.e("Saturday WEEK_OF_YEAR", dateSaturday.get(Calendar.WEEK_OF_YEAR)+"");
            dateSaturday.set(Calendar.DAY_OF_WEEK,7);


            tvTime = dateFormat.format(dateSunday.getTime())+" -> "+dateFormat.format(dateSaturday.getTime());

            holder.tvTime.setText(tvTime);
        }

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
        private final TextView tvTime;
        public BarChartViewHolder(@NonNull View itemView) {
            super(itemView);
            chart = itemView.findViewById(R.id.item_bar_chart);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
