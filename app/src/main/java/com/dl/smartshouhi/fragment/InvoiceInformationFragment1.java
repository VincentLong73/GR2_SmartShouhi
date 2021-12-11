package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activities.HomeActivity;
import com.dl.smartshouhi.adapter.PhotoAdapter;
import com.dl.smartshouhi.api.ApiService;
import com.dl.smartshouhi.model.Invoice;
import com.dl.smartshouhi.utils.RealPathUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import me.relex.circleindicator.CircleIndicator3;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceInformationFragment1 extends Fragment {

    private HomeActivity homeActivity;
    private View mView;

    private static final int MY_REQUEST_CODE = 7;

    private EditText edtSeller;
    private EditText edtAddress;
    private EditText edtTimestamp;
    private EditText edtTotalCost;
    private ImageButton imgButtonCalendar;

    private ImageView imgMerged;


    private PhotoAdapter photoAdapter1;

    private ViewPager2 viewPager2;
    private CircleIndicator3 circleIndicator3;



    private ImageView imgFromGallery;

    private Button btnGetIn4, btnSelectImage, btnSaveInfo;

    private Uri mUri;
    private ProgressDialog mProressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.test_select_multi_image, container, false);

        initUI();
        initListener();

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


        String currentDate1 = DateFormat.format("dd/MM/yyyy", new Date()).toString();

        edtTimestamp.setText(currentDate1);

        imgFromGallery = mView.findViewById(R.id.img_from_gallery);

        imgMerged = mView.findViewById(R.id.img_merged);

        btnGetIn4 = mView.findViewById(R.id.btn_get_in4);
        btnSelectImage = mView.findViewById(R.id.btn_select_image);
        btnSaveInfo = mView.findViewById(R.id.btn_save_info);
        imgButtonCalendar = mView.findViewById(R.id.btn_calendar);

        viewPager2 = mView.findViewById(R.id.view_pager);
        circleIndicator3 = mView.findViewById(R.id.circle_indicator);

        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);


        photoAdapter1 = new PhotoAdapter(homeActivity);
        viewPager2.setAdapter(photoAdapter1);
        circleIndicator3.setViewPager(viewPager2);

    }

    private void initListener() {
        btnSelectImage.setOnClickListener(v -> onClickRequestPermission());

//        btnGetIn4.setOnClickListener(v -> callApi());
        btnGetIn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mergeListImages();
//                callApi();
            }
        });
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
            }
        });
        imgButtonCalendar.setOnClickListener(v -> displayCalendar());

    }



    private void mergeListImages() {
        List<Uri> listUri = photoAdapter1.getListUriPhoto();
        List<Bitmap> listBimapImages = new ArrayList<>();
        for( Uri uri : listUri){
            try {
                listBimapImages.add(MediaStore.Images.Media.getBitmap(homeActivity.getContentResolver(),uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap bmOverlay;


        List<Integer> dimensionImage = getDimensionImage(listBimapImages);
        bmOverlay = Bitmap.createBitmap(dimensionImage.get(1), dimensionImage.get(0),
                listBimapImages.get(0).getConfig());

        imgMerged.setImageBitmap(bmOverlay);
    }

    private void merImageIntoCanvas(Bitmap bmOverlay, List<Bitmap> bitmapList) {

        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmapList.get(0), new Matrix(), null);
        for(int i = 1; i< bitmapList.size(); i++){
            canvas.drawBitmap(bitmapList.get(i), 0, bitmapList.get(i-1).getHeight(), null);
        }

    }

    private List<Integer> getDimensionImage(List<Bitmap> listBimapImages) {
        int width = 0;
        int height = 0;
        for (Bitmap bitmap : listBimapImages){
            if(bitmap.getWidth() > width){
                width = bitmap.getWidth();
            }
            height += bitmap.getHeight();
        }
        List<Integer> result = new ArrayList<>();
        result.add(height);
        result.add(width);

        return result;
    }

    private void requestPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectImagesFromGalerry();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(homeActivity, "Permission denied", Toast.LENGTH_LONG).show();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

    }

    private void selectImagesFromGalerry() {

        TedBottomPicker.with(homeActivity)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        if(uriList != null && !uriList.isEmpty()){
                            photoAdapter1.setData(uriList);
                        }
                    }
                });


    }

    private void onClickRequestPermission() {

        if(homeActivity == null){
            return;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
//            homeActivity.openGallery();
            selectImagesFromGalerry();
            return;
        }

        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//            homeActivity.openGallery();
            selectImagesFromGalerry();
        }else{
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, MY_REQUEST_CODE);
        }
    }


    private void callApi() {

        mProressDialog.show();

        String strRealPath = RealPathUtil.getRealPath(getActivity(), mUri);
        Log.e("DuynDuyn", strRealPath);
//        File file1 = new File(imgMerged.get);
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

    private void saveInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }

        Invoice invoice = new Invoice();
        invoice.setSeller(edtSeller.getText().toString());
        invoice.setAddress(edtAddress.getText().toString());
        invoice.setTimestamp(edtTimestamp.getText().toString());
        invoice.setTotalCost(Float.parseFloat(edtTotalCost.getText().toString()));

        getUserByEmail();

        /* save into firebase
        https://console.firebase.google.com/u/6/project/smart-shouhi/database/smart-shouhi-default-rtdb/data
        https://firebase.google.com/docs/database/admin/save-data
        */

    }

    private void getUserByEmail() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference ref = database.getReference("users");
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
