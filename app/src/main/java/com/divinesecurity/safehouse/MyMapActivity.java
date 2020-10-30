package com.divinesecurity.safehouse;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    String contname, latitude, longitude, address = "", dateE = "undefined", group, urole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.framemap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //
        Intent beforeActivity = getIntent();
        if (beforeActivity != null) {
            contname  = beforeActivity.getStringExtra("contName");
            latitude  = beforeActivity.getStringExtra("contLat");
            longitude = beforeActivity.getStringExtra("contLong");
            address   = beforeActivity.getStringExtra("contDir");
            group     = beforeActivity.getStringExtra("group");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss", Locale.getDefault());
            dateE = sdf.format(new Date());

            SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
            urole = mypreference.getString("prole", "");
        }

        drawMap();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent beforeActivity = this.getIntent();
        if (beforeActivity != null){
            startActivity(new Intent(MyMapActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng markpoint = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        googleMap.addMarker(new MarkerOptions().position(markpoint).title(contname));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markpoint));
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    private void drawMap() {

        TextView nameC = findViewById(R.id.name_map);

        nameC.setText(contname);
        TextView addrssE = findViewById(R.id.addrss_map);
        addrssE.setText("requests for help at " + address);

        TextView icidentTitle = findViewById(R.id.incident_map);
        if (urole.equals("Client") || urole.equals("Admin")) {
            icidentTitle.setText("The member " + contname + " of your group (" + group + ") has an incident arround the map. \r\nOn " + dateE );
        } else if (urole.equals("Guest")){
            icidentTitle.setText("The member " + contname + " of your group has an incident arround the map. \r\nOn " + dateE );
        }
    }
}

