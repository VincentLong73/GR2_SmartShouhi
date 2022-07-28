package com.dl.smartshouhi.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.dl.smartshouhi.activity.HomeActivity;
import com.dl.smartshouhi.adapter.PhotoAdapter;
import com.dl.smartshouhi.model.InvoiceModel;
import com.dl.smartshouhi.model.Item;
import com.dl.smartshouhi.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import me.relex.circleindicator.CircleIndicator3;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
    private Bitmap bitmapImageMerged;


    private PhotoAdapter photoAdapter1;

    private ViewPager2 viewPager2;
    private CircleIndicator3 circleIndicator3;



    private ImageView imgFromGallery;

    private Button btnGetIn4, btnSelectImage, btnSaveInfo;

    private Uri mUri;
    private ProgressDialog mProgressDialog;

    private long totalUser;
    private int indexUserCurrent;

    private Dialog dialogItem;
    private View viewDialogItem;
    private Button btnSaveItem;
    private EditText edtItemName;
    private EditText edtItemCost;
    private List<Item> itemList;
    private Button btnAddItem;
    private Button btnSeeListItem;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.test_select_multi_image, container, false);

        initUI();
        initListener();
        showDialogItem();
        return mView;
    }



    private void initUI() {
        homeActivity = (HomeActivity) getActivity();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Please wait ...");

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
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager2.setPageTransformer(compositePageTransformer);


        photoAdapter1 = new PhotoAdapter(homeActivity);
        viewPager2.setAdapter(photoAdapter1);
        circleIndicator3.setViewPager(viewPager2);

        itemList = new ArrayList<>();
        btnAddItem = mView.findViewById(R.id.btn_add_item);
        btnSeeListItem = mView.findViewById(R.id.btn_see_list_item);
        /*S- Bat dau khoi tao UI trong Dialog Item */
        dialogItem = new Dialog(getActivity());
        dialogItem.setCancelable(true);

        viewDialogItem  = homeActivity.getLayoutInflater().inflate(R.layout.layout_dialog_add_item, null);
        dialogItem.setContentView(viewDialogItem);

        edtItemName = viewDialogItem.findViewById(R.id.edt_item_name);
        edtItemCost = viewDialogItem.findViewById(R.id.edt_item_cost);

        btnSaveItem = viewDialogItem.findViewById(R.id.btn_save_item);

        /*E- Ket thuc khoi tao UI trong Dialog Item */


    }

    private void initListener() {
        btnSelectImage.setOnClickListener(v -> onClickRequestPermission());

//        btnGetIn4.setOnClickListener(v -> callApi());
        btnGetIn4.setOnClickListener(view -> {
            mergeListImages();
                callApi();
        });
        btnSaveInfo.setOnClickListener(view -> saveInfo());

        btnAddItem.setOnClickListener(v -> showDialogItem());

        btnSeeListItem.setOnClickListener(v -> {

        });
        imgButtonCalendar.setOnClickListener(v -> displayCalendar());
        btnSaveItem.setOnClickListener(view -> onClickSaveItem());

    }

    private void showDialogItem(){


        edtItemName.setText("");
        edtItemCost.setText("");

        initListenerDialogItem();

        dialogItem.show();
    }

    private void initListenerDialogItem(){


    }

    private void onClickSaveItem(){
        String itemName = edtItemName.getText().toString().trim();
        float itemCost = Float.parseFloat(edtItemCost.getText().toString().trim());
        Item item = new Item(itemName, itemCost);
        itemList.add(item);
        showDialogItem();
    }

    private void onClickFinishItem(){
        Log.e("Size List Item :", itemList.size()+"");
        dialogItem.dismiss();
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


        bitmapImageMerged = merImageIntoCanvas(bmOverlay,listBimapImages);
        Bitmap bitmapImageBlur = fastblur(bitmapImageMerged, 2f,2);
        imgMerged.setImageBitmap(bitmapImageBlur);
//        imgMerged.setImageBitmap(bitmapImageMerged);

    }

    private Bitmap merImageIntoCanvas(Bitmap bmOverlay,List<Bitmap> bitmapList) {


        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmapList.get(0), new Matrix(), null);
        float height = 0;
        for(int i = 1; i< bitmapList.size(); i++){
            height += bitmapList.get(i-1).getHeight();
            canvas.drawBitmap(bitmapList.get(i), 0, height, null);
        }

        return bmOverlay;

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
//https://stackoverflow.com/questions/2067955/fast-bitmap-blur-for-android-sdk
    //https://stackoverflow.com/questions/34038157/blurred-image-issue-in-imageview
    public Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    private void requestPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectImagesFromGallery();
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

    private void selectImagesFromGallery() {

        TedBottomPicker.with(homeActivity)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .showMultiImage(uriList -> {
                    if(uriList != null && !uriList.isEmpty()){
                        photoAdapter1.setData(uriList);
                    }
                });


    }

    private void onClickRequestPermission() {

        if(homeActivity == null){
            return;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
//            homeActivity.openGallery();
            selectImagesFromGallery();
            return;
        }

        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//            homeActivity.openGallery();
            selectImagesFromGallery();
        }else{
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, MY_REQUEST_CODE);
        }
    }


    private void callApi() {

        mProgressDialog.show();

//        String strRealPath = RealPathUtil.getRealPath(getActivity(), mUri);
//        Log.e("DuynDuyn", strRealPath);
//        File file = new File(strRealPath);

        File file = bitmapToFile(homeActivity.getApplicationContext(),bitmapImageMerged,"invoice.jpg");
        RequestBody requestBodyImg = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),bitma)
        MultipartBody.Part multipartBodyImg = MultipartBody.Part.createFormData("image", file.getName(), requestBodyImg);

//        ApiService.apiService.getInformationInvoice2(multipartBodyImg).enqueue(new Callback<Invoice>() {
//            @Override
//            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
//
//                Invoice invoice = response.body();
//                if(invoice != null){
//                    mProgressDialog.dismiss();
//                    edtSeller.setText(invoice.getSeller());
//                    edtAddress.setText(invoice.getAddress());
//                    edtTimestamp.setText(invoice.getTimestamp());
//                    edtTotalCost.setText(invoice.getTotalCost()+"");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Invoice> call, Throwable t) {
//                mProgressDialog.dismiss();
//                t.printStackTrace();
//                Toast.makeText(getActivity(), "Call Api False", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public static File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + fileNameToSave);
            file.createNewFile();

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0 , bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file; // it will return null
        }
    }

    private void saveInfo() {
//        if(user == null){
//            return;
//        }
//
//        InvoiceModel invoice = new InvoiceModel();
//        invoice.setSeller(edtSeller.getText().toString());
//        invoice.setAddress(edtAddress.getText().toString());
//        invoice.setTimestamp(edtTimestamp.getText().toString());
//        invoice.setTotalCost(Float.parseFloat(edtTotalCost.getText().toString()));
//        saveInformationInvoiceOnFirebase(invoice);FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        /* save into firebase
        https://console.firebase.google.com/u/6/project/smart-shouhi/database/smart-shouhi-default-rtdb/data
        https://firebase.google.com/docs/database/admin/save-data
        */





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

    private void getTotalUserOnFb(InvoiceModel invoiceModel){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();
        myRef.child("totalUser").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.e("Total User", String.valueOf(task.getResult().getValue()));
                setTotalUser((Long) Long.parseLong(String.valueOf(task.getResult().getValue())));
                getIdUserCurrent(invoiceModel);
            }
        });
    }

    private void getIdUserCurrent(InvoiceModel invoiceModel){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        User user = new User(mAuth.getCurrentUser().getEmail());

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        for(int i = 0 ; i<getTotalUser() ; i++){
            int finalI = i;
            myRef.child(i+"").child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    if(task.isSuccessful()){
                        String email = String.valueOf(task.getResult().getValue());
                        if(email.equals(user.getEmail())){
                            setIndexUserCurrent(finalI);
                            getListInvoiceDatabase(invoiceModel);
                        }

                    }
                }
            });
        }
    }

    private void getListInvoiceDatabase(InvoiceModel invoiceModel){

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-shouhi-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference(getIndexUserCurrent()+"/invoices");

        myRef.child("totalInvoice").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                long totalInvoice = (Long) task.getResult().getValue();
                invoiceModel.setId((int) totalInvoice);
                myRef.child(totalInvoice+"").setValue(invoiceModel);
                myRef.child("totalInvoice").setValue(totalInvoice+1);
            }
        });
    }

    private void saveInformationInvoiceOnFirebase(InvoiceModel invoiceResult){
        getTotalUserOnFb(invoiceResult);
    }


    public void setBitmapImageView(Bitmap bitmapImageView){
        imgFromGallery.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
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
}
