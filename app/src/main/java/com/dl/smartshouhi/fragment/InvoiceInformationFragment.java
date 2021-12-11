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
    private View imgButtonZoom;



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
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

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

//    private void zoomImageFromThumb(final View thumbView) {
//        // If there's an animation in progress, cancel it
//        // immediately and proceed with this one.
//        if (currentAnimator != null) {
//            currentAnimator.cancel();
//        }
//
//        // Load the high-resolution "zoomed-in" image.
//        final ImageView expandedImageView = mView.findViewById(
//                R.id.expanded_image);
////        int idImageFromGallery = getResources().getIdentifier(mUri.getPath(), "drawable", getActivity().getPackageName());
////        expandedImageView.setImageResource(idImageFromGallery);
//        Log.e("DRAWABLE1 : " ,(getResources().getIdentifier(mUri.getPath(), "drawable", getActivity().getPackageName()))+"");
//        Log.e("DRAWABLE2 : ", R.drawable.hoa_don + "");
//        expandedImageView.setImageResource(R.drawable.hoa_don);
//
//        // Calculate the starting and ending bounds for the zoomed-in image.
//        // This step involves lots of math. Yay, math.
//        final Rect startBounds = new Rect();
//        final Rect finalBounds = new Rect();
//        final Point globalOffset = new Point();
//
//        // The start bounds are the global visible rectangle of the thumbnail,
//        // and the final bounds are the global visible rectangle of the container
//        // view. Also set the container view's offset as the origin for the
//        // bounds, since that's the origin for the positioning animation
//        // properties (X, Y).
//        thumbView.getGlobalVisibleRect(startBounds);
//        mView.findViewById(R.id.container)
//                .getGlobalVisibleRect(finalBounds, globalOffset);
//        startBounds.offset(-globalOffset.x, -globalOffset.y);
//        finalBounds.offset(-globalOffset.x, -globalOffset.y);
//
//        // Adjust the start bounds to be the same aspect ratio as the final
//        // bounds using the "center crop" technique. This prevents undesirable
//        // stretching during the animation. Also calculate the start scaling
//        // factor (the end scaling factor is always 1.0).
//        float startScale;
//        if ((float) finalBounds.width() / finalBounds.height()
//                > (float) startBounds.width() / startBounds.height()) {
//            // Extend start bounds horizontally
//            startScale = (float) startBounds.height() / finalBounds.height();
//            float startWidth = startScale * finalBounds.width();
//            float deltaWidth = (startWidth - startBounds.width()) / 2;
//            startBounds.left -= deltaWidth;
//            startBounds.right += deltaWidth;
//        } else {
//            // Extend start bounds vertically
//            startScale = (float) startBounds.width() / finalBounds.width();
//            float startHeight = startScale * finalBounds.height();
//            float deltaHeight = (startHeight - startBounds.height()) / 2;
//            startBounds.top -= deltaHeight;
//            startBounds.bottom += deltaHeight;
//        }
//
//        // Hide the thumbnail and show the zoomed-in view. When the animation
//        // begins, it will position the zoomed-in view in the place of the
//        // thumbnail.
//        thumbView.setAlpha(0f);
//        expandedImageView.setVisibility(View.VISIBLE);
//
//        // Set the pivot point for SCALE_X and SCALE_Y transformations
//        // to the top-left corner of the zoomed-in view (the default
//        // is the center of the view).
//        expandedImageView.setPivotX(0f);
//        expandedImageView.setPivotY(0f);
//
//        // Construct and run the parallel animation of the four translation and
//        // scale properties (X, Y, SCALE_X, and SCALE_Y).
//        AnimatorSet set = new AnimatorSet();
//        set
//                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
//                        startBounds.left, finalBounds.left))
//                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
//                        startBounds.top, finalBounds.top))
//                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
//                        startScale, 1f))
//                .with(ObjectAnimator.ofFloat(expandedImageView,
//                        View.SCALE_Y, startScale, 1f));
//        set.setDuration(shortAnimationDuration);
//        set.setInterpolator(new DecelerateInterpolator());
//        set.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                currentAnimator = null;
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                currentAnimator = null;
//            }
//        });
//        set.start();
//        currentAnimator = set;
//
//        // Upon clicking the zoomed-in image, it should zoom back down
//        // to the original bounds and show the thumbnail instead of
//        // the expanded image.
//        final float startScaleFinal = startScale;
//        expandedImageView.setOnClickListener(view -> {
//            if (currentAnimator != null) {
//                currentAnimator.cancel();
//            }
//
//            // Animate the four positioning/sizing properties in parallel,
//            // back to their original values.
//            AnimatorSet set1 = new AnimatorSet();
//            set1.play(ObjectAnimator
//                    .ofFloat(expandedImageView, View.X, startBounds.left))
//                    .with(ObjectAnimator
//                            .ofFloat(expandedImageView,
//                                    View.Y,startBounds.top))
//                    .with(ObjectAnimator
//                            .ofFloat(expandedImageView,
//                                    View.SCALE_X, startScaleFinal))
//                    .with(ObjectAnimator
//                            .ofFloat(expandedImageView,
//                                    View.SCALE_Y, startScaleFinal));
//            set1.setDuration(shortAnimationDuration);
//            set1.setInterpolator(new DecelerateInterpolator());
//            set1.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    thumbView.setAlpha(1f);
//                    expandedImageView.setVisibility(View.GONE);
//                    currentAnimator = null;
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                    thumbView.setAlpha(1f);
//                    expandedImageView.setVisibility(View.GONE);
//                    currentAnimator = null;
//                }
//            });
//            set1.start();
//            currentAnimator = set1;
//        });
//    }


    public void setBitmapImageView(Bitmap bitmapImageView){
        imgFromGallery.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }
}
