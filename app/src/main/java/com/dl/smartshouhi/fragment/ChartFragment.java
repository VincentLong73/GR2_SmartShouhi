package com.dl.smartshouhi.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.Invoice;
import com.dl.smartshouhi.model.User;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
//    private SwitchMaterial btnSwitch;
    private Switch btnSwitch;
    private Spinner spinnerYear;

    private long totalUser;
    private int indexUserCurrent;
    private int totalInvoice;
    private int yearSelected ;
    private List<String> listYear;



    public ChartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        initUI();

        getTotalUserOnFb();

        btnSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            getTotalUserOnFb();
//            if(btnSwitch.isChecked()){
//                replaceFragmentChart(totalCostOfListInvoiceYear,MONTHS,MAX_X_VALUE_MONTH);
//            }else{
//                replaceFragmentChart(totalCostOfListInvoiceWeek,DAYS,MAX_X_VALUE);
//            }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearSelected = Integer.parseInt(spinnerAdapter.getItem(position));
                getTotalUserOnFb();
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


    private void getTotalUserOnFb(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();
        myRef.child("totalUser").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                setTotalUser((Long) Long.parseLong(String.valueOf(task.getResult().getValue())));
                getIdUserCurrent();
            }
        });
    }

    private void getIdUserCurrent(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        User user = new User(mAuth.getCurrentUser().getEmail());

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        for(int i = 0 ; i<getTotalUser() ; i++){
            int finalI = i;
            myRef.child(i+"").child("email").get().addOnCompleteListener(task -> {

                if(task.isSuccessful()){
                    String email = String.valueOf(task.getResult().getValue());
                    if(email.equals(user.getEmail())){
                        setIndexUserCurrent(finalI);
                        getListInvoiceDatabase();
                    }

                }
            });
        }
    }

    private void getListInvoiceDatabase(){


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference(getIndexUserCurrent()+"/invoices");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invoiceList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        Invoice invoice = dataSnapshot.getValue(Invoice.class);
                        if (invoice != null) {
                            invoiceList.add(invoice);
                        }
                    } catch (DatabaseException e) {
                        if(dataSnapshot.getKey().equals("totalInvoice")){
                            totalInvoice = dataSnapshot.getValue(Integer.class);
                        }
                    }
                }

//                processingDataWeek();
//                processingDataYear();
                if(btnSwitch.isChecked()){
                    processingDataYear();
                    replaceFragmentChart(totalCostOfListInvoiceYear,MONTHS,MAX_X_VALUE_MONTH);
                }else{
                    processingDataWeek();
                    replaceFragmentChart(totalCostOfListInvoiceWeek,DAYS,MAX_X_VALUE);
                }

//                replaceFragmentChart(totalCostOfListInvoiceWeek,DAYS,MAX_X_VALUE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void processingDataWeek() {
        //Khoi tao mang list invoice
        initTotalCostOfListInvoiceWeek();

        for(Invoice invoice : invoiceList){

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                calendar.setTime(dateFormat.parse(invoice.getTimestamp()));
                if(calendar.get(Calendar.YEAR) == yearSelected) {
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                    if (weekOfYear == 1) {
                        weekOfYear = 52;
                    } else {
                        weekOfYear -= 1;
                    }
                    totalCostOfListInvoiceWeek[weekOfYear - 1][dayOfWeek - 1] += invoice.getTotalCost();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
    private void processingDataYear() {

        //Khoi tao mang list invoice
        initTotalCostOfListInvoiceYear();

        for(Invoice invoice : invoiceList){

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                calendar.setTime(dateFormat.parse(invoice.getTimestamp()));

                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                totalCostOfListInvoiceYear[year - 2020][month] += invoice.getTotalCost();

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void replaceFragmentChart(Float[][] totalCostOfListInVoice,String[] xLabels,  int maxXAxis){


        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_show_chart,
                new BarChartFragment(xLabels,maxXAxis,totalCostOfListInVoice,yearSelected),
                "Bar Chart Fragment");
        transaction.commit();
    }

    public long getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(long totalUser) {
        this.totalUser = totalUser;
    }

    public int getIndexUserCurrent() {
        return indexUserCurrent;
    }

    public void setIndexUserCurrent(int indexUserCurrent) {
        this.indexUserCurrent = indexUserCurrent;
    }
}
