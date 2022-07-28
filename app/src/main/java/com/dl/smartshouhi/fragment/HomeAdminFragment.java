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
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
import com.dl.smartshouhi.activity.HomeAdminActivity;
import com.dl.smartshouhi.adapter.UserAdapter;
import com.dl.smartshouhi.model.UserModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dl.smartshouhi.constaint.Constant.URL_GET_USER_NOT_ADMIN;
import static com.dl.smartshouhi.constaint.Constant.URL_INFO_FOR_ADMIN;

public class HomeAdminFragment extends Fragment {
    private TextView tvUserNumber;
    private TextView tvInvoiceNumber;
    private TextView tvItemNumber;
    private RecyclerView rcvUser;
    private List<UserModel> listUser;
    private UserAdapter userAdapter;
    private View mView;

    private RequestQueue requestQueue;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.activity_home_admin, container, false);
        initUI();
        initListener();
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


        requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_INFO_FOR_ADMIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.substring(1, response.length()-1);

                String[] listResult = response.split("#");
                if(listResult[0].equals("200")){
                    String countUser = listResult[1];
                    String countInvoice = listResult[2];
                    String countItem = listResult[3];

                    tvUserNumber.setText(countUser);
                    tvInvoiceNumber.setText(countInvoice);
                    tvItemNumber.setText(countItem);
                }else{
                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };
        requestQueue.add(stringRequest);

        StringRequest stringRequestUser = new StringRequest(Request.Method.GET, URL_GET_USER_NOT_ADMIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.substring(1, response.length()-1);
                Gson gson = new Gson();

                String[] listResult = response.split("#");

                if(listResult[0].equals("200")){
                    listUser = Arrays.asList(gson.fromJson(listResult[1], UserModel[].class));
                    initRecycleView();
                }else{
                    Toast.makeText(getActivity(), listResult[1], Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };

        requestQueue.add(stringRequestUser);
    }

    private void initListener() {
    }

    private void initRecycleView(){
        userAdapter = new UserAdapter(listUser);
        rcvUser.setAdapter(userAdapter);
    }

}
