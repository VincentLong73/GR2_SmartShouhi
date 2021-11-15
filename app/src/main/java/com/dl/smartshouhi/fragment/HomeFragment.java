package com.dl.smartshouhi.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.adapter.InvoiceAdapter;
import com.dl.smartshouhi.model.Invoice;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private EditText edtSeller, edtAddress, edtTimestamp, edtTotalCost;
    private TextView tvTotalInvoices;
    private Button btnSendData;

    private View mView;
    private RecyclerView rcvInvoices;
    private InvoiceAdapter invoiceAdapter;

    private List<Invoice> invoiceList;

    private int totalInvoices = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_home, container, false);
        initUI();
        initListener();
        getListInvoiceDatabase();

        return mView;
    }

    private void initUI() {
        edtSeller = mView.findViewById(R.id.edt_seller);
        edtAddress = mView.findViewById(R.id.edt_address);
        edtTimestamp = mView.findViewById(R.id.edt_timestamp);
        edtTotalCost = mView.findViewById(R.id.edt_total_cost);
        tvTotalInvoices = mView.findViewById(R.id.tv_total_invoice);
        btnSendData = mView.findViewById(R.id.btn_send_data);
        rcvInvoices = mView.findViewById(R.id.rcv_invoices);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvInvoices.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        rcvInvoices.addItemDecoration(dividerItemDecoration);

        invoiceList = new ArrayList<>();
        invoiceAdapter = new InvoiceAdapter(invoiceList, new InvoiceAdapter.IClickListener() {
            @Override
            public void onClickUpdateItem(Invoice invoice) {
                openDialogUpdateItem(invoice);
            }
        });
        rcvInvoices.setAdapter(invoiceAdapter);
    }

    private void initListener() {
        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seller, address, timestamp, totalCost;
                seller = edtSeller.getText().toString().trim();
                address = edtAddress.getText().toString().trim();
                timestamp = edtTimestamp.getText().toString().trim();
                totalCost = edtTotalCost.getText().toString().trim();

                Invoice invoice = new Invoice(seller, address, timestamp, totalCost);
                onClickSendData(invoice);
            }
        });
    }

    private void onClickSendData(Invoice invoice) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://modulelogin-253a6-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("users/1/invoices");



        String pathObject = "invoice-1";

        myRef.child(pathObject).setValue(invoice, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getActivity(), "Send data success", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getListInvoiceDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://modulelogin-253a6-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("users/0/invoices");

//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(invoiceList != null){
//                    invoiceList.clear();
//                }
//
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Invoice invoice = dataSnapshot.getValue(Invoice.class);
//                    invoiceList.add(invoice);
//                }
//
//                invoiceAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), "Get list invoices failed", Toast.LENGTH_LONG).show();
//            }
//        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Invoice invoice = snapshot.getValue(Invoice.class);
                    if(invoice != null){
                        invoiceList.add(invoice);
                        invoiceAdapter.notifyDataSetChanged();
                    }
                }catch (DatabaseException e){
                    totalInvoices = snapshot.getValue(Integer.class);
                    tvTotalInvoices.setText(totalInvoices+"");
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openDialogUpdateItem(Invoice invoice){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_update_item_invoice);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        EditText edtUpdateSeller = dialog.findViewById(R.id.edt_update_seller);
        Button btnCancelUpdate = dialog.findViewById(R.id.btn_cancel_update);
        Button btnUpdateSeller = dialog.findViewById(R.id.btn_update_seller);

        edtUpdateSeller.setText(invoice.getSeller());

        btnCancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnUpdateSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://modulelogin-253a6-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference myRef = database.getReference("users/1/invoices");

                String pathObject = "invoice-1";

                String seller = edtUpdateSeller.getText().toString().trim();
                invoice.setSeller(seller);
                myRef.child(pathObject).updateChildren(invoice.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getActivity(), "Update data success", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }
}
