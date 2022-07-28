package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activity.HomeActivity;
import com.dl.smartshouhi.adapter.ItemAdapter;
import com.dl.smartshouhi.model.InvoiceItemModel;
import com.dl.smartshouhi.model.ItemTest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dl.smartshouhi.constaint.Constant.ID_KEY;
import static com.dl.smartshouhi.constaint.Constant.INVOICE_ITEMS;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constant.URL_ADD_A_INVOICE;
import static com.dl.smartshouhi.constaint.Constant.URL_ADD_LIST_ITEM;
import static com.dl.smartshouhi.constaint.Constant.URL_INFO_A_INVOICE;

public class InvoiceInformationFragment extends Fragment {

    private HomeActivity homeActivity;
    private View mView;

    private static final int MY_REQUEST_CODE = 7;

    private EditText edtSeller;
    private EditText edtAddress;
    private EditText edtTimestamp;
    private EditText edtTotalCost;
    private ImageButton imgButtonCalendar;

    private ImageView imgFromGallery;

    private Button btnGetIn4, btnSelectImage;

    private Uri mUri;
    private ProgressDialog mProressDialog;

    private Dialog dialogInvoice;
    private View viewDialogInvoice;
    private Button btnSaveItem;
    private Button btnSaveInvoice;
    private Button btnCancleInvoice;
    private EditText edtItemName;
    private EditText edtItemCost;

    private RequestQueue requestQueue;
    private SharedPreferences sharedpreferences;
    private int invoiceId;

    private RecyclerView rcvItems;
    private ItemAdapter itemAdapter;
    private List<ItemTest> itemList;
    private Dialog dialogItem;
    private Button btnSaveListItem;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_invoice_information, container, false);

        initUI();
        initListener();

        return mView;
    }



    private void initUI() {
        homeActivity = (HomeActivity) getActivity();
        mProressDialog = new ProgressDialog(getActivity());
        mProressDialog.setMessage("Please wait ...");



        imgFromGallery = mView.findViewById(R.id.img_from_gallery);

        btnGetIn4 = mView.findViewById(R.id.btn_get_in4);
        btnSelectImage = mView.findViewById(R.id.btn_select_image);
        btnSaveListItem = mView.findViewById(R.id.btn_save_list_item);


        /*S- Bat dau khoi tao UI trong Dialog Update */


        dialogInvoice = new Dialog(getActivity());
        dialogInvoice.setCancelable(true);
        viewDialogInvoice = homeActivity.getLayoutInflater().inflate(R.layout.layout_dialog_add_infor_invoice, null);
        dialogInvoice.setContentView(viewDialogInvoice);
        edtSeller = viewDialogInvoice.findViewById(R.id.edt_seller);
        edtAddress = viewDialogInvoice.findViewById(R.id.edt_address);
        edtTimestamp = viewDialogInvoice.findViewById(R.id.edt_timestamp);
        edtTotalCost = viewDialogInvoice.findViewById(R.id.edt_total_cost);
        btnSaveInvoice = viewDialogInvoice.findViewById(R.id.btn_save_invoice);
        btnCancleInvoice = viewDialogInvoice.findViewById(R.id.btn_cancel_invoice);
        imgButtonCalendar = viewDialogInvoice.findViewById(R.id.btn_calendar_invoice);


        rcvItems = mView.findViewById(R.id.rcv_items);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvItems.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        rcvItems.addItemDecoration(dividerItemDecoration);

        /*E- Ket thuc khoi tao UI trong Dialog Update */

        dialogItem = new Dialog(getActivity());
        dialogItem.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogItem.setContentView(R.layout.layout_dialog_add_item);
        Window window = dialogItem.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogItem.setCancelable(false);

        requestQueue = Volley.newRequestQueue(getActivity());
        sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

    }

    private void initRecycleView(){
        sharedpreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String invoice_items = sharedpreferences.getString(INVOICE_ITEMS, "");
        Gson gson = new Gson();
        InvoiceItemModel invoiceItemModel = gson.fromJson(invoice_items, InvoiceItemModel.class);
        itemList = new ArrayList<>();
        itemList = invoiceItemModel.getItem();
        itemAdapter = new ItemAdapter(itemList, (item, position) -> openDialogItem(item, position));
        rcvItems.setAdapter(itemAdapter);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(INVOICE_ITEMS);
        editor.commit();
    }

    private void openDialogItem(ItemTest item, int position){

        edtItemName = dialogItem.findViewById(R.id.edt_item_name);
        edtItemCost = dialogItem.findViewById(R.id.edt_item_cost);

        btnSaveItem = dialogItem.findViewById(R.id.btn_save_item);

        edtItemName.setText(item.getItem_name());
        edtItemCost.setText(item.getCost_item()+"");

        btnSaveItem.setOnClickListener(v -> {
            addItem(position);
        });

        dialogItem.show();
    }

    private void initListener() {
        btnSelectImage.setOnClickListener(v -> onClickRequestPermission());

        btnGetIn4.setOnClickListener(v -> CallApi());
        imgButtonCalendar.setOnClickListener(v -> displayCalendar());
        btnSaveInvoice.setOnClickListener(v -> onClickSaveInvoice());
        btnCancleInvoice.setOnClickListener(v -> dialogInvoice.dismiss());
        btnSaveListItem.setOnClickListener(v -> {
            onClickSaveListItem();
        });

    }
    private void onClickSaveListItem() {
        Gson gson = new Gson();
        String strListItem = gson.toJson(
                itemList,
                new TypeToken<ArrayList<ItemTest>>() {}.getType());
//        Toast.makeText(getActivity(), strListItem, Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_LIST_ITEM, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.substring(1, response.length()-1);
                String[] listResult = response.split("#");
                Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
//                if(listResult[0].equals("200")){
//
//                    Toast.makeText(getActivity(), "Add Item Successfully :", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getActivity(), "Add Item  failed.", Toast.LENGTH_SHORT).show();
//                }

            }
        }, error -> {
            Toast.makeText(getActivity(), "Error 500", Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("invoiceId", String.valueOf(invoiceId));
                params.put("listItem", strListItem);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
    private void onClickSaveInvoice(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_A_INVOICE, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.substring(1, response.length()-1);
                Gson gson = new Gson();
                String[] listResult = response.split("#");
                if(listResult[0].equals("200")){

                    invoiceId = Integer.parseInt(listResult[1].trim());

                    Toast.makeText(getActivity(), "Add Invoice Successfully" + invoiceId, Toast.LENGTH_SHORT).show();
                    dialogInvoice.dismiss();
                    initRecycleView();
                }else {
                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                    dialogInvoice.dismiss();
                }

            }
        }, error -> {
            Toast.makeText(getActivity(), "Error 500", Toast.LENGTH_SHORT).show();
            dialogInvoice.dismiss();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                String seller = edtSeller.getText().toString().trim();
                String address = edtAddress.getText().toString().trim();
                String timestamp = edtTimestamp.getText().toString().trim();
                String totalCost = edtTotalCost.getText().toString().trim();
                int uid = sharedpreferences.getInt(ID_KEY, 0);

                params.put("seller", seller);
                params.put("address", address);
                params.put("timestamp", timestamp);
                params.put("totalcost", totalCost);
                params.put("uid", String.valueOf(uid));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void onClickRequestPermission() {

        if(homeActivity == null){
            return;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            homeActivity.openGallery();
            return;
        }

        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            homeActivity.openGallery();
        }else{
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, MY_REQUEST_CODE);
        }
    }


    private void CallApi() {

        mProressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_INFO_A_INVOICE, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.substring(1, response.length()-1);
                Gson gson = new Gson();
                String[] listResult = response.split("#");
                if(listResult[0].equals("200")){
                    InvoiceItemModel invoiceItemModel = gson.fromJson(listResult[1], InvoiceItemModel.class);
                    try {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(INVOICE_ITEMS, response);
                        // to save our data with key and value.
                        editor.apply();
                        Toast.makeText(getActivity(), "Get Invoice Successfully", Toast.LENGTH_SHORT).show();
                        mProressDialog.dismiss();
                        showDialogInvoice(invoiceItemModel);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }



                }else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    mProressDialog.dismiss();
                }

            }
        }, error -> {
            Toast.makeText(getActivity(), "Error 500", Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String, String> getParams() {

                return null;
            }
        };

            requestQueue.add(stringRequest);

//        String strRealPath = RealPathUtil.getRealPath(getActivity(), mUri);
//        Log.e("DuynDuyn", strRealPath);
//        File file = new File(strRealPath);
//        RequestBody requestBodyImg = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part multipartBodyImg = MultipartBody.Part.createFormData("image", file.getName(), requestBodyImg);

//        ApiService.apiService.getInformationInvoice().enqueue(new Callback<InvoiceItemModel>() {
//            @Override
//            public void onResponse(Call<InvoiceItemModel> call, Response<InvoiceItemModel> response) {
//
//                InvoiceItemModel invoiceItemModel = response.body();
//
//                if(invoiceItemModel != null){
//                    Toast.makeText(getActivity(), "Call Api Successfully", Toast.LENGTH_SHORT).show();
//                }

//                Invoice invoice = response.body();
//                if(invoice != null){
//                    mProressDialog.dismiss();
//                    edtSeller.setText(invoice.getSeller());
//                    edtAddress.setText(invoice.getAddress());
//                    edtTimestamp.setText(invoice.getTimestamp());
//                    edtTotalCost.setText(invoice.getTotalCost()+"");
//                }


//            }
//
//            @Override
//            public void onFailure(Call<InvoiceItemModel> call, Throwable t) {
//                mProressDialog.dismiss();
//                t.printStackTrace();
//                Toast.makeText(getActivity(), "Call Api False", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void addItem(Integer position){

        String itemName = edtItemName.getText().toString().trim();
        String itemCost = edtItemCost.getText().toString().trim();

        itemList.get(position).setCost_item(Float.parseFloat(itemCost));
        itemList.get(position).setItem_name(itemName);
        dialogItem.dismiss();

    }

    private void showDialogInvoice(InvoiceItemModel invoiceItemModel) throws ParseException {
        String seller = invoiceItemModel.getInvoice().getSeller();
        String address = invoiceItemModel.getInvoice().getAddress();
        String timestampTmp = invoiceItemModel.getInvoice().getTimestamp();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String timestamp =  sdf2.format(sdf.parse(timestampTmp));
        String totalCost = invoiceItemModel.getInvoice().getTotal_cost();

        edtSeller.setText(seller);
        edtAddress.setText(address);
        edtTimestamp.setText(timestamp);
        edtTotalCost.setText(totalCost);
        dialogInvoice.show();

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
            edtTimestamp.setText(currentDate);

            alertDialog.dismiss();
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    public void setBitmapImageView(Bitmap bitmapImageView){
        imgFromGallery.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

}
