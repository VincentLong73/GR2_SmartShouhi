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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.adapter.InvoiceAdapter;
import com.dl.smartshouhi.api.InvoiceDbApi;
import com.dl.smartshouhi.model.Invoice;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dl.smartshouhi.constaint.Constant.ID_KEY;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS_API;

public class HistoryFragment extends Fragment {

    private View mView;
    private RecyclerView rcvInvoices;

    private EditText edtUpdateSeller, edtUpdateAddress, edtUpdateTotalCost, edtUpdateTimestamp;
    private Dialog dialogUpdate;

    private Dialog dialogDelete;

    private List<Invoice> invoiceList;
    private SharedPreferences sharedPreferences ;

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


        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

    }

    private void initRecycleView(){

        InvoiceAdapter invoiceAdapter = new InvoiceAdapter(invoiceList, new InvoiceAdapter.IClickListener() {
            @Override
            public void onClickUpdateItem(Invoice invoice, int position) {
                openDialogUpdateItem(invoice, position);
            }

            @Override
            public void onClickDeleteItem(Invoice invoice, int position) {
                openDialogDeleteItem(invoice, position);
            }
        });
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
        Button btnCancelUpdate = dialogUpdate.findViewById(R.id.btn_cancel_update);
        Button btnUpdate = dialogUpdate.findViewById(R.id.btn_update);
        ImageButton imgButtonCalendar = dialogUpdate.findViewById(R.id.btn_calendar_invoice);

        edtUpdateSeller.setText(invoice.getSeller());
        edtUpdateAddress.setText(invoice.getAddress());
        edtUpdateTotalCost.setText(invoice.getTotalCost()+"");
        edtUpdateTimestamp.setText(invoice.getTimestamp());

        btnCancelUpdate.setOnClickListener(v -> dialogUpdate.dismiss());

        btnUpdate.setOnClickListener(v -> updateItem(invoice));

        imgButtonCalendar.setOnClickListener(v -> displayCalendar());

        dialogUpdate.show();
    }

    private void openDialogDeleteItem(Invoice invoice, int position){
        dialogDelete = new Dialog(getActivity());
        dialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDelete.setContentView(R.layout.layout_dialog_delete_item_invoice);
        Window window = dialogDelete.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDelete.setCancelable(false);

        Button btnCancelDelete = dialogDelete.findViewById(R.id.btn_cancel_delete);
        Button btnDelete = dialogDelete.findViewById(R.id.btn_delete);


        btnCancelDelete.setOnClickListener(v -> dialogDelete.dismiss());

        btnDelete.setOnClickListener(v -> {
            deleteItem(invoice);
        });

        dialogDelete.show();
    }

    private void updateItem(Invoice invoice){

        String seller = edtUpdateSeller.getText().toString().trim();
        String address = edtUpdateAddress.getText().toString().trim();
        String timestamp = edtUpdateTimestamp.getText().toString().trim();
        String totalCost = edtUpdateTotalCost.getText().toString().trim();
        int id = invoice.getId();

        InvoiceDbApi.databaseApi.updateInvoice(id, seller, address, totalCost, timestamp).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String strResult;
                try {
                    strResult = response.body().string();
                    if(strResult.length() > 1){

                        String[] listResult = strResult.split("#");
                        if(listResult[0].equals("200")) {
                            Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                            dialogUpdate.dismiss();
                            processingData();
                        }else {
                            Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_LONG).show();
                            dialogUpdate.dismiss();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private void deleteItem(Invoice invoice){

        InvoiceDbApi.databaseApi.deleteInvoice(invoice.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String strResult;
                try {
                    strResult = response.body().string();
                    if(strResult.length() > 1){

                        String[] listResult = strResult.split("#");
                        if(listResult[0].equals("200")) {
                            Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                            dialogDelete.dismiss();
                            processingData();
                        }else {
                            Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_LONG).show();
                            dialogDelete.dismiss();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    private void processingData() {
        int userId;
        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        userId = sharedPreferences.getInt(ID_KEY, -1);

        InvoiceDbApi.databaseApi.getListInvoice(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String strResult;
                try {
                    strResult = response.body().string();
                    if(strResult.length() > 1){
                        strResult = strResult.replace("\\", "");
                        strResult = strResult.replace("\"{", "{");
                        strResult = strResult.replace("}\"", "}");
                        strResult = strResult.substring(1, strResult.length() - 1);

                        Gson gson = new Gson();

                        String[] listResult = strResult.split("#");
                        if(listResult[0].equals("200")) {
                            invoiceList = Arrays.asList(gson.fromJson(listResult[1], Invoice[].class));
                            initRecycleView();
                        }else {
                            Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

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