package com.dl.smartshouhi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.Invoice;
import com.dl.smartshouhi.model.ItemModel;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final List<ItemModel> itemList;
    private final IClickListener iClickListener;

    public interface IClickListener{
        void onClickUpdateItem(ItemModel item, int position);
        void onClickDeleteItem(ItemModel item, int position);
    }

    public ItemAdapter(List<ItemModel> itemList, IClickListener iClickListener) {
        this.itemList = itemList;
        this.iClickListener = iClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ItemViewHolder holder, int position) {
        ItemModel item = itemList.get(position);
        if(item == null){
            return;
        }
        holder.tvItemName.setText(item.getItem_name());
        holder.tvItemCost.setText(item.getCost_item()+"");

        holder.imgBtnUpdate.setOnClickListener(v -> iClickListener.onClickUpdateItem(item, position));
        holder.imgBtnDelete.setOnClickListener(v -> iClickListener.onClickDeleteItem(item, position));
    }

    @Override
    public int getItemCount() {
        if(itemList != null){
            return itemList.size();
        }
        return 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvItemName;
        private final TextView tvItemCost;
        private final ImageButton imgBtnUpdate;
        private final ImageButton imgBtnDelete;

        public ItemViewHolder(View itemView){
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemCost = itemView.findViewById(R.id.tv_item_cost);
            imgBtnUpdate = itemView.findViewById(R.id.btn_update_item);
            imgBtnDelete = itemView.findViewById(R.id.btn_delete_item);

        }
    }
}
