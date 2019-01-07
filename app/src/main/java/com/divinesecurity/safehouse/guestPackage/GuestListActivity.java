package com.divinesecurity.safehouse.guestPackage;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Toast;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.listViewAdapterPackage.Guest_Adapter;
import com.divinesecurity.safehouse.toolsPackage.Tools;
import com.divinesecurity.safehouse.utils.CustomListView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GuestListActivity extends AppCompatActivity {

    ArrayList<GuestDataModel> dataModels;

    CustomListView listView;
    Guest_Adapter adapter;

    MyDataBaseAdapter dataBaseAdapter;

    ArrayList<String> list_items;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbarid_guest);
        setSupportActionBar(toolbar);
        //this line shows back button
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.list_guest);
        dataModels = new ArrayList<>();

        // create a instance of SQLite Database
        dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        list_items = new ArrayList<>();

        LoadChatGroupList();

        adapter = new Guest_Adapter(dataModels, getApplicationContext());
        listView.setAdapter(adapter);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked){
                    count++;
                    //list_items.add(dataModels.get(position).getgName());
                    dataModels.get(position).setgSelect(true);
                } else{
                    count--;
                    //list_items.remove(dataModels.get(position).getgName());
                    dataModels.get(position).setgSelect(false);
                }
                mode.setTitle(count + " guests selected");
                adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.guest_select_menu, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_guest:
                        //metodoDelete()
                        String list_name = "";
                        int cant = 0;
                        for (GuestDataModel guest : dataModels) {
                            if (guest.getgSelect()){
                                list_name += guest.getgName() + ",";
                                cant ++;

                                dataBaseAdapter.deleteEntry("g",guest.getgName());
                                dataModels.remove(guest);
                            }
                        }
                        if (cant >  0){
                            if (list_name.charAt(list_name.length()-1) == ','){
                                list_name = list_name.substring(0,list_name.length()-1);
                            }
                            SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                            String accno = mypreference.getString("paccountNo", "");
                            try {
                                String data = URLEncoder.encode("a", "UTF-8") + "=" + URLEncoder.encode(accno, "UTF-8") + "&" +
                                        URLEncoder.encode("ps", "UTF-8") + "=" + URLEncoder.encode(list_name, "UTF-8");
                                new DelectGuest().execute(getApplicationContext().getResources().getString(R.string.url) + "guestdelete.php?" + data);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                //Toast.makeText(getApplicationContext(), "Opss, incompenssible mistake...", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!dataModels.isEmpty()) {
                            listView.setAdapter(adapter);
                        }
                        Toast.makeText(getApplicationContext(), count + " Items removed", Toast.LENGTH_SHORT).show();
                        list_items.clear();
                        count = 0;
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                for (GuestDataModel guest : dataModels) {
                    guest.setgSelect(false);
                }
                listView.setAdapter(adapter);
                count = 0;
            }
        });
    }


    public void LoadChatGroupList(){
        Cursor cursor = dataBaseAdapter.getEntry_G_List();
        int cant = cursor.getCount();

        GuestDataModel guestDataModel;
        if (cant > 0) {
            while (cursor.moveToNext()) { //move for columns
                //cursor.moveToFirst();
                guestDataModel = new GuestDataModel(cursor.getString(cursor.getColumnIndex("IDG")),
                        cursor.getString(cursor.getColumnIndex("GUESTNAME")),
                        cursor.getString(cursor.getColumnIndex("GUESTROLE")),
                        cursor.getString(cursor.getColumnIndex("ACCOUNTNAME")),
                        false);
                dataModels.add(guestDataModel);
            }
        }
        cursor.close();
        /*if (!is_GropuList_Empty()) {
            ListViewRefresh();
        }*/
    }


    private static class DelectGuest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return Tools.downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

        }
    }

}
