package com.divinesecurity.safehouse.invoicePackage;

/**
 * Created by ang3l on 4/3/2018.
 */

public class InvoiceDataModel {
    private String invno;
    private String date;
    private String price;
    private String iacc;

    public InvoiceDataModel(String no, String date, String price, String iacc) {
        this.invno = no;
        this.date = date;
        this.price = price;
        this.iacc = iacc;
    }

    public String getNo() {
        return invno;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }

    public String getAcc() {
        return iacc;
    }
}
