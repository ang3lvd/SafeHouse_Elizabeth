package com.divinesecurity.safehouse.listViewAdapterPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.divinesecurity.safehouse.R;

import java.util.ArrayList;

/**
 * Created by ang3l on 7/8/2018.
 */

public class Account_Adapter extends BaseAdapter {

    private Context mContext;
    private final ArrayList<String> mysName;
    private final ArrayList<Integer> mysIcon;
    private ArrayList<Boolean> myaccSelects;

    private int lastPosition = -1;


    public Account_Adapter(Context c, ArrayList<String> n, ArrayList<Integer> i, ArrayList<Boolean> s) {
        mContext = c;
        this.mysName = n;
        this.mysIcon = i;
        this.myaccSelects = s;
    }

    @Override
    public int getCount() {
        return mysName.size();
    }

    @Override
    public Object getItem(int p) {
        return null;
    }

    @Override
    public long getItemId(int p) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid = null;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            if (inflater != null) {
                grid = inflater.inflate(R.layout.item_account, null);

                TextView textView = grid.findViewById(R.id.account_name);
                ImageView imageView = grid.findViewById(R.id.account_img);
                LinearLayout llayoutView = grid.findViewById(R.id.ly_ia_id);
                textView.setText(mysName.get(position));
                imageView.setImageResource(mysIcon.get(position));

                if(myaccSelects.get(position)){
                    llayoutView.setBackgroundResource(R.color.GuestListcolorPrimary);
                } else{
                    llayoutView.setBackgroundResource(android.R.color.transparent);
                }
            }
        } else {
            grid = convertView;
        }


        if(!myaccSelects.contains(true)){
            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            if(grid != null)
                grid.startAnimation(animation);
            lastPosition = position;
        }


        return grid;
    }

}




   /* //private int mysIcon;
    private String mysName[];
    private LayoutInflater inflter;
    private Context mContext;
    private int lastPosition = -1;


    public Account_Adapter(Context c, String[] actions){
        //this.mysIcon = icons;
        this.mContext = c;
        this.mysName = actions;
        inflter = (LayoutInflater.from(c));
    }

    @Override
    public int getCount() {
        return mysName.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_settings, null);

        //ImageView mys_icon = view.findViewById(R.id.account_img);
        TextView txtName = view.findViewById(R.id.account_name);

        //mys_icon.setImageResource(mysIcon);
        txtName.setText(mysName[i]);
        txtName.setTextColor(Color.BLUE);

        Animation animation = AnimationUtils.loadAnimation(mContext, (i > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        view.startAnimation(animation);
        lastPosition = i;

        return view;
    }

}

/*public class Account_Adapter extends ArrayAdapter<AccountDataModel> {

    private ArrayList<AccountDataModel> dataSet;
    private Context mContext;
    private int lastPosition = -1;


    public Account_Adapter(@NonNull Context context, @NonNull ArrayList<AccountDataModel> data) {
        super(context, R.layout.item_account, data);
        this.dataSet = data;
        this.mContext = context;
    }


    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        Account_Adapter.ViewHolder viewHolder = null; // view lookup cache stored in tag

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_account, parent,false);

            viewHolder = new Account_Adapter.ViewHolder();
            viewHolder.txtName  = convertView.findViewById(R.id.account_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Account_Adapter.ViewHolder) convertView.getTag();
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataSet.get(position).getName());
        // Return the completed view to render on screen
        return convertView;
    }
}*/
