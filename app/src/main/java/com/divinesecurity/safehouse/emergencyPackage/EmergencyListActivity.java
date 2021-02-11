package com.divinesecurity.safehouse.emergencyPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.divinesecurity.safehouse.MyMapActivity;
import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.listViewAdapterPackage.Emergency_Adapter;
import com.divinesecurity.safehouse.toolsPackage.Tools;
import com.divinesecurity.safehouse.utils.CustomListView;

import java.util.ArrayList;
import java.util.Objects;

public class EmergencyListActivity extends AppCompatActivity {

    ArrayList<EmergencyDataModel> dataModels;
    CustomListView listView;
    private Emergency_Adapter adapter;

    MyDataBaseAdapter dataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_list);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarid_emergency);
        setSupportActionBar(toolbar);
        //this line shows back button
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.list_emergency);
        dataModels = new ArrayList<>();

        // create a instance of SQLite Database
        dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        LoadEmergencyList();

        adapter = new Emergency_Adapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent mapIntent = new Intent(EmergencyListActivity.this, MyMapActivity.class);
                mapIntent.putExtra("contName", Objects.requireNonNull(adapter.getItem(position)).getEName());
                mapIntent.putExtra("contLat", Objects.requireNonNull(adapter.getItem(position)).getELatitude());
                mapIntent.putExtra("contLong", Objects.requireNonNull(adapter.getItem(position)).getELongitude());
                mapIntent.putExtra("contDir", Objects.requireNonNull(adapter.getItem(position)).getEAddress());
                mapIntent.putExtra("group", Objects.requireNonNull(adapter.getItem(position)).getEAcc());
                //mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mapIntent);
            }
        });

        Tools.setScreen_active("emergencyscreen");

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mypreference.edit();
        editor.putString("pemergbadge", "0");
        editor.apply();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("broad-emergency"));
    }

    /*public void onEmergencyClick(int position) {
        adapter.getItem(position).getEAcc();
    }*/

    public void LoadEmergencyList(){
        Cursor cursor = dataBaseAdapter.getEntry_E_List();
        int cant = cursor.getCount();

        EmergencyDataModel emergencyDataModel;
        if (cant > 0) {
            while (cursor.moveToNext()) { //move for columns
                //cursor.moveToFirst();
                emergencyDataModel = new EmergencyDataModel(cursor.getString(cursor.getColumnIndex("ENAME")),
                        cursor.getString(cursor.getColumnIndex("ESTATUS")).equals("1"),
                        cursor.getString(cursor.getColumnIndex("EDATE")),
                        cursor.getString(cursor.getColumnIndex("EADDRESS")),
                        cursor.getString(cursor.getColumnIndex("ELATITUDE")),
                        cursor.getString(cursor.getColumnIndex("ELONGITUDE")),
                        cursor.getString(cursor.getColumnIndex("ENAME")),
                        cursor.getString(cursor.getColumnIndex("IDE")));
                dataModels.add(emergencyDataModel);
            }
        }
        cursor.close();
        /*if (!is_GropuList_Empty()) {
            ListViewRefresh();
        }*/
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoadEmergencyList();
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
    };
}