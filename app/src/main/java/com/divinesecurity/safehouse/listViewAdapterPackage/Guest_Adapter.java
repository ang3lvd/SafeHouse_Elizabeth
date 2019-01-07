package com.divinesecurity.safehouse.listViewAdapterPackage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.guestPackage.GuestDataModel;

import java.util.ArrayList;

/**
 * Created by ang3l on 6/3/2018.
 */

public class Guest_Adapter extends ArrayAdapter<GuestDataModel> {

    private ArrayList<GuestDataModel> dataSet;
    private Context mContext;
    private int lastPosition = -1;

    public Guest_Adapter(ArrayList<GuestDataModel> data, Context context) {
        super(context, R.layout.item_guest, data);
        this.dataSet  = data;
        this.mContext = context;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtgName;
        TextView txtgRole;
        RelativeLayout rlcontent;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        Guest_Adapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_guest, parent,false);

            viewHolder = new Guest_Adapter.ViewHolder();
            viewHolder.txtgName  = convertView.findViewById(R.id.guest_Name);
            viewHolder.txtgRole  = convertView.findViewById(R.id.guest_Role);
            viewHolder.rlcontent  = convertView.findViewById(R.id.rly_content_guest_item);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Guest_Adapter.ViewHolder) convertView.getTag();
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtgName.setText(dataSet.get(position).getgName());
        viewHolder.txtgRole.setText(dataSet.get(position).getgRole());
        if (dataSet.get(position).getgSelect()){
            viewHolder.rlcontent.setAlpha(0.5f);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
