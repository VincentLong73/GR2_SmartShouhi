package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activities.MainActivity;
import com.dl.smartshouhi.api.ApiService;
import com.dl.smartshouhi.model.Invoice;
import com.dl.smartshouhi.utils.RealPathUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceInformationFragment extends Fragment {

    private MainActivity mainActivity;

    private View mView;

    private static final int MY_REQUEST_CODE = 7;

    private TextView tvSeller;
    private TextView tvAddress;
    private TextView tvTimestamp;
    private TextView tvTotalCost;

    private ImageView imgFromGallery;

    private Button btnGetIn4, btnSelectImage;

    private Uri mUri;
    private ProgressDialog mProressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_invoice_information, container, false);

        initUI();
        mainActivity = (MainActivity) getActivity();
        mProressDialog = new ProgressDialog(getActivity());
        mProressDialog.setMessage("Please wait ...");

        btnSelectImage.setOnClickListener(v -> onClickRequestPermission());

        btnGetIn4.setOnClickListener(v -> CallApi());

        return mView;
    }

    private void initUI() {
        tvSeller = mView.findViewById(R.id.seller);
        tvAddress = mView.findViewById(R.id.address);
        tvTimestamp = mView.findViewById(R.id.timestamp);
        tvTotalCost = mView.findViewById(R.id.total_cost);

        imgFromGallery = mView.findViewById(R.id.img_from_gallery);

        btnGetIn4 = mView.findViewById(R.id.btn_get_in4);
        btnSelectImage = mView.findViewById(R.id.btn_select_image);
    }

    private void onClickRequestPermission() {

        if(mainActivity == null){
            return;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            mainActivity.openGallery();
            return;
        }

        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            mainActivity.openGallery();
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
                    tvSeller.setText(invoice.getSeller());
                    tvAddress.setText(invoice.getAddress());
                    tvTimestamp.setText(invoice.getTimestamp());
                    tvTotalCost.setText(invoice.getTotalCost());
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

    public void setBitmapImageView(Bitmap bitmapImageView){
        imgFromGallery.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }
}
