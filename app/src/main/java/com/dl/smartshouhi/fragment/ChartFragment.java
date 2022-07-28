
package com.dl.smartshouhi.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.Invoice;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dl.smartshouhi.constaint.Constant.ID_KEY;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constant.URL_GET_INVOICE_BY_USER_ID;
import static com.dl.smartshouhi.constaint.Constant.URL_GET_INVOICE_BY_USER_ID_YEAR;

public class ChartFragment extends Fragment {
    private static final int MAX_X_VALUE = 7;
    private static final String[] DAYS = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
    private static final String[] MONTHS = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                                        "JUL","AUG","OCT","SEP","NOV","DEC" };
    private static final int MAX_X_VALUE_MONTH = 12;


    private List<Invoice> invoiceList;
    private Float[][] totalCostOfListInvoiceWeek;
    private Float[][] totalCostOfListInvoiceYear;
    private ArrayAdapter<String> spinnerAdapter;

    private View mView;
    private Switch btnSwitch;
    private Spinner spinnerYear;

    private int yearSelected ;
    private List<String> listYear;

    private SharedPreferences sharedpreferences;
    private int userId;
    private boolean isFlag = false; // false - Year, true - Week
    private boolean isFirst = true;



    public ChartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        initUI();

        btnSwitch.setOnCheckedChangeListener((compoundButton, b) -> {



//            if(btnSwitch.isChecked()){
////                processingDataYear();
//                if(isFlag){
//                    processingDataYear();
//                    isFlag = false;
//                }else {
//                    processingDataWeek();
//                    isFlag = true;
//                }
//            }else{
//                processingDataWeek();
//            }
            if(isFirst){
                isFirst = false;
            }else{
                if(isFlag){
                    processingDataYear();
                    isFlag = false;
                }else {
                    processingDataWeek();
                    isFlag = true;
                }
            }

        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearSelected = Integer.parseInt(spinnerAdapter.getItem(position));

                if(isFirst){
                    isFirst = false;
                }else{
                    if(isFlag){
                        processingDataWeek();
                    }else{
                        processingDataYear();
                    }
                }


//                if(btnSwitch.isChecked()){
//                    processingDataYear();
//                }else{
//                    processingDataWeek();
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        return mView;
    }
    private void initUI() {

        invoiceList = new ArrayList<>();

        initTotalCostOfListInvoiceWeek();
        initTotalCostOfListInvoiceYear();

        btnSwitch = mView.findViewById(R.id.btn_switch);

        spinnerYear = mView.findViewById(R.id.spinner_year);

        spinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,listYear);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(spinnerAdapter);
        yearSelected = Integer.parseInt(spinnerAdapter.getItem(0));

        processingDataYear();

    }

    private void initTotalCostOfListInvoiceWeek(){
        totalCostOfListInvoiceWeek = new Float[52][7];
        for(int i = 0;i<52 ; i ++){
            Arrays.fill(totalCostOfListInvoiceWeek[i], 0f);
        }
    }

    private void initTotalCostOfListInvoiceYear(){
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int numberYear = year - 2020 +1 ;
        listYear = new ArrayList<>();

        Log.e("Check Year",year+"-"+numberYear);

        totalCostOfListInvoiceYear = new Float[numberYear][12];
        for(int i = 0;i<numberYear ; i ++){
            Arrays.fill(totalCostOfListInvoiceYear[i], 0f);
            listYear.add(2020 + i +"");
        }
    }

    private void processingDataWeek() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getActivity());
        initTotalCostOfListInvoiceWeek();

        sharedpreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // getting data from shared prefs and
        // storing it in our string variable.
        userId = sharedpreferences.getInt(ID_KEY, -1);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_INVOICE_BY_USER_ID+userId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.substring(1, response.length() - 1);
                Gson gson = new Gson();

                String[] listResult = response.split("#");;

                if(listResult[0].equals("200")){
                    invoiceList = Arrays.asList(gson.fromJson(listResult[1], Invoice[].class));
                    for(Invoice invoice : invoiceList) {
                        Calendar calendar = Calendar.getInstance();
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            String strDate = dateFormat2.format(dateFormat1.parse(invoice.getTimestamp()));
//                            calendar.setTime(dateFormat.parse(invoice.getTimestamp()));
                            calendar.setTime(dateFormat2.parse(strDate));
                            if (calendar.get(Calendar.YEAR) == yearSelected) {
                                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                                int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                                if (weekOfYear == 1) {
                                    weekOfYear = 52;
                                } else {
                                    weekOfYear -= 1;
                                }
                                totalCostOfListInvoiceWeek[weekOfYear - 1][dayOfWeek - 1] += invoice.getTotalCost();
                            }
                        }catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    replaceFragmentChart(totalCostOfListInvoiceYear,MONTHS,MAX_X_VALUE_MONTH);

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

                return params;
            }
        };

        requestQueue.add(stringRequest);

    }
    private void processingDataYear() {

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getActivity());


        //Khoi tao mang list invoice
        initTotalCostOfListInvoiceYear();
        sharedpreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // getting data from shared prefs and
        // storing it in our string variable.
        userId = sharedpreferences.getInt(ID_KEY, -1);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_INVOICE_BY_USER_ID_YEAR+"id="+userId+"&"+"year="+yearSelected, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.substring(1, response.length() - 1);
                Gson gson = new Gson();

                String[] listResult = response.split("#");

                if(listResult[0].equals("200")){
                    invoiceList = Arrays.asList(gson.fromJson(listResult[1], Invoice[].class));

                    for(Invoice invoice : invoiceList) {
                        Calendar calendar = Calendar.getInstance();
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            String strDate = dateFormat2.format(dateFormat1.parse(invoice.getTimestamp()));
//                            calendar.setTime(dateFormat.parse(invoice.getTimestamp()));
                            calendar.setTime(dateFormat2.parse(strDate));

                            int month = calendar.get(Calendar.MONTH);
                            int year = calendar.get(Calendar.YEAR);
                            float total = invoice.getTotalCost();
                            totalCostOfListInvoiceYear[year - 2020][month] += invoice.getTotalCost();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    replaceFragmentChart(totalCostOfListInvoiceWeek,DAYS,MAX_X_VALUE);

                }else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                }

            }
        }, error -> {

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    private void replaceFragmentChart(Float[][] totalCostOfListInVoice,String[] xLabels,  int maxXAxis){


        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_show_chart,
                new BarChartFragment(xLabels,maxXAxis,totalCostOfListInVoice,yearSelected),
                "Bar Chart Fragment");
        transaction.commit();
    }


}
