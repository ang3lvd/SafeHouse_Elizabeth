package com.divinesecurity.safehouse.listViewAdapterPackage;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.divinesecurity.safehouse.R;

/**
 * Created by ang3l on 14/3/2018.
 */

public class Settings_Adapter extends BaseAdapter {
    private int mysIcons[];
    private String mysActions[];
    private LayoutInflater inflter;

    public Settings_Adapter(Context c, int[] icons, String[] actions){
        this.mysIcons = icons;
        this.mysActions = actions;
        inflter = (LayoutInflater.from(c));
    }

    @Override
    public int getCount() {
        return mysActions.length;
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

        ImageView mys_icon = view.findViewById(R.id.imgList_sttg);
        TextView mys_action = view.findViewById(R.id.actionList_sttg);

        mys_icon.setImageResource(mysIcons[i]);
        mys_action.setText(mysActions[i]);
        mys_action.setTextColor(Color.BLUE);

        return view;
    }
}
