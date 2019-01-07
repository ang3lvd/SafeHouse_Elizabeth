package com.divinesecurity.safehouse.zonePackage;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.listViewAdapterPackage.Zone_Adapter;
import com.divinesecurity.safehouse.utils.CustomListView;

import java.util.ArrayList;

public class ZoneListActivity extends AppCompatActivity {

    ArrayList<ZoneDataModel> dataModels;
    CustomListView listView;

    MyDataBaseAdapter dataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbaridZone);
        setSupportActionBar(toolbar);
        //this line shows back button
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.list_zone);
        dataModels = new ArrayList<>();

        // create a instance of SQLite Database
        dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        LoadChatGroupList();

        Zone_Adapter adapter = new Zone_Adapter(dataModels, getApplicationContext());
        listView.setAdapter(adapter);
    }

    public void LoadChatGroupList(){
        Cursor cursor = dataBaseAdapter.getEntry_Z_List();
        int cant = cursor.getCount();

        ZoneDataModel zoneDataModel;
        if (cant > 0) {
            while (cursor.moveToNext()) { //move for columns
                //cursor.moveToFirst();
                zoneDataModel = new ZoneDataModel(cursor.getString(cursor.getColumnIndex("ZNO")),
                                                  cursor.getString(cursor.getColumnIndex("ZDESC")),
                                                  cursor.getString(cursor.getColumnIndex("ACCOUNTNAME")));
                dataModels.add(zoneDataModel);
            }
        }
        cursor.close();
        /*if (!is_GropuList_Empty()) {
            ListViewRefresh();
        }*/
    }
}
