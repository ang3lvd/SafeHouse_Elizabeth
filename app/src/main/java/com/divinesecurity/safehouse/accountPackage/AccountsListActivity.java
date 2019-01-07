package com.divinesecurity.safehouse.accountPackage;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.listViewAdapterPackage.Account_Adapter;
import com.divinesecurity.safehouse.toolsPackage.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AccountsListActivity extends AppCompatActivity {
    Toolbar toolbar;
    CoordinatorLayout rootLayoutAndroid;
    GridView gridView;

    private Account_Adapter adapter;

    Boolean appBarExpanded = true;
    private Menu collapsedMenu;

    public static ArrayList<String> gridViewStrings;
    public static ArrayList<Integer> gridViewIcons;

    FloatingActionButton fAb;
    int menuId;

    Dialog dialog;

    MyDataBaseAdapter dataBaseAdapter;

    int count = 0;
    ArrayList<Boolean> accSelect;
    ArrayList<String> list_items;

    String code, uname, upassw, uspassw, uemail, urole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

        toolbar = findViewById(R.id.toolbarid_switch);
        setSupportActionBar(toolbar);
        //this line shows back button
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridViewStrings = new ArrayList<>();
        gridViewIcons   = new ArrayList<>();

        accSelect  = new ArrayList<>();
        list_items = new ArrayList<>();

        initInstances();

        AppBarLayout appBarLayout = findViewById(R.id.appbar_switch);
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

        fAb = findViewById(R.id.fAB_switch);
        fAb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCode();
            }
        });

        // create a instance of SQLite Database
        dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        LoadAccountList();

        gridView = findViewById(R.id.accountGV_id);
        adapter = new Account_Adapter(getApplicationContext(), gridViewStrings, gridViewIcons, accSelect);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AccountsListActivity.this, gridViewStrings.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(Color.BLUE);// remove item from list
                return true;
            }
        });

        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked){
                    count++;
                    list_items.add(gridViewStrings.get(position));
                    accSelect.set(position, true);
                } else{
                    count--;
                    list_items.remove(gridViewStrings.get(position));
                    accSelect.set(position, false);
                }
                mode.setTitle(count + " items selected");
                //adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_context, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        for (String name : list_items) {
                            dataBaseAdapter.deleteEntry("acc",name);
                            int pos = gridViewStrings.indexOf(name);
                            gridViewStrings.remove(pos);
                            gridViewIcons.remove(pos);
                            accSelect.remove(pos);

                            dataBaseAdapter.deleteZonesFromAcc(name);
                            //                                  adapter.notifyDataSetChanged();
                        }

                        Toast.makeText(getApplicationContext(), count + " Accounts removed", Toast.LENGTH_SHORT).show();
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
                count = 0;
                for (int pos=0; pos<accSelect.size(); pos++) {
                    if (accSelect.get(pos)){
                        accSelect.set(pos, false);
                    }
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
            collapsedMenu.add("Add")
                    .setIcon(R.drawable.ic_account_add_black_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menuId = collapsedMenu.getItem(0).getItemId();
        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == menuId) {

            showDialogCode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void LoadAccountList(){
        Cursor cursor = dataBaseAdapter.getEntry_Acc_List();
        int cant = cursor.getCount();

        if (cant > 0) {
            gridViewStrings.clear();
            gridViewIcons.clear();
            accSelect.clear();
            while (cursor.moveToNext()) { //move for columns
                gridViewStrings.add(cursor.getString(cursor.getColumnIndex("ACCOUNTNAME")));
                gridViewIcons.add(R.mipmap.ic_account);
                accSelect.add(false);
            }
        }
        cursor.close();
        /*if (!is_GropuList_Empty()) {
            ListViewRefresh();
        }*/
    }

    private void initInstances() {
        rootLayoutAndroid = findViewById(R.id.coordinatorLayout_switch);
        CollapsingToolbarLayout collapsingToolbarLayoutAndroid = findViewById(R.id.colappsintoolbar_switch);
        collapsingToolbarLayoutAndroid.setTitle("Account Managment");
    }


    private void showDialogCode() {

        //dialog window
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_new_account);
        dialog.setTitle("Create New Account");
        // get the References of views
        final EditText txtcode  = dialog.findViewById(R.id.editCode_NA);
        final EditText txtname  = dialog.findViewById(R.id.editUser_NA);
        final Button btnNewACC = dialog.findViewById(R.id.btnnewAcc);

        // Set On ClickListener
        btnNewACC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if(txtcode.getText().toString().length() < 4){
                    txtcode.setError("Code is requiered!");
                    flag = false;
                }else if(txtname.getText().toString().length() < 2){
                    txtname.setError("Name is requiered!");
                    flag = false;
                }
                if(flag){
                    signupAcc(txtcode.getText().toString(), txtname.getText().toString());
                }

                dialog.dismiss();
            }
        });



        dialog.show();
    }

    public void signupAcc(String acc_code, String acc_name) {
        //GET TOKEN FROM FIREBASE
        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        String mToken = mypreference.getString("ptoken", "");

        code    = acc_code;
        uname   = acc_name;
        upassw  = "null";
        uspassw = "null";
        uemail  = "null";
        urole   = "Client";
        String data = null;
        try {
            data = URLEncoder.encode("c", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8") + "&" +
                    URLEncoder.encode("u", "UTF-8") + "=" + URLEncoder.encode(uname, "UTF-8") + "&" +
                    URLEncoder.encode("p", "UTF-8") + "=" + URLEncoder.encode(upassw, "UTF-8") + "&" +
                    URLEncoder.encode("sp", "UTF-8") + "=" + URLEncoder.encode(uspassw, "UTF-8") + "&" +
                    URLEncoder.encode("e", "UTF-8") + "=" + URLEncoder.encode(uemail, "UTF-8") + "&" +
                    URLEncoder.encode("r", "UTF-8") + "=" + URLEncoder.encode(urole, "UTF-8") + "&" +
                    URLEncoder.encode("t", "UTF-8") + "=" + URLEncoder.encode(mToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new UserRegister(this, data).execute(getResources().getString(R.string.url) + "uregisterapp.php?" + data);

    }


    private static class UserRegister extends AsyncTask<String, Void, String> {
        private WeakReference<AccountsListActivity> activityReference;
        String urlparameters;

        UserRegister(AccountsListActivity context, String postdata){
            this.activityReference = new WeakReference<>(context);
            this.urlparameters = postdata;
        }

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
            // get a reference to the activity if it is still there
            AccountsListActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            try {
                JSONObject jsonResult = new JSONObject(result);
                if(jsonResult.getBoolean("uregister")){

                    String accno = jsonResult.getString("accNo");

                    MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(activity.getApplicationContext());
                    dataBaseAdapter = dataBaseAdapter.open();
                    dataBaseAdapter.insertEntry_ACC(activity.uname, activity.upassw, activity.uspassw, activity.urole, activity.uemail, accno, "true");

                    activity.LoadAccountList();
                    activity.adapter.notifyDataSetChanged();
                    activity.gridView.setAdapter(activity.adapter);

                    //Get Zones Info
                    String data = null;
                    try {
                        data = URLEncoder.encode("a","UTF-8") + "=" + URLEncoder.encode(accno, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    new getZoneInfo(activity, data).execute(activity.getResources().getString(R.string.url) + "zoneinfo.php?"+data);

                    Toast.makeText(activity.getApplicationContext(), "Account registered successful!", Toast.LENGTH_LONG).show();
                    activity.LoadAccountList();

                } else {
                    if (jsonResult.getBoolean("codefaild")) {
                        Toast.makeText(activity.getApplicationContext(), "Code wrong!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "There is some problem with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "There is some error. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private static class getZoneInfo extends AsyncTask<String, Void, String> {
        private WeakReference<AccountsListActivity> activityReference;
        String urlparameters;

        getZoneInfo(AccountsListActivity context, String postdata){
            this.activityReference = new WeakReference<>(context);
            this.urlparameters = postdata;
        }

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
            // get a reference to the activity if it is still there
            AccountsListActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            try {
                //
                JSONObject jsonResult = new JSONObject(result);

                if(jsonResult.getBoolean("result")){

                    JSONArray jsonArray = jsonResult.getJSONArray("values");

                    JSONArray ijsonArray;
                    String[] zNo_Arr = new String[jsonArray.length()];
                    String[] zDesc_Arr = new String[jsonArray.length()];
                    for (int i=0; i<jsonArray.length(); i++){
                        ijsonArray = jsonArray.getJSONArray(i);
                        zNo_Arr[i] = ijsonArray.getString(0);
                        zDesc_Arr[i] = ijsonArray.getString(1);
                    }

                    MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(activity.getApplicationContext());
                    dataBaseAdapter = dataBaseAdapter.open();
                    if (zNo_Arr.length > 0){
                        for (int i = 0; i< zNo_Arr.length; i++){
                            dataBaseAdapter.insertEntry_Z(zNo_Arr[i], zDesc_Arr[i], activity.uname);
                        }
                    }
                }

                /*Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();*/

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "There is some problem with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
