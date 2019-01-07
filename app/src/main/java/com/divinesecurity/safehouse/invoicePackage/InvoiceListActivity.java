package com.divinesecurity.safehouse.invoicePackage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.listViewAdapterPackage.Invoice_Adapter;
import com.divinesecurity.safehouse.paypalPackage.Config;
import com.divinesecurity.safehouse.pdfPackage.TemplatePDF;
import com.divinesecurity.safehouse.toolsPackage.Tools;
import com.divinesecurity.safehouse.utils.CustomListView;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;

public class InvoiceListActivity extends AppCompatActivity {

    ArrayList<InvoiceDataModel> dataModels;
    CustomListView listView;
    private Invoice_Adapter adapter;

    MyDataBaseAdapter dataBaseAdapter;

    private TemplatePDF templatePDF;

    private String[] header = {"Item", "Quantity", "Description", "Rate", "SubTotal"};
    String iName, iAddress, iDate, iDueDate, iNo, iTotal;


    //PAYPAL
    public static final int PAYPAL_REQUEST_CODE = 7171;
    private  static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    String payamount, payinvNo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbaridInvoice);
        setSupportActionBar(toolbar);
        //this line shows back button
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.list_invoice);
        dataModels = new ArrayList<>();

        // create a instance of SQLite Database
        dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        LoadChatGroupList();

        adapter = new Invoice_Adapter(dataModels,getApplicationContext(), this);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                createPDF(position);
                templatePDF.appViewPDF(InvoiceListActivity.this);
            }
        });

        Tools.setScreen_active("invoicescreen");

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mypreference.edit();
        editor.putString("pinvbadge", "0");
        editor.apply();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("broad-invoice"));
    }

    public void LoadChatGroupList(){
        Cursor cursor = dataBaseAdapter.getEntry_I_List();
        int cant = cursor.getCount();

        InvoiceDataModel invoiceDataModel;
        if (cant > 0) {
            while (cursor.moveToNext()) { //move for columns
                //cursor.moveToFirst();
                invoiceDataModel = new InvoiceDataModel(cursor.getString(cursor.getColumnIndex("INVNO")),
                        cursor.getString(cursor.getColumnIndex("INVDATE")),
                        cursor.getString(cursor.getColumnIndex("INVTOTAL")),
                        cursor.getString(cursor.getColumnIndex("ACCOUNTNAME")));
                dataModels.add(invoiceDataModel);
            }
        }
        cursor.close();
        /*if (!is_GropuList_Empty()) {
            ListViewRefresh();
        }*/
    }

    private void createPDF(int position) {
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
        templatePDF.addMetaData("Invoice", "Clients", "Divine Security");

        templatePDF.addTitles("Divine Security", "Invoice");

        ArrayList<String[]> rows;
        rows = getInvoice(position);

        templatePDF.createTableDates(iDate, iDueDate, iNo);
        templatePDF.createTableClientDet(new String[]{iName, iAddress});
        templatePDF.createTableBill(header, rows);

        templatePDF.createTableTotal(iTotal);

        String shortText = "Thank you for your business";
        templatePDF.addParagrah(shortText);

        templatePDF.closeDocument();
    }

    private ArrayList<String[]> getInvoice(int pos){
        ArrayList<String[]> rows = new ArrayList<>();

        Cursor cursor = dataBaseAdapter.getEntry_I(dataModels.get(pos).getNo());
        int cant = cursor.getCount();
        String[] itemArr, qtyArr, descArr, rateArr, subTArr;
        String iItem, iQty, iDesc, iRate, iSubTotal;
        if (cant > 0) {
            while (cursor.moveToNext()) { //move for columns
                //cursor.moveToFirst();
                iName    = cursor.getString(cursor.getColumnIndex("ACCNAME"));
                iAddress = cursor.getString(cursor.getColumnIndex("ACCADDRESS"));
                iNo      = cursor.getString(cursor.getColumnIndex("INVNO"));
                iDate    = cursor.getString(cursor.getColumnIndex("INVDATE"));
                iDueDate = cursor.getString(cursor.getColumnIndex("INVDUEDATE"));
                iTotal   = cursor.getString(cursor.getColumnIndex("INVTOTAL"));

                iItem     = cursor.getString(cursor.getColumnIndex("INVITEM"));
                iQty      = cursor.getString(cursor.getColumnIndex("INVQTY"));
                iDesc     = cursor.getString(cursor.getColumnIndex("INVDESC"));
                iRate     = cursor.getString(cursor.getColumnIndex("INVRATE"));
                iSubTotal = cursor.getString(cursor.getColumnIndex("INVSUBTOTAL"));

                itemArr = iItem.split(";");
                qtyArr  = iQty.split(";");
                descArr = iDesc.split(";");
                rateArr = iRate.split(";");
                subTArr = iSubTotal.split(";");

                for (int i=0; i<itemArr.length; i++){
                    rows.add(new String[]{itemArr[i], qtyArr[i], descArr[i], rateArr[i], subTArr[i]});
                }
            }
        }
        cursor.close();

        return rows;
    }

    public void processPayment(String amount, String invno) {
        this.payamount = amount;
        this.payinvNo = invno;
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD",
                "Pay Invoice", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null){
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PayPalPaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", payamount)
                        );
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
            else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoadChatGroupList();
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
    };
}


