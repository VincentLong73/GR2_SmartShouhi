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
import android.util.Log;
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
import com.dl.smartshouhi.api.ApiService;
import com.dl.smartshouhi.api.InvoiceDbApi;
import com.dl.smartshouhi.api.ItemDbApi;
import com.dl.smartshouhi.model.InvoiceItemModel;
import com.dl.smartshouhi.model.ItemModel;
import com.dl.smartshouhi.util.RealPathUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dl.smartshouhi.constaint.Constant.ID_KEY;
import static com.dl.smartshouhi.constaint.Constant.INVOICE_ITEMS;
import static com.dl.smartshouhi.constaint.Constant.SHARED_PREFS;
import static com.dl.smartshouhi.constaint.Constant.URL_ADD_A_INVOICE;
import static com.dl.smartshouhi.constaint.Constant.URL_ADD_LIST_ITEM;

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
    private ProgressDialog mProcessDialog;

    private Dialog dialogInvoice;
    private Button btnSaveInvoice;
    private Button btnCancelInvoice;
    private EditText edtItemName;
    private EditText edtItemCost;

    private SharedPreferences sharedpreferences;
    private int invoiceId;

    private RecyclerView rcvItems;
    private List<ItemModel> itemList;
    private Dialog dialogUpdateItem;
    private Dialog dialogDeleteItem;
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
        mProcessDialog = new ProgressDialog(getActivity());
        mProcessDialog.setMessage("Please wait ...");



        imgFromGallery = mView.findViewById(R.id.img_from_gallery);

        btnGetIn4 = mView.findViewById(R.id.btn_get_in4);
        btnSelectImage = mView.findViewById(R.id.btn_select_image);
        btnSaveListItem = mView.findViewById(R.id.btn_save_list_item);


        /*S- Bat dau khoi tao UI trong Dialog Update */


        dialogInvoice = new Dialog(getActivity());
        dialogInvoice.setCancelable(true);
        View viewDialogInvoice = homeActivity.getLayoutInflater().inflate(R.layout.layout_dialog_add_infor_invoice, null);
        dialogInvoice.setContentView(viewDialogInvoice);
        edtSeller = viewDialogInvoice.findViewById(R.id.edt_seller);
        edtAddress = viewDialogInvoice.findViewById(R.id.edt_address);
        edtTimestamp = viewDialogInvoice.findViewById(R.id.edt_timestamp);
        edtTotalCost = viewDialogInvoice.findViewById(R.id.edt_total_cost);
        btnSaveInvoice = viewDialogInvoice.findViewById(R.id.btn_save_invoice);
        btnCancelInvoice = viewDialogInvoice.findViewById(R.id.btn_cancel_invoice);
        imgButtonCalendar = viewDialogInvoice.findViewById(R.id.btn_calendar_invoice);


        rcvItems = mView.findViewById(R.id.rcv_items);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvItems.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        rcvItems.addItemDecoration(dividerItemDecoration);

        /*E- Ket thuc khoi tao UI trong Dialog Update */

        sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

    }

    private void initRecycleView(){
        sharedpreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String invoice_items = sharedpreferences.getString(INVOICE_ITEMS, "");
        Gson gson = new Gson();
        InvoiceItemModel invoiceItemModel = gson.fromJson(invoice_items, InvoiceItemModel.class);
        itemList = new ArrayList<>();
        itemList = invoiceItemModel.getItem();
        ItemAdapter itemAdapter = new ItemAdapter(itemList, new ItemAdapter.IClickListener() {
            @Override
            public void onClickUpdateItem(ItemModel item, int position) {
                openDialogUpdateItem(item, position);
            }

            @Override
            public void onClickDeleteItem(ItemModel item, int position) {
                openDialogDeleteItem(item, position);
            }
        });
        rcvItems.setAdapter(itemAdapter);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(INVOICE_ITEMS);
        editor.apply();
    }

    private void openDialogUpdateItem(ItemModel item, int position){

        dialogUpdateItem = new Dialog(getActivity());
        dialogUpdateItem.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdateItem.setContentView(R.layout.layout_dialog_add_item);
        Window window = dialogUpdateItem.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogUpdateItem.setCancelable(false);


        edtItemName = dialogUpdateItem.findViewById(R.id.edt_item_name);
        edtItemCost = dialogUpdateItem.findViewById(R.id.edt_item_cost);

        Button btnSaveItem = dialogUpdateItem.findViewById(R.id.btn_save_item);

        edtItemName.setText(item.getItem_name());
        edtItemCost.setText(item.getCost_item()+"");

        btnSaveItem.setOnClickListener(v -> addItem(position));

        dialogUpdateItem.show();
    }

    private void openDialogDeleteItem(ItemModel item, int position){

        dialogDeleteItem = new Dialog(getActivity());
        dialogDeleteItem.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDeleteItem.setContentView(R.layout.layout_dialog_delete_item);
        Window window = dialogDeleteItem.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogDeleteItem.setCancelable(false);

        Button btnCancelDelete = dialogDeleteItem.findViewById(R.id.btn_cancel_delete);
        Button btnDelete = dialogDeleteItem.findViewById(R.id.btn_delete);


        btnCancelDelete.setOnClickListener(v -> dialogDeleteItem.dismiss());
        btnDelete.setOnClickListener(v -> deleteItem(position));

        dialogDeleteItem.show();
    }

    private void initListener() {
        btnSelectImage.setOnClickListener(v -> onClickRequestPermission());

        btnGetIn4.setOnClickListener(v -> CallApi());
        imgButtonCalendar.setOnClickListener(v -> displayCalendar());
        btnSaveInvoice.setOnClickListener(v -> onClickSaveInvoice());
        btnCancelInvoice.setOnClickListener(v -> dialogInvoice.dismiss());
        btnSaveListItem.setOnClickListener(v -> onClickSaveListItem());

    }
    private void onClickSaveListItem() {
        Gson gson = new Gson();
        String strListItem = gson.toJson(
                itemList,
                new TypeToken<ArrayList<ItemModel>>() {}.getType());

        ItemDbApi.databaseApi.addItem(invoiceId, strListItem).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String result = response.body().string();
                    String[] listResult = result.split("#");
                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Error 500", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onClickSaveInvoice(){

        String seller = edtSeller.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String timestamp = edtTimestamp.getText().toString().trim();
        String totalCost = edtTotalCost.getText().toString().trim();
        int uid = sharedpreferences.getInt(ID_KEY, 0);

        InvoiceDbApi.databaseApi.addInvoice(uid, seller, address, totalCost, timestamp).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String strResult;
                try {
                    strResult = response.body().string();
                    if(strResult.length() > 1){

                        String[] listResult = strResult.split("#");
                        if(listResult[0].equals("200")) {
                            invoiceId = Integer.parseInt(listResult[1].trim());
                            Toast.makeText(getActivity(), "Lưu hóa đơn thành công", Toast.LENGTH_SHORT).show();
                            dialogInvoice.dismiss();
                            initRecycleView();
                        }else {
                            Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                            dialogInvoice.dismiss();
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

        mProcessDialog.show();

        String strRealPath = RealPathUtil.getRealPath(getActivity(), mUri);
        Log.e("DuynDuyn", strRealPath);
        File file = new File(strRealPath);
        RequestBody requestBodyImg = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipartBodyImg = MultipartBody.Part.createFormData("image", file.getName(), requestBodyImg);

        ApiService.apiService.getInformationInvoice2(multipartBodyImg).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null){
                    String strResult = response.body().toString();
                    strResult = strResult.replace("\\", "");
                    strResult = strResult.replace("\"{", "{");
                    strResult = strResult.replace("}\"", "}");

                    Gson gson = new Gson();

                    InvoiceItemModel invoiceItemModel = gson.fromJson(strResult, InvoiceItemModel.class);
                    if(invoiceItemModel != null){
                        try {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(INVOICE_ITEMS, strResult);
                            // to save our data with key and value.
                            editor.apply();
                            mProcessDialog.dismiss();
                            Toast.makeText(getActivity(), "Trích xuất thông tin thành công", Toast.LENGTH_SHORT).show();
                            showDialogInvoice(invoiceItemModel);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else {
                        mProcessDialog.dismiss();
                        Toast.makeText(getActivity(), "Trích xuất thông tin lỗi", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    mProcessDialog.dismiss();
                    Toast.makeText(getActivity(), "Trích xuất thông tin lỗi", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProcessDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(getActivity(), "Trích xuất thông tin lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItem(Integer position){

        String itemName = edtItemName.getText().toString().trim();
        String itemCost = edtItemCost.getText().toString().trim();

        itemList.get(position).setCost_item(Float.parseFloat(itemCost));
        itemList.get(position).setItem_name(itemName);
        dialogUpdateItem.dismiss();

    }

    private void deleteItem(Integer position){
        itemList.remove(position);
        dialogDeleteItem.dismiss();

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
