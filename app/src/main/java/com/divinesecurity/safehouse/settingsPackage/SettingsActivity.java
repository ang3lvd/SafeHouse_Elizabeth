package com.divinesecurity.safehouse.settingsPackage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    ListView contList;
    String[] actionList = {"Account", "Notifications", "Invitation", "About and help", "Events"};
    int[] iconList = {R.mipmap.ic_account, R.drawable.ic_notifications, R.mipmap.ic_group, R.mipmap.ic_help, R.mipmap.ic_events};

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
                        //showEventPayDialog();
                        showDialogEvent();
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

        //dialog window
        final Dialog dialogEvent = new Dialog(this);
        dialogEvent.setContentView(R.layout.dialog_event);
        dialogEvent.setTitle("Set Notification");
        // get the References of views
        final CheckBox chkbAlarm = dialogEvent.findViewById(R.id.checkbox_Alarm);
        final CheckBox chkbOpCl  = dialogEvent.findViewById(R.id.checkbox_OpCl);
        final CheckBox chkbEvent = dialogEvent.findViewById(R.id.checkbox_Event);

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        //final String accno  = mypreference.getString("paccountNo", "");
        //final String addadmin  = mypreference.getString("padmin", "");
        chkbAlarm.setChecked(mypreference.getString("evAlarm", "").equals("true"));
        chkbOpCl.setChecked(mypreference.getString("evOpenClose", "").equals("true"));
        chkbEvent.setChecked(mypreference.getString("evEvent", "").equals("true"));

        final Button btndefault = dialogEvent.findViewById(R.id.btndefaultPrioEvent);
        btndefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkbAlarm.setChecked(true);
                chkbOpCl.setChecked(true);
                chkbEvent.setChecked(false);
            }
        });

        final Button btnsave = dialogEvent.findViewById(R.id.btnsavePrioEvent);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mypreferences = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mypreferences.edit();
                editor.putString("evAlarm",     chkbAlarm.isChecked()? "true" : "false");
                editor.putString("evOpenClose", chkbOpCl.isChecked()? "true" : "false");
                editor.putString("evEvent",     chkbEvent.isChecked()? "true" : "false");
                editor.apply();

                dialogEvent.dismiss();

                Toast.makeText(SettingsActivity.this, "saved..", Toast.LENGTH_SHORT).show();
            }
        });

        dialogEvent.show();
    }

    Button btnSetCode;
    String uid;
    private void showDialogCode() {

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        //final String accno  = mypreference.getString("paccountNo", "");
        final String addadmin  = mypreference.getString("padmin", "");

        //dialog window
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_guestcode);
        dialog.setTitle("Create New Account");

        // get the References of views
        final Spinner spnacc = dialog.findViewById(R.id.spinner_Account);
        //final com.bachors.prefixinput.EditText txtcode  = dialog.findViewById(R.id.txtGuestCode);
        final Spinner spnnrole = dialog.findViewById(R.id.spinner_Role);
        btnSetCode = dialog.findViewById(R.id.btnsetCode);

        final TextView tv_code = dialog.findViewById(R.id.tv_code);
        final EditText et_code1 = dialog.findViewById(R.id.et_code1);
        final EditText et_code2 = dialog.findViewById(R.id.et_code2);
        final EditText et_code3 = dialog.findViewById(R.id.et_code3);
        final EditText et_code4 = dialog.findViewById(R.id.et_code4);
        final EditText et_code5 = dialog.findViewById(R.id.et_code5);
        final EditText et_code6 = dialog.findViewById(R.id.et_code6);

        final EditText et_code7  = dialog.findViewById(R.id.et_code7);
        final EditText et_code8  = dialog.findViewById(R.id.et_code8);
        final EditText et_code9  = dialog.findViewById(R.id.et_code9);
        final EditText et_code10 = dialog.findViewById(R.id.et_code10);
        final EditText et_code11 = dialog.findViewById(R.id.et_code11);
        final EditText et_code12 = dialog.findViewById(R.id.et_code12);

        et_code7.requestFocus();
        et_code7.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code7.setText(Character.toString(character));
                    } else {
                        et_code7.setTextColor(Color.WHITE);
                        et_code7.setBackgroundResource(R.drawable.code_circle);
                    }
                } else{
                    editedFlag = false;
                    et_code7.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code7.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code8.requestFocus();
                }
            }
        });
        et_code8.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editedFlag) {
                    if (editable.length() >= 1) {
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code8.setText(Character.toString(character));
                    } else {
                        et_code8.setTextColor(Color.WHITE);
                        et_code8.setBackgroundResource(R.drawable.code_circle);
                        et_code7.requestFocus();
                    }
                } else {
                    editedFlag = false;
                    et_code8.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code8.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code9.requestFocus();
                }
            }
        });
        et_code9.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code9.setText(Character.toString(character));
                    } else {
                        et_code9.setTextColor(Color.WHITE);
                        et_code9.setBackgroundResource(R.drawable.code_circle);
                        et_code8.requestFocus();
                    }
                } else{
                    editedFlag = false;
                    et_code9.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code9.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code10.requestFocus();
                }
            }
        });
        et_code10.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code10.setText(Character.toString(character));
                    } else {
                        et_code10.setTextColor(Color.WHITE);
                        et_code10.setBackgroundResource(R.drawable.code_circle);
                        et_code9.requestFocus();
                    }
                } else{
                    editedFlag = false;
                    et_code10.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code10.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code11.requestFocus();
                }
            }
        });
        et_code11.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code11.setText(Character.toString(character));
                    } else {
                        et_code11.setTextColor(Color.WHITE);
                        et_code11.setBackgroundResource(R.drawable.code_circle);
                        et_code10.requestFocus();
                    }
                } else{
                    editedFlag = false;
                    et_code11.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code11.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code12.requestFocus();
                }
            }
        });
        et_code12.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code12.setText(Character.toString(character));
                    } else {
                        et_code12.setTextColor(Color.WHITE);
                        et_code12.setBackgroundResource(R.drawable.code_circle);
                        et_code12.requestFocus();
                    }
                } else{
                    editedFlag = false;
                    et_code12.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code12.setBackgroundResource(R.drawable.code_circle_selected);
                }
            }
        });

        LoadAcc();
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_text_acc, accList);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdow);
        spnacc.setAdapter(langAdapter);

        //txtcode.setPrefix(accno.get(0) + "-");
        if(accno.get(0).length() > 0 && accno.get(0).length() <= 6){
            //String p6_uid = String.format("%06d", Integer.parseInt(uid));
            uid = accno.get(0);
            List et_codeList = new ArrayList<EditText>(){{add(et_code6); add(et_code5); add(et_code4); add(et_code3); add(et_code2); add(et_code1);}};

            for (int i = accno.get(0).length()-1; i >= 0; i--) {
                ((EditText)et_codeList.get(i)).setText(accno.get(0).charAt(i) + "");
            }
        } else {
            Toast.makeText(getApplicationContext(), "There was a problem getting the User Reference Number", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

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
                //txtcode.setPrefix(accno.get(position) + "-");
                if(accno.get(position).length() > 0 && accno.get(position).length() <= 6){
                    //String p6_uid = String.format("%06d", Integer.parseInt(uid));
                    uid = accno.get(position);
                    List et_codeList = new ArrayList<EditText>(){{add(et_code6); add(et_code5); add(et_code4); add(et_code3); add(et_code2); add(et_code1);}};

                    for (int i = accno.get(position).length()-1; i >= 0; i--) {
                        ((EditText)et_codeList.get(i)).setText(accno.get(position).charAt(i) + "");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "There was a problem getting the User Reference Number", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
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
                    //String code = txtcode.getText().toString();
                    String code = uid +"-"+ et_code7.getText().toString() + et_code8.getText().toString() +
                            et_code9.getText().toString() + et_code10.getText().toString() +
                            et_code11.getText().toString() + et_code12.getText().toString();
                    if (code.length() != 6) {
                        tv_code.setError("Code in correct.");
                    } else {
                        btnSetCode.setText("Setting up ...");
                    }
                    String data = null;
                    try {
                        data = URLEncoder.encode("c","UTF-8") + "=" + URLEncoder.encode(code, "UTF-8") + "&" +
                                URLEncoder.encode("r","UTF-8") + "=" + URLEncoder.encode(selectRole, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    new SettingsActivity.UpdateCode(SettingsActivity.this, data).execute(getResources().getString(R.string.dssoft_url) + "giams/includes/SafeHome/setguestcode.php?"+data);
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
        private WeakReference<SettingsActivity>
 activityReference;
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
                    activity.btnSetCode.setText("Set up");
                    if (jsonResult.getString("cause").equals("cantidad")){
                        Toast.makeText(activity.getApplicationContext(), "You can't add more guest. Please, for more, contact us.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "Would you mind try again in a few minutes?", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                activity.btnSetCode.setText("Set up");
                Toast.makeText(activity.getApplicationContext(), "There is some problem with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();

                activity.dialog.dismiss();
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
