package com.dl.smartshouhi.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.api.ApiService;
import com.dl.smartshouhi.model.Invoice;
import com.dl.smartshouhi.utils.RealPathUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceInformationActivity extends AppCompatActivity {

    public static final String TAG = InvoiceInformationActivity.class.getName();

    private static final int MY_REQUEST_CODE = 7;

    private TextView tvSeller;
    private TextView tvAddress;
    private TextView tvTimestamp;
    private TextView tvTotalCost;

    private ImageView imgFromGallery;

    private Button btnGetIn4, btnSelectImage;

    private Uri mUri;
    private ProgressDialog mProressDialog;

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data == null){
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgFromGallery.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_infor);

        initUI();
        mProressDialog = new ProgressDialog(this);
        mProressDialog.setMessage("Please wait ...");

        btnSelectImage.setOnClickListener(v -> onClickRequestPermission());

        btnGetIn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallApi();
            }
        });
    }

    private void initUI() {
        tvSeller = findViewById(R.id.seller);
        tvAddress = findViewById(R.id.address);
        tvTimestamp = findViewById(R.id.timestamp);
        tvTotalCost = findViewById(R.id.total_cost);

        imgFromGallery = findViewById(R.id.img_from_gallery);

        btnGetIn4 = findViewById(R.id.btn_get_in4);
        btnSelectImage = findViewById(R.id.btn_select_image);
    }

    private void onClickRequestPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openGallery();
            return;
        }

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else{
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void CallApi() {

        mProressDialog.show();

        String strRealPath = RealPathUtil.getRealPath(this, mUri);
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
                Toast.makeText(InvoiceInformationActivity.this, "Call Api False", Toast.LENGTH_SHORT).show();
            }
        });
    }
}