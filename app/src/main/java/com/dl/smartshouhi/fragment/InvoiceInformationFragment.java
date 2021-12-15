package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.animation.Animator;
import android.app.ProgressDialog;
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

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activities.HomeActivity;
import com.dl.smartshouhi.api.ApiService;
import com.dl.smartshouhi.model.Invoice;
import com.dl.smartshouhi.utils.RealPathUtil;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_invoice_information, container, false);

        initUI();
        initListener();

        // Retrieve and cache the system's default "short" animation time.
//        shortAnimationDuration = getResources().getInteger(
//                android.R.integer.config_shortAnimTime);

        return mView;
    }



    private void initUI() {
        homeActivity = (HomeActivity) getActivity();
        mProressDialog = new ProgressDialog(getActivity());
        mProressDialog.setMessage("Please wait ...");

        edtSeller = mView.findViewById(R.id.edt_seller);
        edtAddress = mView.findViewById(R.id.edt_address);
        edtTimestamp = mView.findViewById(R.id.edt_timestamp);
        edtTotalCost = mView.findViewById(R.id.edt_total_cost);
//        imgButtonZoom = mView.findViewById(R.id.img_button_zoom);


        String currentDate1 = DateFormat.format("dd/MM/yyyy", new Date()).toString();

        edtTimestamp.setText(currentDate1);

        imgFromGallery = mView.findViewById(R.id.img_from_gallery);

        btnGetIn4 = mView.findViewById(R.id.btn_get_in4);
        btnSelectImage = mView.findViewById(R.id.btn_select_image);
        imgButtonCalendar = mView.findViewById(R.id.btn_calendar);


    }

    private void initListener() {
        btnSelectImage.setOnClickListener(v -> onClickRequestPermission());

//        imgButtonZoom.setOnClickListener(v -> {
//                zoomImageFromThumb(mView);
////            ZoomImage zoomImage = new ZoomImage(mView);
////            int idImageFromGallery = getResources().getIdentifier(mUri.getPath(), "drawable", getActivity().getPackageName());
////            zoomImage.zoomImageFromThumb(imgButtonZoom, mView, R.id.expanded_image, idImageFromGallery);
//        });

        btnGetIn4.setOnClickListener(v -> CallApi());
        imgButtonCalendar.setOnClickListener(v -> displayCalendar());

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

        String strRealPath = RealPathUtil.getRealPath(getActivity(), mUri);
        Log.e("DuynDuyn", strRealPath);
        File file = new File(strRealPath);
        RequestBody requestBodyImg = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipartBodyImg = MultipartBody.Part.createFormData("image", file.getName(), requestBodyImg);

        ApiService.apiService.getInformationInvoice2(multipartBodyImg).enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {


                Invoice invoice = response.body();
                if(invoice != null){
                    mProressDialog.dismiss();
                    edtSeller.setText(invoice.getSeller());
                    edtAddress.setText(invoice.getAddress());
                    edtTimestamp.setText(invoice.getTimestamp());
                    edtTotalCost.setText(invoice.getTotalCost()+"");
                }
            }

            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                mProressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(getActivity(), "Call Api False", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCalendar() {
        final View dialogView = View.inflate(getActivity(), R.layout.layout_date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {

            DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);

            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                    datePicker.getMonth(),
                    datePicker.getDayOfMonth());

            String currentDate = DateFormat.format("dd/MM/yyyy", calendar).toString();
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
