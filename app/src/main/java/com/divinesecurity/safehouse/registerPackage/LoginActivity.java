package com.divinesecurity.safehouse.registerPackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.divinesecurity.safehouse.MainActivity;
import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.alarmPackage.EventListActivity;
import com.divinesecurity.safehouse.invoicePackage.InvoiceListActivity;
import com.divinesecurity.safehouse.toolsPackage.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    String userstate, accno, role;
    String fromNotf, finalsecreen;

    EditText etPass;
    boolean passwVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        userstate = mypreference.getString("puserstatus", "");
        accno     = mypreference.getString("paccountNo", "");
        role      = mypreference.getString("prole", "");

        //define if come from notification or back activity
        Intent beforeActivity = getIntent();
        fromNotf = "none";
        if (beforeActivity != null) {
            //finalsecreen = beforeActivity.getStringExtra("finalscreen");
            finalsecreen = Tools.getScreen_active();
            fromNotf     = beforeActivity.getStringExtra("fromNotification");
        }


        etPass = findViewById(R.id.txt_loginPassword);
        etPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etPass.getRight() - etPass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (!passwVisible){
                            etPass.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_lock_white_24dp, 0, R.drawable.ic_eye_on_withe_24dp,0);
                            etPass.setTransformationMethod(null);
                        }
                        else{
                            etPass.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_lock_white_24dp, 0, R.drawable.ic_eye_off_withe_24dp,0);
                            etPass.setTransformationMethod(new PasswordTransformationMethod());
                        }

                        passwVisible = !passwVisible;

                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void registerScreen(View view) {
        if (userstate.equals("active")){
            Toast.makeText(this, "Account already register. Did you forget the credential?", Toast.LENGTH_SHORT).show();
        } else {
            Intent nextActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(nextActivity);
            finish();
        }
    }

    public void login(View view) {

        if (userstate.equals("active")){
            EditText etName = findViewById(R.id.txt_loginUsername);


            String name = etName.getText().toString();
            String passw = etPass.getText().toString();

            String data = null;
            try {
                data = URLEncoder.encode("n","UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("p","UTF-8") + "=" + URLEncoder.encode(passw, "UTF-8") + "&" +
                        URLEncoder.encode("a","UTF-8") + "=" + URLEncoder.encode(accno, "UTF-8") + "&" +
                        URLEncoder.encode("r","UTF-8") + "=" + URLEncoder.encode(role, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            new LoginActivity.tryLogin(this, data).execute(getResources().getString(R.string.url) + "applogin.php?"+data);
        } else {
            Toast.makeText(this, "There is no registered account. Please register first.", Toast.LENGTH_SHORT).show();
        }

        //Delete all the information in database
        /*MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(this);
        dataBaseAdapter.open();
        if(dataBaseAdapter.db != null) {
            if(dataBaseAdapter.deletDB()){
                Toast.makeText(getApplicationContext(), "ContactsActivity information deleted", Toast.LENGTH_SHORT).show();
            }
        }*/

    }

    public void recover(View view) {
        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        String name = mypreference.getString("puser", "");
        String data = null;
        try {
            data = URLEncoder.encode("a","UTF-8") + "=" + URLEncoder.encode(accno, "UTF-8") + "&" +
                    URLEncoder.encode("r","UTF-8") + "=" + URLEncoder.encode(role, "UTF-8") + "&" +
                    URLEncoder.encode("n","UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new LoginActivity.recoverPassw(this, data).execute(getResources().getString(R.string.url) + "recoverpassw.php?"+data);
    }


    private static class tryLogin extends AsyncTask<String, Void, String> {
        private WeakReference<LoginActivity> activityReference;
        String urlparameters;

        tryLogin(LoginActivity context, String postdata){
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
            LoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            try {
                //
                JSONObject jsonResult = new JSONObject(result);

                if(jsonResult.getBoolean("login")) {

                    Intent nextActivity = new Intent(activity.getApplicationContext(), MainActivity.class);
                    if(activity.fromNotf.equals("true")){ //from notification
                        if (activity.finalsecreen.equals("event")) {
                            nextActivity = new Intent(activity.getApplicationContext(), EventListActivity.class);
                        } else if (activity.finalsecreen.equals("invoice")){
                            nextActivity = new Intent(activity.getApplicationContext(), InvoiceListActivity.class);
                        }
                    }
                    activity.startActivity(nextActivity);
                    activity.finish();
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "There is some problem with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class recoverPassw extends AsyncTask<String, Void, String> {
        private WeakReference<LoginActivity> activityReference;
        String urlparameters;

        recoverPassw(LoginActivity context, String postdata){
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
            LoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            try {
                //
                JSONObject jsonResult = new JSONObject(result);

                if(jsonResult.getBoolean("recover")) {
                    for (int i=0; i < 2; i++)
                    {
                        Toast.makeText(activity.getApplicationContext(), "Information has been sent to the email linked to your account. Please, in case you do not have access to it or can not access it, communicate with us", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(activity.getApplicationContext(), "It was not possible to recover your password. Please, contact us.", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "There is some problem with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
