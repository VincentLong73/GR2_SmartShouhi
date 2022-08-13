package com.dl.smartshouhi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dl.smartshouhi.R;
import com.dl.smartshouhi.model.Invoice;

import java.util.List;


public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private final List<Invoice> invoiceList;
    private final IClickListener iClickListener;

    public interface IClickListener{
        void onClickUpdateItem(Invoice invoice, int position);
        void onClickDeleteItem(Invoice invoice, int position);
    }

    public InvoiceAdapter(List<Invoice> invoiceList, IClickListener iClickListener) {
        this.invoiceList = invoiceList;
        this.iClickListener = iClickListener;
    }


    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  InvoiceViewHolder holder, int position) {
        Invoice invoice = invoiceList.get(position);
        if(invoice == null){
            return;
        }
        holder.tvSeller.setText(invoice.getSeller());
        holder.tvAddress.setText(invoice.getAddress());
        holder.tvTimestamp.setText(invoice.getTimestamp());
        holder.tvTotalCost.setText(invoice.getTotalCost()+"");

        holder.imgBtnUpdate.setOnClickListener(v -> iClickListener.onClickUpdateItem(invoice, position));
        holder.imgBtnDelete.setOnClickListener(v -> iClickListener.onClickDeleteItem(invoice, position));

    }

    @Override
    public int getItemCount() {
        if(invoiceList!=null){
            return invoiceList.size();
        }
        return 0;
    }

    public static class InvoiceViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvSeller;
        private final TextView tvAddress;
        private final TextView tvTimestamp;
        private final TextView tvTotalCost;
        private final ImageButton imgBtnUpdate;
        private final ImageButton imgBtnDelete;


        public InvoiceViewHolder(View itemView){
            super(itemView);
            tvSeller = itemView.findViewById(R.id.tv_seller);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvTotalCost = itemView.findViewById(R.id.tv_total_cost);
            imgBtnUpdate = itemView.findViewById(R.id.btn_update_item_invoice);
            imgBtnDelete = itemView.findViewById(R.id.btn_delete_item_invoice);

        }
    }
}
