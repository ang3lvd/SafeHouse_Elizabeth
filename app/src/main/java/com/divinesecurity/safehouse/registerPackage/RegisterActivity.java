package com.divinesecurity.safehouse.registerPackage;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.divinesecurity.safehouse.MainActivity;
import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.listViewAdapterPackage.Spinner_Adapter;
import com.divinesecurity.safehouse.toolsPackage.Tools;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    //User credentials
    EditText txtcode, txtusern, txtpassw, txtspassw, txtemail;
    Spinner spnnRole, spnnCompany;
    String code, uname, upassw, uspassw, uemail, urole, ucountry, ucompany;
    String mToken = "";

    RelativeLayout rellayCom;

    ImageButton mSpeakBtn;

    private static final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txtcode     = findViewById(R.id.editCode);
        txtusern    = findViewById(R.id.editUser);
        txtpassw    = findViewById(R.id.editPassw);
        txtspassw   = findViewById(R.id.editStressPassw);
        txtemail    = findViewById(R.id.editEmail);
        spnnCompany = findViewById(R.id.spinnerCompany);
        spnnRole    = findViewById(R.id.spinnerRole);
        mSpeakBtn   = findViewById(R.id.imgBtn_mic);
        rellayCom   = findViewById(R.id.rellayCompany);

        spnnCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView t1 = view.findViewById(R.id.idcompany_name);
                Toast.makeText(RegisterActivity.this, t1.getText(), Toast.LENGTH_SHORT).show();
                ucompany = t1.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        urole = "Client";
        String[] role = {"Client","Admin","Guest"};
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_text, role);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdow);
        spnnRole.setAdapter(langAdapter);

        spnnRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    txtspassw.setVisibility(View.VISIBLE);
                } else if(position == 1){
                    txtspassw.setVisibility(View.GONE);
                }

                urole   = spnnRole.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtcode.requestFocus();
        //Filter format code for guest
        txtcode.addTextChangedListener(new TextWatcher() {
            //we need to know if the user is erasing or inputing some new character
            //private boolean backspacingFlag = false;
            //we need to block the :afterTextChanges method to be called again after we just replaced the EditText text
            private boolean editedFlag = false;
            //we need to mark the cursor position and restore it after the edition
            private int cursorComplement;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //we store the cursor local relative to the end of the string in the EditText before the edition
                cursorComplement = charSequence.length()-txtcode.getSelectionStart();
                //we check if the user is inputing or erasing a character
                //backspacingFlag = i1 > i2;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();
                //what matters are the digits beneath the mask, so we always work with a raw string with only digits
                String tmpcode = string.replaceAll("[^\\d]", "");

                //if the text was just edited, :afterTextChanged is called another time... so we need to verify the flag of edition
                //if the flag is false, this is a original user-typed entry. so we go on and do some magic ;)
                if (!editedFlag) {
                    if (tmpcode.length() > 4) {
                        editedFlag = true;
                        String ans = tmpcode.substring(0, 4) + "-" + tmpcode.substring(4);
                        txtcode.setText(ans);
                        txtcode.setSelection(txtcode.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.getResult() != null)
                    mToken = task.getResult().getToken();
            }
        });

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.map_key));
        }
        setupAutocompleteSupportFragment();

       mSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
    }

    private void setupAutocompleteSupportFragment() {
        // Initialize the AutocompleteSupportFragment.
        final AutocompleteSupportFragment autocompleteFragment =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        Objects.requireNonNull(autocompleteFragment).setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());
    }

    private PlaceSelectionListener getPlaceSelectionListener() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                String[] placeList = Objects.requireNonNull(place.getAddress()).split(",");
                ucountry = placeList[placeList.length - 1];

                Toast.makeText(getApplicationContext(), "Clicked: " + placeList[0],
                        Toast.LENGTH_SHORT).show();

                String data = null;
                try {
                    data = URLEncoder.encode("c","UTF-8") + "=" + URLEncoder.encode(ucountry, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                closeKeyboard();

                rellayCom.setVisibility(View.VISIBLE);
                new Companies_List(RegisterActivity.this).execute(getResources().getString(R.string.dsapp_url) + "divsecapp/companies_list_app.php?"+ data);
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(RegisterActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void signup(View view) {

        if (checkFields()) {
            if(Tools.checkConnectivity(getApplicationContext())) {
                if (!mToken.equals("")) {

                    code = txtcode.getText().toString();
                    uname = txtusern.getText().toString();
                    upassw = txtpassw.getText().toString();
                    uspassw = txtspassw.getText().toString();
                    uemail = txtemail.getText().toString();
                    urole = spnnRole.getSelectedItem().toString();

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
                } else {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (task.getResult() != null)
                                mToken = task.getResult().getToken();
                        }
                    });
                    Toast.makeText(getApplicationContext(), "Registration Token is not ready yet. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "We warned you that there is no connection to internet. Make sure Wi-Fi or cellular data is turned on.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Some mistake in fields registration", Toast.LENGTH_SHORT).show();
        }

    }


    private static class UserRegister extends AsyncTask<String, Void, String> {
        private WeakReference<RegisterActivity> activityReference;
        String urlparameters;

        UserRegister(RegisterActivity context, String postdata){
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
            RegisterActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            try {
                JSONObject jsonResult = new JSONObject(result);
                if(jsonResult.getBoolean("uregister")){

                    String accno = null;

                    SharedPreferences mypreferences = activity.getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mypreferences.edit();

                    editor.putString("puser", activity.uname);
                    editor.putString("ppassw", activity.upassw);
                    editor.putString("pspassw", activity.uspassw);
                    editor.putString("prole", activity.urole);
                    editor.putString("puserstatus", "active");
                    editor.putString("ppanel", "none");
                    editor.putString("padmin", "true"); //puede agregar     false: full

                    editor.putString("ptoken", activity.mToken);

                    editor.putString("pinvbadge", "0");
                    editor.putString("palarmbadge", "0");

                    editor.putString("alertSound", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
                    editor.putString("alertVibrator", "Default");
                    editor.putString("alertLight", "White");

                    if (activity.urole.equals("Client")){
                        accno = jsonResult.getString("accNo");
                    } else if (activity.urole.equals("Guest") || activity.urole.equals("Admin")){
                        accno = activity.code.split("-")[0];

                    }
                    editor.putString("paccountNo", accno);

                    editor.putString("pscreen", "none");

                    editor.putString("lock_state", "none");

                    editor.putString("evAlarm", "true");
                    editor.putString("evOpenClose", "true");
                    editor.putString("evEvent", "true");
                    editor.apply();

                    MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(activity.getApplicationContext());
                    dataBaseAdapter = dataBaseAdapter.open();

                    dataBaseAdapter.insertEntry_ACC(activity.uname, activity.upassw, activity.uspassw, activity.urole, activity.uemail, accno, "true");

                    //Get Zones Info
                    String data = null;
                    try {
                        data = URLEncoder.encode("a","UTF-8") + "=" + URLEncoder.encode(accno, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    new getZoneInfo(activity, data).execute(activity.getResources().getString(R.string.url) + "zoneinfo.php?"+data);

                    /*Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivity);
                    finish();*/

                    Toast.makeText(activity.getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                }
                else {
                    if (jsonResult.getBoolean("codefaild")) {
                        activity.txtcode.setError("Code wrong!");
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "There is some problem with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "There is some error with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkFields() {
        boolean flag = true;
        if(txtcode.getText().toString().length() < 4){
            txtcode.setError("Code is requiered!");
            flag = false;
        }else if(txtusern.getText().toString().length() < 2){
            txtusern.setError("Name is requiered!");
            flag = false;
        }else if(txtpassw.getText().toString().length() < 4){
            txtpassw.setError("Password is requiered!");
            flag = false;
        }/*else if((txtspassw.getVisibility() == View.VISIBLE) && (txtspassw.getText().toString().length() < 4)){
            txtspassw.setError("Stress Password is requiered!");
            flag = false;
        }*/else if (!Tools.validarEmail(txtemail.getText().toString())){
            txtemail.setError("Email is requiered!");
            flag = false;
        } else if (txtpassw.getText().toString().equals(txtspassw.getText().toString())){
            txtspassw.setError("Stress Password couldn't be equal to Password");
            flag = false;
        }
        return flag;
    }

    private static class getZoneInfo extends AsyncTask<String, Void, String> {
        private WeakReference<RegisterActivity> activityReference;
        String urlparameters;

        getZoneInfo(RegisterActivity context, String postdata){
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
            RegisterActivity activity = activityReference.get();
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

                Intent mainActivity = new Intent(activity.getApplicationContext(), MainActivity.class);
                activity.startActivity(mainActivity);
                activity.finish();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "There is some problem with the server. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, Pronunce?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException ignored) {

        }
    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private static class Companies_List extends AsyncTask<String, Void, String> {
        private WeakReference<RegisterActivity> activityReference;

        Companies_List(RegisterActivity context){
            this.activityReference = new WeakReference<>(context);
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
            RegisterActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            try {
                if (!result.equals("error")) {
                    JSONArray jsonResult = new JSONArray(result);

                    Spinner_Adapter company_adaptador;

                    ArrayList<String> companyList = new ArrayList<>();
                    ArrayList<String> logoList = new ArrayList<>();
                    ArrayList<String> stateList = new ArrayList<>();

                    companyList.clear();
                    stateList.clear();

                    companyList.add("-select company-");
                    logoList.add("null");
                    stateList.add("Companies List");

                    if (jsonResult.length() > 1){
                        for (int i = 0; i<jsonResult.length(); i++){
                            companyList.add(jsonResult.getJSONArray(i).getString(0));
                            logoList.add(jsonResult.getJSONArray(i).getString(1));
                            stateList.add(jsonResult.getJSONArray(i).getString(2));
                        }
                    }

                    company_adaptador = new Spinner_Adapter(activity.getApplicationContext(), activity, companyList, logoList, stateList);
                    activity.spnnCompany.setAdapter(company_adaptador);
                }

            } catch (JSONException e) {
                //Log.i("a", e.getMessage());
            }
        }
    }
}
