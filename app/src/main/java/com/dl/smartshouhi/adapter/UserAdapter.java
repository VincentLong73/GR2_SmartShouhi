package com.dl.smartshouhi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.UserModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private List<UserModel> listUser;

    public UserAdapter(List<UserModel> listUser) {
        this.listUser = listUser;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserAdapter.UserViewHolder holder, int position) {
        UserModel userModel = listUser.get(position);
        holder.tvUsername.setText(userModel.getUserName());
        holder.tvEmail.setText(userModel.getEmail());
        if(userModel.isActive()){
            holder.tvStatus.setText("Active");
        }else{
            holder.tvStatus.setText("Inactive");
        }
    }

    @Override
    public int getItemCount() {
        if(listUser != null){
            return listUser.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView tvUsername, tvEmail, tvStatus;
        public UserViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username_admin);
            tvEmail = itemView.findViewById(R.id.tv_email_admin);
            tvStatus = itemView.findViewById(R.id.tv_status);

        }
    }
}
