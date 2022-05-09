package com.dl.smartshouhi.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.adapter.InvoiceAdapter;
import com.dl.smartshouhi.model.Invoice;
import com.dl.smartshouhi.model.InvoiceModel;
import com.dl.smartshouhi.model.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dl.smartshouhi.constaint.Constaint.ID_KEY;
import static com.dl.smartshouhi.constaint.Constaint.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constaint.URL_GET_INVOICE_BY_USER_ID;

public class HistoryFragment extends Fragment {

    private View mView;
    private RecyclerView rcvInvoices;
    private InvoiceAdapter invoiceAdapter;
    private long totalUser;
    private int indexUserCurrent;

    private EditText edtUpdateSeller, edtUpdateAddress, edtUpdateTotalCost, edtUpdateTimestamp;
    private Button btnCancelUpdate, btnUpdate;
    private Dialog dialogUpdate;


    private int totalInvoice;
    private List<Invoice> invoiceList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_history, container, false);

        initUI();
        processingData();
        //getTotalUserOnFb();

        return mView;
    }


    private void initUI() {

        invoiceList = new ArrayList<>();
        rcvInvoices = mView.findViewById(R.id.rcv_invoices);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvInvoices.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        rcvInvoices.addItemDecoration(dividerItemDecoration);

    }

    private void initRecycleView(){
        invoiceAdapter = new InvoiceAdapter(invoiceList, (invoice, position) -> openDialogUpdateItem(invoice, position));
        rcvInvoices.setAdapter(invoiceAdapter);
    }

    private void openDialogUpdateItem(Invoice invoice, int position){
        dialogUpdate = new Dialog(getActivity());
        dialogUpdate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdate.setContentView(R.layout.layout_dialog_update_item_invoice);
        Window window = dialogUpdate.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogUpdate.setCancelable(false);

        edtUpdateSeller = dialogUpdate.findViewById(R.id.edt_update_seller);
        edtUpdateAddress = dialogUpdate.findViewById(R.id.edt_update_address);
        edtUpdateTotalCost = dialogUpdate.findViewById(R.id.edt_update_total_cost);
        edtUpdateTimestamp = dialogUpdate.findViewById(R.id.edt_update_timestamp);
        btnCancelUpdate = dialogUpdate.findViewById(R.id.btn_cancel_update);
        btnUpdate = dialogUpdate.findViewById(R.id.btn_update);

        edtUpdateSeller.setText(invoice.getSeller());
        edtUpdateAddress.setText(invoice.getAddress());
        edtUpdateTotalCost.setText(invoice.getTotalCost()+"");
        edtUpdateTimestamp.setText(invoice.getTimestamp());

        btnCancelUpdate.setOnClickListener(v -> dialogUpdate.dismiss());

        btnUpdate.setOnClickListener(v -> {
            updateItem(invoice, position);
        });

        dialogUpdate.show();
    }

    private void getTotalUserOnFb(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();
        myRef.child("totalUser").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                setTotalUser( Long.parseLong(String.valueOf(task.getResult().getValue())));
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
                initRecycleView();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateItem(Invoice invoice, int postion){

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();
        myRef.child("totalUser").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                setTotalUser(Long.parseLong(String.valueOf(task.getResult().getValue())));
                getIdUserCurrentToUpdate(invoice, postion);
            }
        });
    }
    private void getIdUserCurrentToUpdate(Invoice invoice, int postion){
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
                        Log.e("Test total user ",getTotalUser()+"");
                        Log.e("Test index user ",getIndexUserCurrent()+"");
                        updateItemInList(invoice, postion);
                    }

                }
            });
        }
    }

    private void updateItemInList(Invoice invoice, int postion){


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference(getIndexUserCurrent()+"/invoices/"+postion);

        myRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
//                invoice.setId(postion);
                InvoiceModel invoiceModel = new InvoiceModel();
                invoiceModel.setId(postion);
                invoiceModel.setSeller(edtUpdateSeller.getText().toString().trim());
                invoiceModel.setAddress(edtUpdateAddress.getText().toString().trim());
                invoiceModel.setTotalCost(Float.parseFloat(edtUpdateTotalCost.getText().toString().trim()));
                invoiceModel.setTimestamp(edtUpdateTimestamp.getText().toString().trim());
                myRef.setValue(invoiceModel);

                Toast.makeText(getActivity(), "Update Successed", Toast.LENGTH_LONG);
                dialogUpdate.dismiss();
                getTotalUserOnFb();
            }
        });
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


    private void processingData() {
        SharedPreferences sharedpreferences;
        int userId;
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getActivity());
        invoiceList.clear();

        sharedpreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // getting data from shared prefs and
        // storing it in our string variable.
        userId = sharedpreferences.getInt(ID_KEY, -1);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_INVOICE_BY_USER_ID+userId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson gson = new Gson();
                invoiceList = Arrays.asList(gson.fromJson(response, Invoice[].class));

                initRecycleView();

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
}