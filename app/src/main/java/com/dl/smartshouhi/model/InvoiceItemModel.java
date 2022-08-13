package com.dl.smartshouhi.model;

import java.util.List;

public class InvoiceItemModel {

    InvoiceModel invoice;
    List<ItemModel> item;

    public InvoiceItemModel() {
    }

    public InvoiceModel getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceModel invoice) {
        this.invoice = invoice;
    }

    public List<ItemModel> getItem() {
        return item;
    }

    public void setItem(List<ItemModel> item) {
        this.item = item;
    }
}
