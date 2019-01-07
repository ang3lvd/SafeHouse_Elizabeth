package com.divinesecurity.safehouse.settingsPackage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.listViewAdapterPackage.Settings_Adapter;
import com.divinesecurity.safehouse.toolsPackage.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    ListView contList;
    String actionList[] = {"Account", "Notifications", "Invitation", "About and help", "Events"};
    int iconList[] = {R.mipmap.ic_account, R.drawable.ic_notifications, R.mipmap.ic_group, R.mipmap.ic_help, R.mipmap.ic_events};

    Dialog dialog;
    String selectRole = "Guest", urole;

    String userNname;

    ArrayList<String> accno, accList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        contList = findViewById(R.id.listVw_settings);
        Settings_Adapter customAdapter = new Settings_Adapter(getApplicationContext(), iconList, actionList);
        contList.setAdapter(customAdapter);

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        urole  = mypreference.getString("prole", "");

        contList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String select_action = ((TextView) view.findViewById(R.id.actionList_sttg)).getText().toString();
                Intent settings_sel;
                switch (select_action) {
                    case "Account":
                        showDialogAccount();
                        break;
                    case "Notifications":
                        settings_sel = new Intent(getApplicationContext(), NotificationSttngActivity.class);
                        startActivity(settings_sel);
                        break;
                    case "Invitation":
                        if (urole.equals("Client") || urole.equals("Admin")) {
                            showDialogCode();
                        } else {
                            Toast.makeText(getApplicationContext(), "This feature is only for client...", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Events":
                        showEventPayDialog();
                        break;
                    case "About and help":
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://divinesecurityapp.com/privacy_policy.html"));
                        startActivity(intent);
                        break;
                }
            }
        });

        accno = new ArrayList<>();
        accList = new ArrayList<>();
    }

    private void showEventPayDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("When you change the default selection you will be billed with 2.99 EC by month");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                showDialogEvent();
            }
        });
        alertDialogBuilder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDialogEvent() {

        //SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        //final String accno  = mypreference.getString("paccountNo", "");
        //final String addadmin  = mypreference.getString("padmin", "");

        //dialog window
        Dialog dialogEvent = new Dialog(this);
        dialogEvent.setContentView(R.layout.dialog_event);
        dialogEvent.setTitle("Set Invitation Code");
        // get the References of views
        final CheckBox chkbHigh    = dialogEvent.findViewById(R.id.checkbox_High);
        final CheckBox chkbMedium  = dialogEvent.findViewById(R.id.checkbox_Medium);
        final CheckBox chkbLow     = dialogEvent.findViewById(R.id.checkbox_Low);

        final boolean[] chkarr = new boolean[3];
        chkarr[0] = chkbHigh.isChecked();
        chkarr[1] = chkbMedium.isChecked();
        chkarr[2] = chkbLow.isChecked();

        chkbHigh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                chkarr[0] = chkbHigh.isChecked();
            }
        });

        chkbMedium.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                chkarr[0] = chkbMedium.isChecked();
            }
        });

        chkbLow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                chkarr[0] = chkbLow.isChecked();
            }
        });

        final Button btndefault = dialogEvent.findViewById(R.id.btndefaultPrioEvent);
        btndefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkbHigh.setChecked(true);
                chkbMedium.setChecked(false);
                chkbLow.setChecked(false);

                chkarr[0] = chkbHigh.isChecked();
                chkarr[1] = chkbMedium.isChecked();
                chkarr[2] = chkbLow.isChecked();
            }
        });

        final Button btnsave = dialogEvent.findViewById(R.id.btnsavePrioEvent);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "saveing..", Toast.LENGTH_SHORT).show();
            }
        });

        dialogEvent.show();
    }

    private void showDialogCode() {

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        //final String accno  = mypreference.getString("paccountNo", "");
        final String addadmin  = mypreference.getString("padmin", "");

        //dialog window
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_guestcode);
        dialog.setTitle("Set Invitation Code");
        // get the References of views
        final Spinner spnacc = dialog.findViewById(R.id.spinner_Account);
        final com.bachors.prefixinput.EditText txtcode  = dialog.findViewById(R.id.txtGuestCode);
        final Spinner spnnrole = dialog.findViewById(R.id.spinner_Role);
        final Button btnSetCode = dialog.findViewById(R.id.btnsetCode);

        LoadAcc();
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_text_acc, accList);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdow);
        spnacc.setAdapter(langAdapter);
        txtcode.setPrefix(accno.get(0) + "-");

        spnnrole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    selectRole = "Admin";
                } else if(position == 1){
                    selectRole = "Guest";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set On ClickListener
        spnacc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtcode.setPrefix(accno.get(position) + "-");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnSetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the code
                if (selectRole.equals("Admin") && addadmin.equals("false")){
                    Toast.makeText(SettingsActivity.this, "You can't add more admin guest. Please contact us for more.", Toast.LENGTH_SHORT).show();
                } else if (selectRole.equals("Guest") || (selectRole.equals("Admin") && addadmin.equals("true"))){
                    String code = txtcode.getText().toString();
                    String data = null;
                    try {
                        data = URLEncoder.encode("c","UTF-8") + "=" + URLEncoder.encode(code, "UTF-8") + "&" +
                                URLEncoder.encode("r","UTF-8") + "=" + URLEncoder.encode(selectRole, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    new SettingsActivity.UpdateCode(SettingsActivity.this, data).execute(getResources().getString(R.string.url) + "setguestcode.php?"+data);
                }
            }
        });



        dialog.show();
    }

    private void showDialogAccount() {

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        final String accno  = mypreference.getString("paccountNo", "");

        //dialog window
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sett_account);
        // get the References of views
        final EditText txtname    = dialog.findViewById(R.id.etName);
        final EditText txtpassw   = dialog.findViewById(R.id.etPassword);
        final EditText txtnname   = dialog.findViewById(R.id.etNewName);
        final EditText txtnpassw  = dialog.findViewById(R.id.etNewPassword);
        final EditText txtnspassw = dialog.findViewById(R.id.etNewSPassword);
        final CheckBox cBname     = dialog.findViewById(R.id.ckBname);
        final CheckBox cBpassw    = dialog.findViewById(R.id.ckBpassw);
        final CheckBox cBspassw   = dialog.findViewById(R.id.ckBspassw);
        Button btnok = dialog.findViewById(R.id.btn_UpdAccOK);
        Button btncancel = dialog.findViewById(R.id.btn_UpdAccCANCEL);


        cBname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txtnname.setEnabled(isChecked);
            }
        });

        cBpassw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txtnpassw.setEnabled(isChecked);
            }
        });

        cBspassw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txtnspassw.setEnabled(isChecked);
            }
        });

        // Set On ClickListener
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the info
                SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                userNname           = mypreference.getString("puser", "");
                String userNpassw   = mypreference.getString("ppassw", "");
                String usersNpassw  = mypreference.getString("pspassw", "");
                //String role        = mypreference.getString("prole", "");

                String username     = txtname.getText().toString();
                String userpassw    = txtpassw.getText().toString();
                if (cBname.isChecked()){
                    userNname    = txtnname.getText().toString();
                }
                if (cBpassw.isChecked()){
                    userNpassw   = txtnpassw.getText().toString();
                }
                if (cBspassw.isChecked()){
                    usersNpassw  = txtnspassw.getText().toString();
                }

                String data = null;
                try {
                    data = URLEncoder.encode("n","UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                            URLEncoder.encode("p","UTF-8") + "=" + URLEncoder.encode(userpassw, "UTF-8") + "&" +
                            URLEncoder.encode("nn","UTF-8") + "=" + URLEncoder.encode(userNname, "UTF-8") + "&" +
                            URLEncoder.encode("np","UTF-8") + "=" + URLEncoder.encode(userNpassw, "UTF-8") + "&" +
                            URLEncoder.encode("nsp","UTF-8") + "=" + URLEncoder.encode(usersNpassw, "UTF-8") + "&" +
                            URLEncoder.encode("a","UTF-8") + "=" + URLEncoder.encode(accno, "UTF-8")  + "&" +
                            URLEncoder.encode("r","UTF-8") + "=" + URLEncoder.encode(urole, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                new SettingsActivity.UpdateInfo(SettingsActivity.this, data).execute(getResources().getString(R.string.url) + "updateuserinfo.php?"+data);

            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void LoadAcc(){
        MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        Cursor cursor = dataBaseAdapter.getEntry_AllAcc_List();
        int cant = cursor.getCount();

        if (cant > 0) {
            while (cursor.moveToNext()) { //move for columns
                //cursor.moveToFirst();
                accList.add(cursor.getString(cursor.getColumnIndex("ACCOUNTNAME")));
                accno.add(cursor.getString(cursor.getColumnIndex("ACCNO")));
            }
        }
        cursor.close();
    }

    private static class UpdateCode extends AsyncTask<String, Void, String> {
        private WeakReference<SettingsActivity> activityReference;
        String urlparameters;

        UpdateCode(SettingsActivity context, String postdata){
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
            SettingsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            try {
                //
                JSONObject jsonResult = new JSONObject(result);

                if(jsonResult.getBoolean("codeupdate")){
                    activity.dialog.dismiss();
                    Toast.makeText(activity.getApplicationContext(), "Code set succesfully.", Toast.LENGTH_LONG).show();

                } else {
                    if (jsonResult.getString("cause").equals("cantidad")){
                        Toast.makeText(activity.getApplicationContext(), "You can't add more guest. Please, for more, contact us.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "Would you mind try again in a few minutes?", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "There is some problem with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class UpdateInfo extends AsyncTask<String, Void, String> {
        private WeakReference<SettingsActivity> activityReference;
        String urlparameters;

        UpdateInfo(SettingsActivity context, String postdata){
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
            SettingsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            try {
                //
                JSONObject jsonResult = new JSONObject(result);

                if(jsonResult.getBoolean("infoupdate")){
                    activity.dialog.dismiss();
                    Toast.makeText(activity.getApplicationContext(), "Info update succesfully.", Toast.LENGTH_LONG).show();
                    SharedPreferences mypreferences = activity.getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mypreferences.edit();
                    editor.putString("puser", activity.userNname);
                    editor.apply();
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Would you mind try again in a few minutes?", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "There is some problem with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
