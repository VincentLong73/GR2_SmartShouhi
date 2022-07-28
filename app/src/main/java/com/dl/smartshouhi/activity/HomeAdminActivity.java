package com.dl.smartshouhi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dl.smartshouhi.R;
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

public class HomeAdminActivity extends AppCompatActivity {

    private TextView tvUserNumber;
    private TextView tvInvoiceNumber;
    private TextView tvItemNumber;
    private RecyclerView rcvUser;
    private List<UserModel> listUser;
    private UserAdapter userAdapter;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        
        initUI();
        initListener();
        
    }

    private void initUI() {
        tvUserNumber = findViewById(R.id.tv_user_number);
        tvInvoiceNumber = findViewById(R.id.tv_invoice_number);
        tvItemNumber = findViewById(R.id.tv_item_number);
        listUser = new ArrayList<>();
        rcvUser = findViewById(R.id.rcv_user);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeAdminActivity.this);
        rcvUser.setLayoutManager(linearLayoutManager);


        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_INFO_FOR_ADMIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.substring(1, response.length() - 1);
                String[] result = response.split("#");
                if(result[0].equals("200")){
                    String countUser = result[1];
                    String countInvoice = result[2];
                    String countItem = result[3];

                    tvUserNumber.setText(countUser);
                    tvInvoiceNumber.setText(countInvoice);
                    tvItemNumber.setText(countItem);
                }else{
                    Toast.makeText(HomeAdminActivity.this, result[1], Toast.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            Toast.makeText(HomeAdminActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

                Gson gson = new Gson();
                listUser = Arrays.asList(gson.fromJson(response, UserModel[].class));
                if(listUser.size() > 0){
                    initRecycleView();
                }
            }
        }, error -> {
            Toast.makeText(HomeAdminActivity.this, "Error", Toast.LENGTH_SHORT).show();
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