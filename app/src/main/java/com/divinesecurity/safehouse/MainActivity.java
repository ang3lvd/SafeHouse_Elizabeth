package com.divinesecurity.safehouse;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.divinesecurity.safehouse.accountPackage.AccountsListActivity;
import com.divinesecurity.safehouse.alarmPackage.EventListActivity;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.emergencyPackage.EmergencyListActivity;
import com.divinesecurity.safehouse.guestPackage.GuestListActivity;
import com.divinesecurity.safehouse.invoicePackage.InvoiceListActivity;
import com.divinesecurity.safehouse.pdfPackage.TemplatePDF;
import com.divinesecurity.safehouse.settingsPackage.SettingsActivity;
import com.divinesecurity.safehouse.toolsPackage.Tools;
import com.divinesecurity.safehouse.zonePackage.ZoneListActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    NavigationView navigationView = null;

    Animation animationZoom;
    NotificationBadge invnBadge, alarmnBadge, emergencyBadge;

    private String[] header = {"Item", "Quantity", "Description", "Rate", "SubTotal"};
    private TemplatePDF templatePDF;

    int invbdge, alarmbdge, emebdge;

    MyDataBaseAdapter dataBaseAdapter;

    String iName, iAddress, iDate, iDueDate, iNo, iTotal;
    String urole, upanel;

    String username, accnoSel;
    String compLogo;

    //Buy
    Animation animationIn, animationOut;
    LinearLayout layoutBuy, layoutBuyContent;
    boolean helpShow = false;

    ImageView imlogo;

    CountDownTimer panicCountDown;

    private RelativeLayout layoutCount;
    private TextView txtcounterSec, txtcounterMSec;

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    private String longitude = "0", latitude = "0", address = "";

    ArrayList<String> accno, accList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        animationZoom = AnimationUtils.loadAnimation(this, R.anim.zoom_touch_out);

        invnBadge   = findViewById(R.id.ibadge);
        alarmnBadge = findViewById(R.id.abadge);
        emergencyBadge = findViewById(R.id.ebadge);

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        accnoSel               = mypreference.getString("paccountNo", "");
        username               = mypreference.getString("puser", "");
        urole                  = mypreference.getString("prole", "");
        upanel                 = mypreference.getString("ppanel", "");
        String invoiceBagde    = mypreference.getString("pinvbadge", "");
        String alarmBagde      = mypreference.getString("palarmbadge", "");
        String emergBagde      = mypreference.getString("pemergbadge", "");
        compLogo               = mypreference.getString("plogo", "");

        if (invoiceBagde != null && invoiceBagde.equals("")) {
            invoiceBagde = "0";
        }
        invbdge = Integer.parseInt(invoiceBagde != null ? invoiceBagde : "0");
        invnBadge.setNumber(invbdge);

        if (alarmBagde != null && alarmBagde.equals("")) {
            alarmBagde = "0";
        }
        alarmbdge = Integer.parseInt(alarmBagde != null ? alarmBagde : "0");
        alarmnBadge.setNumber(alarmbdge);

        if (emergBagde != null && emergBagde.equals("")) {
            emergBagde = "0";
        }
        emebdge = Integer.parseInt(emergBagde != null ? emergBagde : "0");
        emergencyBadge.setNumber(emebdge);


        View hView =  navigationView.getHeaderView(0);

        Spinner spnacc = hView.findViewById(R.id.spinner_AccountHeader);

        accno = new ArrayList<>();
        accList = new ArrayList<>();

        accList.add(username);
        accno.add(accnoSel);
        LoadAcc();

        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.color_spinner_layout, accList);
        langAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnacc.setAdapter(langAdapter);

        if (accList.size() > 0) {
            username = accList.get(0);
            accnoSel = accno.get(0);
        }


        spnacc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                username = accList.get(position);
                accnoSel = accno.get(position);

                try{
                    if(getSupportActionBar() != null)
                        getSupportActionBar().setTitle(username != null ? username.toUpperCase() : "Client");
                } catch (NullPointerException ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView nav_user = hView.findViewById(R.id.txt_nav_name);
        nav_user.setText(username);

        try{
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(username != null ? username.toUpperCase() : "Client");
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }

        layoutBuy        = findViewById(R.id.armdis_layoutBuy);
        layoutBuyContent = findViewById(R.id.armdis_layoutBuyContent);

        layoutCount    = findViewById(R.id.emergency_layoutBuyCount);
        txtcounterSec  = findViewById(R.id.panic_sec_textV);
        txtcounterMSec = findViewById(R.id.panic_msec_textV);

        //
        animationIn  = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in);
        animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);

        // create a instance of SQLite Database
        dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        Tools.setScreen_active("mainscreen");

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("broad-main"));

        imlogo = findViewById(R.id.imgLogo);
        imlogo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //button pressed
                    layoutCount.setVisibility(View.VISIBLE);
                    txtcounterSec.startAnimation(animationIn);
                    txtcounterMSec.startAnimation(animationIn);
                    getMyLocation();
                    startCountDown();
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    //button relased
                    panicCountDown.cancel();
                    panicCountDown = null;
                    txtcounterSec.startAnimation(animationOut);
                    txtcounterMSec.startAnimation(animationOut);
                    layoutCount.setVisibility(View.GONE);
                }

                return true;
            }
        });

        Glide.with(this)
                .load("http://www.divinesecuritysoftware.com/resources/images/profiles/companies/" + compLogo)
                .placeholder(R.drawable.shieldlogo)
                //.apply(new RequestOptions().override(500, 500))
                .fitCenter()
                .centerCrop()
                //.override(200,200)
                .into(imlogo);

                /*.thumbnail(0.5f)

                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        apd_image.setImageDrawable(glideDrawable);
                        apd_image.setDrawingCacheEnabled(true);
                        saveImage();
                    }});*/

        setUpGClient();
    }

   /* @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(mMessageReceiver, new IntentFilter("broad-main"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }*/

    @Override
    public void onPause() {
        super.onPause();

        if (googleApiClient!= null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "conection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            latitude = Double.toString(mylocation.getLatitude());
            longitude = Double.toString(mylocation.getLongitude());
            Toast.makeText(getApplicationContext(), "Current location: "+latitude+", "+longitude, Toast.LENGTH_SHORT).show();
            //Or Do whatever you want with your location/
        }
    }


    public void emergency(View view) {
        view.startAnimation(animationZoom);
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.dsag_url)));
        Intent intent = new Intent(this, EmergencyListActivity.class);
        startActivity(intent);
    }

    public void arm_disarm(View view) {
        view.startAnimation(animationZoom);

        if (upanel.equals("none")) {
            if (!helpShow) {
                layoutBuy.setVisibility(View.VISIBLE);
                layoutBuyContent.startAnimation(animationIn);
                helpShow = true;
            } else {
                layoutBuyContent.startAnimation(animationOut);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        layoutBuy.setVisibility(View.GONE);
                    }
                }, 500);
                helpShow = false;
            }
        }
    }

    public void invoice(View view) {
        view.startAnimation(animationZoom);
        if (urole.equals("Client") || urole.equals("Admin")) {
            if (invbdge == 1) {
                createPDF();
                templatePDF.appViewPDF(this);
            } else {
                //Toast.makeText(this, "Open Invoice List ...", Toast.LENGTH_SHORT).show();
                Intent invListActivity = new Intent(this, InvoiceListActivity.class);
                startActivity(invListActivity);
            }
        } else if (urole.equals("Guest")){
            Toast.makeText(this, "This feature is only for client...", Toast.LENGTH_SHORT).show();
        }

    }

    public void event_history(View view) {
        view.startAnimation(animationZoom);

       Intent alarmListActivity = new Intent(this, EventListActivity.class);
       startActivity(alarmListActivity);
    }


    public void nothing(View view) {
    }


    private void createPDF() {
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
        templatePDF.addMetaData("Invoice", "Clients", "Divine Security");

        templatePDF.addTitles("Divine Security", "Invoice");

        ArrayList<String[]> rows;
        rows = getInvoice();

        templatePDF.createTableDates(iDate, iDueDate, iNo);
        templatePDF.createTableClientDet(new String[]{iName, iAddress});
        templatePDF.createTableBill(header, rows);

        templatePDF.createTableTotal(iTotal);

        String shortText = "Thank you for your business";
        templatePDF.addParagrah(shortText);

        templatePDF.closeDocument();
    }

    private ArrayList<String[]> getInvoice(){
        ArrayList<String[]> rows = new ArrayList<>();

        Cursor cursor = dataBaseAdapter.getLastInvoice();
        int cant = cursor.getCount();
        String[] itemArr, qtyArr, descArr, rateArr, subTArr;
        String iItem, iQty, iDesc, iRate, iSubTotal;
        if (cant > 0) {
            while (cursor.moveToNext()) { //move for columns
                //cursor.moveToFirst();
                iName    = cursor.getString(cursor.getColumnIndex("ACCNAME"));
                iAddress = cursor.getString(cursor.getColumnIndex("ACCADDRESS"));
                iNo      = cursor.getString(cursor.getColumnIndex("INVNO"));
                iDate    = cursor.getString(cursor.getColumnIndex("INVDATE"));
                iDueDate = cursor.getString(cursor.getColumnIndex("INVDUEDATE"));
                iTotal   = cursor.getString(cursor.getColumnIndex("INVTOTAL"));

                iItem     = cursor.getString(cursor.getColumnIndex("INVITEM"));
                iQty      = cursor.getString(cursor.getColumnIndex("INVQTY"));
                iDesc     = cursor.getString(cursor.getColumnIndex("INVDESC"));
                iRate     = cursor.getString(cursor.getColumnIndex("INVRATE"));
                iSubTotal = cursor.getString(cursor.getColumnIndex("INVSUBTOTAL"));

                itemArr = iItem.split(";");
                qtyArr  = iQty.split(";");
                descArr = iDesc.split(";");
                rateArr = iRate.split(";");
                subTArr = iSubTotal.split(";");

                for (int i=0; i<itemArr.length; i++){
                    rows.add(new String[]{itemArr[i], qtyArr[i], descArr[i], rateArr[i], subTArr[i]});
                }
            }
        }
        cursor.close();

        return rows;
    }


    public void lay_armdis(View view) {
        layoutBuyContent.startAnimation(animationOut);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                layoutBuy.setVisibility(View.GONE);
            }
        }, 500);
        helpShow = false;
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String ptype = intent.getStringExtra("pType");

            SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mypreference.edit();

            switch (ptype) {
                case "alarm": {
                    String alarmBagde = mypreference.getString("palarmbadge", "");
                    if (alarmBagde != null && alarmBagde.equals("")) {
                        alarmBagde = "0";
                    }
                    int bdge = Integer.parseInt(alarmBagde != null ? alarmBagde : "0");
                    bdge++;
                    editor.putString("palarmbadge", "" + bdge);
                    alarmnBadge.setNumber(bdge);
                    break;
                }
                case "invoice": {
                    String invBagde = mypreference.getString("pinvbadge", "");
                    if (invBagde != null && invBagde.equals("")) {
                        invBagde = "0";
                    }
                    int bdge = Integer.parseInt(invBagde != null ? invBagde : "0");
                    bdge++;
                    editor.putString("pinvbadge", "" + bdge);
                    invnBadge.setNumber(bdge);
                    break;
                }
                case "emergency": {
                    String emergencyBagde = mypreference.getString("pemergbadge", "");
                    if (emergencyBagde != null && emergencyBagde.equals("")) {
                        emergencyBagde = "0";
                    }
                    int bdge = Integer.parseInt(emergencyBagde != null ? emergencyBagde : "0");
                    bdge++;
                    editor.putString("pemergbadge", "" + bdge);
                    emergencyBadge.setNumber(bdge);
                    break;
                }
            }
            editor.apply();
        }
    };


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payment) {
            if (urole.equals("Client") || urole.equals("Admin")) {
                startActivity(new Intent(this, InvoiceListActivity.class));
            } else if (urole.equals("Guest")){
                Toast.makeText(this, "This feature is only for client...", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_zonelist) {
            startActivity(new Intent(this, ZoneListActivity.class));
        } else if (id == R.id.nav_invitation) {
            if (urole.equals("Client") || urole.equals("Admin")) {
                startActivity(new Intent(this, GuestListActivity.class));
            } else if (urole.equals("Guest")){
                Toast.makeText(this, "This feature is only for client...", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_accounts) {
            startActivity(new Intent(this, AccountsListActivity.class));
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_callus) {
            try {
                if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission to call denied", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.cellDS))));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(MainActivity.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    void startCountDown(){
        panicCountDown = new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

                int secs = (int) (millisUntilFinished / 1000);
                int msecs = (int) (millisUntilFinished % 1000);
                txtcounterSec.setText(String.format(Locale.getDefault(),"%02d", secs) + ":");
                txtcounterMSec.setText(String.format(Locale.getDefault(), "%02d", msecs));
            }

            @Override
            public void onFinish() {
                txtcounterSec.startAnimation(animationOut);
                txtcounterMSec.startAnimation(animationOut);
                layoutCount.setVisibility(View.GONE);

                Address();

                String data = null;
                try {
                    data = URLEncoder.encode("la", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8") + "&" +
                            URLEncoder.encode("lo", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8") + "&" +
                            URLEncoder.encode("u", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                            URLEncoder.encode("d", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")  + "&" +
                            URLEncoder.encode("a", "UTF-8") + "=" + URLEncoder.encode(accnoSel, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                new SendEmergency(MainActivity.this).execute(getResources().getString(R.string.dssoft_url) + "giams/includes/SafeHome/eregistro.php?" + data);
            }
        }.start();
    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback() {

                        @Override
                        public void onResult(@NonNull Result result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(getApplicationContext(),
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(MainActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    //finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    private void Address() {

        List<Address> addresses;

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String city = " ", state = " " ,  country = " ";
        try {
            if (mylocation != null) {
                addresses = geocoder.getFromLocation(mylocation.getLatitude(), mylocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            address = address + ", " + city + ", " + state + ", " + country;
        }

    }


    private static class SendEmergency extends AsyncTask<String, Void, String> {
        private WeakReference<MainActivity> weakReference;

        SendEmergency(MainActivity context){ weakReference = new WeakReference<>(context); }
        @Override
        protected String doInBackground(String... urls) {
            try {
                return Tools.downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            MainActivity activity = weakReference.get();
            if(result.equals("Unable to retrieve web page. URL may be invalid.")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.getApplicationContext())
                        .setTitle("Connection Trouble")
                        .setIcon(R.drawable.ic_cloud_off)
                        .setMessage("Unable to connect to the server. Please try again.");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    if (jsonResult.getBoolean("result")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity)
                                .setTitle("Emergency Atendida")
                                .setIcon(R.drawable.ic_shield)
                                .setMessage("Your emergency is been processed. Please be calm and wait for the help.");
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        String cause = jsonResult.getString("case");
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.getApplicationContext())
                                .setTitle("Server Trouble")
                                .setIcon(R.drawable.ic_warning)
                                .setMessage("The server returned a mistake. Please try again.\r\nCause: " + cause);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }



    public void LoadAcc() {
        Cursor cursor = null;
        try {
            MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
            dataBaseAdapter = dataBaseAdapter.open();

            cursor = dataBaseAdapter.getEntry_AllAcc_List();
            int cant = cursor.getCount();

            accList.clear();
            accno.clear();

            if (cant > 0) {
                while (cursor.moveToNext()) { //move for columns
                    //cursor.moveToFirst();
                    accList.add(cursor.getString(cursor.getColumnIndex("ACCOUNTNAME")));
                    accno.add(cursor.getString(cursor.getColumnIndex("ACCNO")));
                }
            }
            cursor.close();
        } catch (Exception ex){
            if (cursor != null)
                cursor.close();
        }
    }

}
