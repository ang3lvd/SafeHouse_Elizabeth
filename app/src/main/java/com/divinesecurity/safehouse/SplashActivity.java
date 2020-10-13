package com.divinesecurity.safehouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.divinesecurity.safehouse.registerPackage.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        //String username  = mypreference.getString("puser", "");
        //final String userstate = mypreference.getString("puserstatus", "");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 2s = 2000ms
                Intent nextActivity = new Intent(getApplicationContext(), LoginActivity.class);
                nextActivity.putExtra("fromNotification", "false");
                nextActivity.putExtra("finalscreen", "none");
                /*Intent nextActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                if(userstate.equals("active")){
                    nextActivity = new Intent(getApplicationContext(), LoginActivity.class);
                }*/
                startActivity(nextActivity);
                finish();
            }
        }, 2000);

    }

}
