package com.dl.smartshouhi.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.Invoice;
import com.dl.smartshouhi.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;

public class ChartFragment extends Fragment {
    private static final int MAX_X_VALUE = 7;
    private static final String[] DAYS = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
    private static final String[] MONTHS = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                                        "JUL","AUG","OCT","SEP","NOV","DEC" };
    private static final int MAX_X_VALUE_MONTH = 12;


    private int totalInvoice;
    private List<Invoice> invoiceList;

    private View mView;
    private SwitchMaterial btnSwitch;

    private long totalUser;
    private int indexUserCurrent;

    private User userCurrent;

    public ChartFragment() {
    }

    public ChartFragment(User userCurrent) {
        this.userCurrent = userCurrent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        initUI();



        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(btnSwitch.isChecked()){
                    replaceFragmentChart(MONTHS,MAX_X_VALUE_MONTH);
                }else{
                    replaceFragmentChart(DAYS,MAX_X_VALUE);
                }
            }
        });

        getTotalUserOnFb();

        return mView;
    }
    private void initUI() {

        invoiceList = new ArrayList<>();
        btnSwitch = mView.findViewById(R.id.btn_switch);


    }

    private void getTotalUserOnFb(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();
        myRef.child("totalUser").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    Log.e("Total User", String.valueOf(task.getResult().getValue()));
                    setTotalUser((Long) Long.parseLong(String.valueOf(task.getResult().getValue())));
                    getIdUserCurrent();
                }
            }
        });
    }

    private void getIdUserCurrent(){
        Log.e("Total User 2 ", getTotalUser()+"");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        User user = new User(mAuth.getCurrentUser().getEmail());

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        for(int i = 0 ; i<getTotalUser() ; i++){
            int finalI = i;
            myRef.child(i+"").child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    if(task.isSuccessful()){
                        String email = String.valueOf(task.getResult().getValue());
                        if(email.equals(user.getEmail())){
                            Log.e("Id", finalI +"");
                            setIndexUserCurrent(finalI);
                            getListInvoiceDatabase();
                        }

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
                replaceFragmentChart(DAYS,MAX_X_VALUE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void replaceFragmentChart(String[] xLabels,  int maxXAxis){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_show_chart,
                new BarChartFragment(xLabels,totalInvoice, invoiceList, maxXAxis),
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
