package com.divinesecurity.safehouse.alarmPackage;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.listViewAdapterPackage.Alarm_Adapter;
import com.divinesecurity.safehouse.toolsPackage.Tools;
import com.divinesecurity.safehouse.utils.CustomListView;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    ArrayList<AlarmDataModel> dataModels;
    CustomListView listView;
    private Alarm_Adapter adapter;

    MyDataBaseAdapter dataBaseAdapter;

    Boolean appBarExpanded = true;
    private Menu collapsedMenu;
    int menuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbaridEvent);
        setSupportActionBar(toolbar);
        //this line shows back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.list_alarm);
        dataModels = new ArrayList<>();

        // create a instance of SQLite Database
        dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        LoadChatGroupList();

        adapter = new Alarm_Adapter(dataModels,getApplicationContext());
        listView.setAdapter(adapter);

        Tools.setScreen_active("eventscreen");

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mypreference.edit();
        editor.putString("palarmbadge", "0");
        editor.apply();

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("broad-alarm"));

        AppBarLayout appBarLayout = findViewById(R.id.appbar_event);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.switch_menu, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null
                && (!appBarExpanded)) {
            //collapsed
            collapsedMenu.add("Clear Events")
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menuId = collapsedMenu.getItem(0).getItemId();
        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == menuId) {
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(EventListActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(EventListActivity.this);
            }
            builder.setTitle("Delete entry")
                    .setMessage("Are you sure you want to clear the events table?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // create a instance of SQLite Database
                            dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
                            dataBaseAdapter = dataBaseAdapter.open();
                            dataBaseAdapter.deleteEntry("a", null);

                            LoadChatGroupList();
                            adapter.notifyDataSetChanged();
                            listView.setAdapter(adapter);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoadChatGroupList(){
        Cursor cursor = dataBaseAdapter.getEntry_A_List();
        int cant = cursor.getCount();
        dataModels.clear();
        AlarmDataModel alarmDataModel;
        if (cant > 0) {
            while (cursor.moveToNext()) { //move for columns
                //cursor.moveToFirst();
                alarmDataModel = new AlarmDataModel(cursor.getString(cursor.getColumnIndex("AMSG")),
                                                    cursor.getString(cursor.getColumnIndex("ADATE")),
                                                    cursor.getString(cursor.getColumnIndex("ACCOUNTNAME")),
                                                    cursor.getString(cursor.getColumnIndex("AZONE")),
                                                    cursor.getString(cursor.getColumnIndex("ADESC")));
                dataModels.add(alarmDataModel);
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
            LoadChatGroupList();
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
    };
}
