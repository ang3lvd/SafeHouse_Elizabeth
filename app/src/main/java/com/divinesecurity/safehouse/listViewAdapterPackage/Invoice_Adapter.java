package com.divinesecurity.safehouse.listViewAdapterPackage;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.invoicePackage.InvoiceDataModel;
import com.divinesecurity.safehouse.invoicePackage.InvoiceListActivity;

import java.util.ArrayList;

/**
 * Created by ang3l on 4/3/2018.
 */

public class Invoice_Adapter extends ArrayAdapter<InvoiceDataModel> {


    private InvoiceListActivity invoiceListActivity;

    private ArrayList<InvoiceDataModel> dataSet;
    private Context mContext;
    private int lastPosition = -1;

    private String beforeAcc;

    public Invoice_Adapter(ArrayList<InvoiceDataModel> data, Context context, InvoiceListActivity invoiceListActivity) {
        super(context, R.layout.item_invoice, data);
        this.dataSet = data;
        this.mContext=context;
        this.invoiceListActivity = invoiceListActivity;

        this.beforeAcc = "nullnull_ds";

    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtinvNo;
        TextView txtinvDate;
        TextView txtinvPrice;
        Button btnpay;
        RelativeLayout rl_inv_item;
        TextView inv_account;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        //InvoiceDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_invoice, parent,false);

            viewHolder = new ViewHolder();
            viewHolder.txtinvNo    = convertView.findViewById(R.id.inv_no);
            viewHolder.txtinvDate  = convertView.findViewById(R.id.inv_dt);
            viewHolder.txtinvPrice = convertView.findViewById(R.id.inv_price);
            viewHolder.btnpay      = convertView.findViewById(R.id.inv_pay);
            viewHolder.rl_inv_item = convertView.findViewById(R.id.rl_invoice_item);

            viewHolder.inv_account   = convertView.findViewById(R.id.zone_account);

            viewHolder.btnpay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mContext, "Button was clicked as position: "+position, Toast.LENGTH_SHORT).show();
                    /*AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(mContext);
                    alertdialogbuilder.setTitle("Select payment method ");

                    alertdialogbuilder.setItems(value, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //String selectedText = Arrays.asList(value).get(which);
                            //Toast.makeText(mContext, "Have you select "+ Arrays.asList(value).get(which) + " for pay " + dataSet.get(position).getPrice(), Toast.LENGTH_SHORT).show();
                            //invoiceListActivity.processPayment(dataSet.get(position).getPrice(), dataSet.get(position).getNo());
                        }
                    });
                    AlertDialog dialog = alertdialogbuilder.create();
                    dialog.show();*/

                    invoiceListActivity.processPayment(dataSet.get(position).getPrice(), dataSet.get(position).getNo());
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtinvNo.setText("Invoice No: "+dataSet.get(position).getNo());
        viewHolder.txtinvDate.setText("Date: "+dataSet.get(position).getDate());
        viewHolder.txtinvPrice.setText("XCD "+dataSet.get(position).getPrice());
        viewHolder.rl_inv_item.setBackgroundColor(Color.parseColor("#88c2df"));

        if(!dataSet.get(position).getAcc().equals(beforeAcc)){
            viewHolder.inv_account.setText(dataSet.get(position).getAcc());
            viewHolder.inv_account.setVisibility(View.VISIBLE);
        } else {
            viewHolder.inv_account.setVisibility(View.GONE);
        }
        beforeAcc = dataSet.get(position).getAcc();
        // Return the completed view to render on screen
        return convertView;
    }



}
