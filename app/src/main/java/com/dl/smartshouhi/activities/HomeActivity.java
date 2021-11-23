package com.dl.smartshouhi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.dl.smartshouhi.R;

public class HomeActivity extends AppCompatActivity {

    private ImageView imgAddInvoice;
    private ImageView imgChart;
    private ImageView imgHistory;
    private ImageView imgFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initUI();
        initListener();
    }

    private void initUI() {
        imgAddInvoice = findViewById(R.id.img_add_an_invoice);
        imgChart = findViewById(R.id.img_chart);
        imgHistory = findViewById(R.id.img_history);
        imgFavorite = findViewById(R.id.img_favorite);
    }

    private void initListener() {
        imgAddInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddInvoice();
            }
        });

        imgChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowChart();
            }
        });

        imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowHistory();
            }
        });

        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowFavorite();
            }
        });

    }

    private void onClickAddInvoice() {
        Intent intent = new Intent(this, InvoiceInformationActivity.class);
        startActivity(intent);
    }

    private void onClickShowChart() {
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }

    private void onClickShowHistory() {
    }

    private void onClickShowFavorite() {
    }

}
