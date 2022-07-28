package com.dl.smartshouhi.model;

import java.util.List;

public class InvoiceItemModel {

    InvoiceTest invoice;
    List<ItemTest> item;

    public InvoiceItemModel() {
    }

    public InvoiceTest getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceTest invoice) {
        this.invoice = invoice;
    }

    public List<ItemTest> getItem() {
        return item;
    }

    public void setItem(List<ItemTest> item) {
        this.item = item;
    }
}
