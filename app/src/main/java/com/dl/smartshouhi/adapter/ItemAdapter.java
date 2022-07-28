package com.dl.smartshouhi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.ItemTest;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<ItemTest> itemList;
    private IClickListener iClickListener;

    public interface IClickListener{
        void onClickUpdateItem(ItemTest item, int position);
    }

    public ItemAdapter(List<ItemTest> itemList, IClickListener iClickListener) {
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
        ItemTest item = itemList.get(position);
        if(item == null){
            return;
        }
        holder.tvItemName.setText(item.getItem_name());
        holder.tvItemCost.setText(item.getCost_item()+"");

        holder.btnUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.onClickUpdateItem(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(itemList != null){
            return itemList.size();
        }
        return 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView tvItemName, tvItemCost;
        private Button btnUpdateItem;

        public ItemViewHolder(View itemView){
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemCost = itemView.findViewById(R.id.tv_item_cost);
            btnUpdateItem = itemView.findViewById(R.id.btn_update_item);

        }
    }
}
