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
import com.divinesecurity.safehouse.alarmPackage.AlarmDataModel;

import java.util.ArrayList;

/**
 * Created by ang3l on 6/3/2018.
 */

public class Alarm_Adapter extends ArrayAdapter<AlarmDataModel> {

    private ArrayList<AlarmDataModel> dataSet;
    private Context mContext;
    private int lastPosition = -1;

    private String beforeAcc;

    public Alarm_Adapter(ArrayList<AlarmDataModel> data, Context context) {
        super(context, R.layout.item_alarm, data);
        this.dataSet  = data;
        this.mContext = context;

        this.beforeAcc = "Account";

    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtaMsg;
        TextView txtaDate;
        TextView txtaZone;
        TextView txaAcc;
        ImageView ivaImage;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        Alarm_Adapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_alarm, parent,false);

            viewHolder = new Alarm_Adapter.ViewHolder();
            viewHolder.txtaMsg  = convertView.findViewById(R.id.alarm_Msg);
            viewHolder.txtaDate = convertView.findViewById(R.id.alarm_Date);
            viewHolder.txtaZone = convertView.findViewById(R.id.alarm_Zn);
            viewHolder.txaAcc = convertView.findViewById(R.id.alarm_account);
            viewHolder.ivaImage = convertView.findViewById(R.id.img_alarm_ic);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Alarm_Adapter.ViewHolder) convertView.getTag();
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        Animation animation = AnimationUtils.loadAnimation(mContext, (position%2 ==0) ? R.anim.right_in : R.anim.left_in);
        convertView.startAnimation(animation);

        viewHolder.txtaMsg.setText(dataSet.get(position).getaMsg());
        viewHolder.txtaZone.setText(dataSet.get(position).getaZone());
        viewHolder.txtaDate.setText(dataSet.get(position).getaDate());

        String a = dataSet.get(position).getaDesc();
        switch (a) {
            case "open":
                viewHolder.ivaImage.setImageResource(R.drawable.ic_disarmed_24);
                break;
            case "close":
                viewHolder.ivaImage.setImageResource(R.drawable.ic_armed_24);
                break;
            case "alarm":
                viewHolder.ivaImage.setImageResource(R.drawable.alarm_icon);
                break;
            default:
                viewHolder.ivaImage.setImageResource(R.drawable.ic_siren_24);
                break;
        }

        if(!dataSet.get(position).getaName().equals(beforeAcc) || position==0){
            viewHolder.txaAcc.setText(dataSet.get(position).getaName());
            viewHolder.txaAcc.setVisibility(View.VISIBLE);
        } else if (lastPosition != position){
            viewHolder.txaAcc.setVisibility(View.GONE);
        }

        beforeAcc = dataSet.get(position).getaName();
        lastPosition = position;
        // Return the completed view to render on screen
        return convertView;
    }
}
