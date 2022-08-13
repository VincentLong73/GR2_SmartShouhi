package com.dl.smartshouhi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activity.HomeAdminActivity;
import com.dl.smartshouhi.adapter.UserAdapter;
import com.dl.smartshouhi.api.AdminDbApi;
import com.dl.smartshouhi.model.ItemModel;
import com.dl.smartshouhi.model.UserModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dl.smartshouhi.constaint.Constant.URL_GET_USER_NOT_ADMIN;
import static com.dl.smartshouhi.constaint.Constant.URL_INFO_FOR_ADMIN;

public class HomeAdminFragment extends Fragment {
    private TextView tvUserNumber;
    private TextView tvInvoiceNumber;
    private TextView tvItemNumber;
    private RecyclerView rcvUser;
    private List<UserModel> listUser;
    private View mView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.activity_home_admin, container, false);
        initUI();
        return mView;
    }

    private void initUI() {
        tvUserNumber = mView.findViewById(R.id.tv_user_number);
        tvInvoiceNumber = mView.findViewById(R.id.tv_invoice_number);
        tvItemNumber = mView.findViewById(R.id.tv_item_number);
        listUser = new ArrayList<>();
        rcvUser = mView.findViewById(R.id.rcv_user);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvUser.setLayoutManager(linearLayoutManager);

        getCountUserInvoiceItem();
        getUserNotAdmin();
    }

    private void getCountUserInvoiceItem() {
        AdminDbApi.databaseApi.getCountUserInvoiceItem().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    String[] strResult = result.split("#");
                    if(strResult[0].equals("200")){
                        String countUser = strResult[1];
                        String countInvoice = strResult[2];
                        String countItem = strResult[3];

                        tvUserNumber.setText(countUser);
                        tvInvoiceNumber.setText(countInvoice);
                        tvItemNumber.setText(countItem);
                    }else{
                        Toast.makeText(getActivity(), strResult[1], Toast.LENGTH_SHORT).show();
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

    private void getUserNotAdmin() {
        AdminDbApi.databaseApi.getUserNotAdmin().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                try {
                    String result = response.body().string();
                    listUser = Arrays.asList(gson.fromJson(result, UserModel[].class));
                    if(listUser.size() > 0){
                        initRecycleView();
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

    private void initRecycleView(){
        UserAdapter userAdapter = new UserAdapter(listUser, new UserAdapter.IClickListener() {
            @Override
            public void onClickBlockUser(UserModel user, int position) {
                blockUser(user, position);
            }

            @Override
            public void onClickUnblock(UserModel user, int position) {
                unblockUser(user, position);
            }
        });
        rcvUser.setAdapter(userAdapter);
    }

    private void unblockUser(UserModel user, int position) {
    }

    private void blockUser(UserModel user, int position) {
    }

}
