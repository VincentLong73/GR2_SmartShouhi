package com.dl.smartshouhi.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.dl.smartshouhi.model.InvoiceModel;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dl.smartshouhi.constaint.Constant.ID_KEY;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constant.URL_GET_INVOICE_BY_USER_ID;
import static com.dl.smartshouhi.constaint.Constant.URL_UPDATE_A_INVOICE;

public class HistoryFragment extends Fragment {

    private View mView;
    private RecyclerView rcvInvoices;

    private EditText edtUpdateSeller, edtUpdateAddress, edtUpdateTotalCost, edtUpdateTimestamp;
    private Button btnCancelUpdate;
    private Dialog dialogUpdate;
    private ImageButton imgButtonCalendar;

    private List<InvoiceModel> invoiceList;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_history, container, false);

        initUI();
        processingData();

        return mView;
    }


    private void initUI() {

        rcvInvoices = mView.findViewById(R.id.rcv_invoices);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvInvoices.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        rcvInvoices.addItemDecoration(dividerItemDecoration);

        requestQueue = Volley.newRequestQueue(getActivity());

    }

    private void initRecycleView(){
        InvoiceAdapter invoiceAdapter = new InvoiceAdapter(invoiceList, (invoice, position) -> openDialogUpdateItem(invoice, position));
        rcvInvoices.setAdapter(invoiceAdapter);
    }

    private void openDialogUpdateItem(InvoiceModel invoice, int position){
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
        Button btnUpdate = dialogUpdate.findViewById(R.id.btn_update);
        imgButtonCalendar = dialogUpdate.findViewById(R.id.btn_calendar_invoice);

        edtUpdateSeller.setText(invoice.getSeller());
        edtUpdateAddress.setText(invoice.getAddress());
        edtUpdateTotalCost.setText(invoice.getTotalCost()+"");
        edtUpdateTimestamp.setText(invoice.getTimestamp());

        btnCancelUpdate.setOnClickListener(v -> dialogUpdate.dismiss());

        btnUpdate.setOnClickListener(v -> {
            updateItem(invoice, position);
        });

        imgButtonCalendar.setOnClickListener(v -> displayCalendar());

        dialogUpdate.show();
    }

    private void updateItem(InvoiceModel invoice, int postion){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_A_INVOICE, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.substring(1, response.length()-1);
                String[] listResult = response.split("#");
                if(listResult[0].equals("200")){

                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                    dialogUpdate.dismiss();
                    processingData();
                }else {
                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                    dialogUpdate.dismiss();
                }

            }
        }, error -> {
            Toast.makeText(getActivity(), "Error 500", Toast.LENGTH_SHORT).show();
            dialogUpdate.dismiss();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                String seller = edtUpdateSeller.getText().toString().trim();
                String address = edtUpdateAddress.getText().toString().trim();
                String timestamp = edtUpdateTimestamp.getText().toString().trim();
                String totalCost = edtUpdateTotalCost.getText().toString().trim();
                int id = invoice.getId();

                params.put("seller", seller);
                params.put("address", address);
                params.put("timestamp", timestamp);
                params.put("totalcost", totalCost);
                params.put("invoiceId", String.valueOf(id));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }



    private void processingData() {
        SharedPreferences sharedpreferences;
        int userId;
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getActivity());
//        invoiceList.clear();

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

//                String result = gson.fromJson(response, String.class);
                String[] listResult = response.split("#");
                if(listResult[0].equals("200")) {
                    invoiceList = Arrays.asList(gson.fromJson(listResult[1], InvoiceModel[].class));
                    initRecycleView();
                }else {
                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_LONG);
                }


            }
        }, error -> {
            Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    private void displayCalendar() {
        final View dialogView = View.inflate(getActivity(), R.layout.layout_date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {

            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);

            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                    datePicker.getMonth(),
                    datePicker.getDayOfMonth());
            String currentDate = DateFormat.format("yyyy-MM-dd", calendar).toString();
            edtUpdateTimestamp.setText(currentDate);

            alertDialog.dismiss();
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
}