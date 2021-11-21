package com.divinesecurity.safehouse.registerPackage;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.divinesecurity.safehouse.MainActivity;
import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.toolsPackage.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
//import com.google.firebase.iid.FirebaseInstanceIdReceiver;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    //User credentials
    EditText txtusern, txtpassw, txtspassw, txtemail;
    Spinner spnnRole;
    //Spinner spnnCompany;
    String ucode, uname, upassw, uspassw, uemail, urole, uref_code="", ucountry, ucompany;
    String mToken = "";

    //RelativeLayout rellayCom;

    //ImageButton mSpeakBtn;

    LinearLayout ll_code;
    Boolean isCodeLayoutVisible = false;
    Animation animationIn, animationOut;
    TextView tv_code;
    EditText et_code1, et_code2, et_code3, et_code4, et_code5, et_code6;
    EditText et_code7, et_code8, et_code9, et_code10, et_code11, et_code12;

    private static final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txtusern    = findViewById(R.id.editUser);
        txtpassw    = findViewById(R.id.editPassw);
        txtspassw   = findViewById(R.id.editStressPassw);
        txtemail    = findViewById(R.id.editEmail);
        //spnnCompany = findViewById(R.id.spinnerCompany);
        spnnRole    = findViewById(R.id.spinnerRole);
        //mSpeakBtn   = findViewById(R.id.imgBtn_mic);
        //rellayCom   = findViewById(R.id.rellayCompany);

        /*spnnCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView t1 = view.findViewById(R.id.idcompany_name);
                Toast.makeText(RegisterActivity.this, t1.getText(), Toast.LENGTH_SHORT).show();
                ucompany = t1.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

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

        /*
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.getResult() != null)
                    mToken = task.getResult().getToken();
            }
        });*/

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        if (task.getResult() != null)
                            mToken = task.getResult();
                    }
                });

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.map_key));
        }
        /*setupAutocompleteSupportFragment();*/

       /*mSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });*/

        ImageView ivLogo = findViewById(R.id.iv_logo);
        Glide.with(this)
                .load("http://www.divinesecuritysoftware.com/resources/images/pas/ShieldT.gif")
                .placeholder(R.drawable.shieldholder)
                .fitCenter()
                .into(ivLogo);


        //CODE
        animationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in);
        animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);
        ll_code = findViewById(R.id.ll_code);
        tv_code = findViewById(R.id.tv_code);
        et_code1 = findViewById(R.id.et_code1);
        et_code1.addTextChangedListener(new TextWatcher() {
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
                        et_code1.setText(Character.toString(character).toUpperCase());
                    } else {
                        et_code1.setTextColor(Color.WHITE);
                        et_code1.setBackgroundResource(R.drawable.code_circle);
                    }

                    checkVisibility();

                } else{
                    editedFlag = false;
                    et_code1.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code1.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code2.requestFocus();
                }
            }
        });
        et_code2 = findViewById(R.id.et_code2);
        et_code2.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code2.setText(Character.toString(character).toUpperCase());
                    } else {
                        et_code2.setTextColor(Color.WHITE);
                        et_code2.setBackgroundResource(R.drawable.code_circle);
                        et_code1.requestFocus();
                    }

                    checkVisibility();
                } else{
                    editedFlag = false;
                    et_code2.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code2.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code3.requestFocus();
                }
            }
        });
        et_code3 = findViewById(R.id.et_code3);
        et_code3.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code3.setText(Character.toString(character));
                    } else {
                        et_code3.setTextColor(Color.WHITE);
                        et_code3.setBackgroundResource(R.drawable.code_circle);
                        et_code2.requestFocus();
                    }
                    checkVisibility();
                } else{
                    editedFlag = false;
                    et_code3.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code3.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code4.requestFocus();
                }
            }
        });
        et_code4 = findViewById(R.id.et_code4);
        et_code4.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code4.setText(Character.toString(character));
                    } else {
                        et_code4.setTextColor(Color.WHITE);
                        et_code4.setBackgroundResource(R.drawable.code_circle);
                        et_code3.requestFocus();
                    }
                    checkVisibility();
                } else{
                    editedFlag = false;
                    et_code4.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code4.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code5.requestFocus();
                }
            }
        });
        et_code5 = findViewById(R.id.et_code5);
        et_code5.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code5.setText(Character.toString(character));
                    } else {
                        et_code5.setTextColor(Color.WHITE);
                        et_code5.setBackgroundResource(R.drawable.code_circle);
                        et_code4.requestFocus();
                    }
                    checkVisibility();
                } else{
                    editedFlag = false;
                    et_code5.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code5.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code6.requestFocus();
                }
            }
        });
        et_code6 = findViewById(R.id.et_code6);
        et_code6.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code6.setText(Character.toString(character));
                    } else {
                        et_code6.setTextColor(Color.WHITE);
                        et_code6.setBackgroundResource(R.drawable.code_circle);
                        et_code5.requestFocus();
                    }
                    checkVisibility();
                } else{
                    editedFlag = false;
                    et_code6.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code6.setBackgroundResource(R.drawable.code_circle_selected);
                }
            }
        });

        et_code7 = findViewById(R.id.et_code7);
        et_code7.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code7.setText(Character.toString(character).toUpperCase());
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
        et_code8 = findViewById(R.id.et_code8);
        et_code8.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code8.setText(Character.toString(character).toUpperCase());
                    } else {
                        et_code8.setTextColor(Color.WHITE);
                        et_code8.setBackgroundResource(R.drawable.code_circle);
                    }
                } else{
                    editedFlag = false;
                    et_code8.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code8.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code9.requestFocus();
                }
            }
        });
        et_code9 = findViewById(R.id.et_code9);
        et_code9.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code9.setText(Character.toString(character).toUpperCase());
                    } else {
                        et_code9.setTextColor(Color.WHITE);
                        et_code9.setBackgroundResource(R.drawable.code_circle);
                    }
                } else{
                    editedFlag = false;
                    et_code9.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code9.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code10.requestFocus();
                }
            }
        });
        et_code10 = findViewById(R.id.et_code10);
        et_code10.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code10.setText(Character.toString(character).toUpperCase());
                    } else {
                        et_code10.setTextColor(Color.WHITE);
                        et_code10.setBackgroundResource(R.drawable.code_circle);
                    }
                } else{
                    editedFlag = false;
                    et_code10.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code10.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code11.requestFocus();
                }
            }
        });
        et_code11 = findViewById(R.id.et_code11);
        et_code11.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code11.setText(Character.toString(character).toUpperCase());
                    } else {
                        et_code11.setTextColor(Color.WHITE);
                        et_code11.setBackgroundResource(R.drawable.code_circle);
                    }
                } else{
                    editedFlag = false;
                    et_code11.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code11.setBackgroundResource(R.drawable.code_circle_selected);
                    et_code12.requestFocus();
                }
            }
        });
        et_code12 = findViewById(R.id.et_code12);
        et_code12.addTextChangedListener(new TextWatcher() {
            private boolean editedFlag = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editedFlag){
                    if(editable.length() >= 1){
                        char character = editable.charAt(editable.length() - 1);
                        editedFlag = true;
                        et_code12.setText(Character.toString(character).toUpperCase());
                    } else {
                        et_code12.setTextColor(Color.WHITE);
                        et_code12.setBackgroundResource(R.drawable.code_circle);
                        et_code11.requestFocus();
                    }
                } else{
                    editedFlag = false;
                    et_code12.setTextColor(getResources().getColor(R.color.colorPrimary));
                    et_code12.setBackgroundResource(R.drawable.code_circle_selected);
                }
            }
        });
    }

    private void checkVisibility(){
        if(fullFields() && ll_code.getVisibility() == View.GONE){
            ll_code.setVisibility(View.VISIBLE);
            ll_code.startAnimation(animationIn);
            isCodeLayoutVisible = true;
        } else if (!fullFields() && ll_code.getVisibility() == View.VISIBLE){
            ll_code.startAnimation(animationOut);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    ll_code.setVisibility(View.GONE);
                }
            }, 500);
        }
    }
    private boolean fullFields() {
        return ((et_code1.getText().toString().length() == 1) &&  (et_code2.getText().toString().length() == 1) && (et_code3.getText().toString().length() == 1) &&
                (et_code4.getText().toString().length() == 1) &&  (et_code5.getText().toString().length() == 1) && (et_code6.getText().toString().length() == 1));
    }

    /*private void setupAutocompleteSupportFragment() {
        // Initialize the AutocompleteSupportFragment.
        final AutocompleteSupportFragment autocompleteFragment =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        Objects.requireNonNull(autocompleteFragment).setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());
    }*/

    /*private PlaceSelectionListener getPlaceSelectionListener() {
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

                //rellayCom.setVisibility(View.VISIBLE);
                //new Companies_List(RegisterActivity.this).execute(getResources().getString(R.string.dsapp_url) + "divsecapp/companies_list_app.php?"+ data);
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(RegisterActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }*/

    //Register btn
    public void signup(View view) {

        if (checkFields()) { //If fields are correct
            if(Tools.checkConnectivity(getApplicationContext())) {//There is connectivity
                if (!mToken.equals("")) { //Token is not empty
                    uref_code = et_code1.getText().toString() + et_code2.getText().toString() + et_code3.getText().toString() + et_code4.getText().toString() + et_code5.getText().toString() + et_code6.getText().toString();
                    ucode     = et_code7.getText().toString() + et_code8.getText().toString() + et_code9.getText().toString() + et_code10.getText().toString() + et_code11.getText().toString() + et_code12.getText().toString();
                    uname = txtusern.getText().toString();
                    upassw = txtpassw.getText().toString();
                    uspassw = txtspassw.getText().toString();
                    uemail = txtemail.getText().toString();
                    urole = spnnRole.getSelectedItem().toString();

                    //Create url string with parameters
                    String data = null;
                    try {
                        data = URLEncoder.encode("c", "UTF-8") + "=" + URLEncoder.encode(ucode, "UTF-8") + "&" +
                                URLEncoder.encode("u", "UTF-8") + "=" + URLEncoder.encode(uname, "UTF-8") + "&" +
                                URLEncoder.encode("p", "UTF-8") + "=" + URLEncoder.encode(upassw, "UTF-8") + "&" +
                                URLEncoder.encode("sp", "UTF-8") + "=" + URLEncoder.encode(uspassw, "UTF-8") + "&" +
                                URLEncoder.encode("e", "UTF-8") + "=" + URLEncoder.encode(uemail, "UTF-8") + "&" +
                                URLEncoder.encode("r", "UTF-8") + "=" + URLEncoder.encode(urole, "UTF-8") + "&" +
                                URLEncoder.encode("t", "UTF-8") + "=" + URLEncoder.encode(mToken, "UTF-8") + "&" +
                                URLEncoder.encode("rc", "UTF-8") + "=" + URLEncoder.encode(uref_code, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //Call UserRegister for saving info in the cloud
                    new UserRegister(this, data).execute(getResources().getString(R.string.dssoft_url) + "giams/includes/SafeHome/uregisterapp.php?" + data);
                } else {
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }

                                    // Get new FCM registration token
                                    if (task.getResult() != null)
                                        mToken = task.getResult();
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

                    String accno = null, logo = null;

                    //Save info received from the cloud
                    SharedPreferences mypreferences = activity.getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mypreferences.edit();

                    editor.putString("puser", activity.uname); //username
                    editor.putString("ppassw", activity.upassw);//user password
                    editor.putString("pspassw", activity.uspassw);//user stress password
                    editor.putString("prole", activity.urole);//user role
                    editor.putString("puserstatus", "active"); //user status
                    editor.putString("ppanel", "none");
                    editor.putString("padmin", "true"); //puede agregar     false: full

                    editor.putString("ptoken", activity.mToken); //token

                    editor.putString("pinvbadge", "0");
                    editor.putString("palarmbadge", "0");
                    editor.putString("pemergbadge", "0");

                    editor.putString("alertSound", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
                    editor.putString("alertVibrator", "Default");
                    editor.putString("alertLight", "White");

                    accno = jsonResult.getString("IDc");
                    editor.putString("paccountNo", accno);

                    logo = jsonResult.getString("compPic");
                    editor.putString("plogo", logo);

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
                    new getZoneInfo(activity, data).execute(activity.getResources().getString(R.string.dssoft_url) + "giams/includes/SafeHome/zoneinfo.php?"+data);

                    /*Intent mainActivity = new Int ent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivity);
                    finish();*/

                    Toast.makeText(activity.getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                }
                else {
                    if (jsonResult.getString("cause").contains("code")) {
                        //activity.txtcode.setError("Code wrong!");
                        activity.tv_code.setError("Code doesn't match.");
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "There was a problem on the server [" + jsonResult.getString("cause") + "]. Please try again in a few minutes.", Toast.LENGTH_LONG).show();
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
        if(et_code1.getText().toString().length() != 1 || et_code2.getText().toString().length() != 1 || et_code3.getText().toString().length() != 1 || et_code4.getText().toString().length() != 1 ||
                et_code5.getText().toString().length() != 1 || et_code6.getText().toString().length() != 1){
            tv_code.setError("Code incorrect.");
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

    /*private static class Companies_List extends AsyncTask<String, Void, String> {
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
                    //activity.spnnCompany.setAdapter(company_adaptador);
                }

            } catch (JSONException e) {
                //Log.i("a", e.getMessage());
            }
        }
    } */
}
