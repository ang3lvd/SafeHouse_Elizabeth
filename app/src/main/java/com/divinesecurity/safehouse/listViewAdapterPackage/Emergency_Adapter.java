package com.divinesecurity.safehouse.listViewAdapterPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.emergencyPackage.EmergencyDataModel;

import java.util.ArrayList;

/**
 * Created by ang3l on 4/3/2018.
 */

public class Emergency_Adapter extends ArrayAdapter<EmergencyDataModel> {


    //private EmergencyListActivity emergencyListActivity;

    private ArrayList<EmergencyDataModel> dataSet;
    private Context mContext;
    private int lastPosition = -1;

    private String beforeAcc;

    public Emergency_Adapter(ArrayList<EmergencyDataModel> data, Context context) {
        super(context, R.layout.item_emergency, data);
        this.dataSet = data;
        this.mContext=context;
        //this.emergencyListActivity = emergencyListActivity;

        this.beforeAcc = "Account";

    }

    // View lookup cache
    private static class ViewHolder {
        TextView tv_eName;
        //TextView tv_eEmergency;
        TextView tv_eStatus;
        TextView tv_eDate;
        TextView tv_eAcc;
        ImageView iv_eImage;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        //InvoiceDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        Emergency_Adapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_emergency, parent,false);

            viewHolder = new ViewHolder();
            viewHolder.tv_eName      = convertView.findViewById(R.id.tv_contactEmergency);
            //viewHolder.tv_eEmergency = convertView.findViewById(R.id.tv_emergencyInfo);
            viewHolder.tv_eStatus    = convertView.findViewById(R.id.tv_newEmergency);
            viewHolder.tv_eDate      = convertView.findViewById(R.id.tv_emergencyDate);
            viewHolder.iv_eImage     = convertView.findViewById(R.id.img_emergency_ic);

            viewHolder.tv_eAcc   = convertView.findViewById(R.id.emerg_account);

            /*convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mContext, "Button was clicked as position: "+position, Toast.LENGTH_SHORT).show();
                    emergencyListActivity.onEmergencyClick(position);
                }
            });*/

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position%2 ==0) ? R.anim.right_in : R.anim.left_in);
        convertView.startAnimation(animation);
        lastPosition = position;

        viewHolder.tv_eName.setText(dataSet.get(position).getEName());
        //viewHolder.tv_eEmergency.setText(dataSet.get(position).getECategory().toUpperCase());
        viewHolder.tv_eStatus.setVisibility(dataSet.get(position).getEStatus() ? View.INVISIBLE : View.VISIBLE);
        viewHolder.tv_eDate.setText(dataSet.get(position).getEDate());

        viewHolder.iv_eImage.setImageResource(R.drawable.ic_alarm_24);

        if(!dataSet.get(position).getEAcc().equals(beforeAcc)){
            viewHolder.tv_eAcc.setText(dataSet.get(position).getEAcc());
            viewHolder.tv_eAcc.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_eAcc.setVisibility(View.GONE);
        }
        beforeAcc = dataSet.get(position).getEAcc();
        // Return the completed view to render on screen
        return convertView;
    }



}
