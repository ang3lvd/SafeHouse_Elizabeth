package com.divinesecurity.safehouse.listViewAdapterPackage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.zonePackage.ZoneDataModel;

import java.util.ArrayList;

/**
 * Created by ang3l on 7/3/2018.
 */

public class Zone_Adapter  extends ArrayAdapter<ZoneDataModel> {

    private ArrayList<ZoneDataModel> dataSet;
    private Context mContext;
    //private int lastPosition = -1;

    private String beforeAcc;

    public Zone_Adapter(ArrayList<ZoneDataModel> data, Context context) {
        super(context, R.layout.item_alarm, data);
        this.dataSet  = data;
        this.mContext = context;

        this.beforeAcc = "nullnull_ds";
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtaZone;
        TextView txtaDesc;
        TextView zone_account;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        Zone_Adapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_zone, parent,false);

            viewHolder = new Zone_Adapter.ViewHolder();
            viewHolder.txtaZone  = convertView.findViewById(R.id.zone_No);
            viewHolder.txtaDesc = convertView.findViewById(R.id.zone_Desc);
            viewHolder.zone_account   = convertView.findViewById(R.id.zone_account);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Zone_Adapter.ViewHolder) convertView.getTag();
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position%2 ==0) ? R.anim.right_in : R.anim.left_in);
        convertView.startAnimation(animation);
        //lastPosition = position;

        viewHolder.txtaZone.setText("Zone " + dataSet.get(position).getNo());
        viewHolder.txtaDesc.setText(dataSet.get(position).getDesc());

        if(!dataSet.get(position).getAcc().equals(beforeAcc)){
            viewHolder.zone_account.setText(dataSet.get(position).getAcc());
            viewHolder.zone_account.setVisibility(View.VISIBLE);
        } else {
            viewHolder.zone_account.setVisibility(View.GONE);
        }
        beforeAcc = dataSet.get(position).getAcc();

        // Return the completed view to render on screen
        return convertView;
    }
}


