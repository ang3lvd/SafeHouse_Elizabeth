package com.divinesecurity.safehouse.settingsPackage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.divinesecurity.safehouse.R;

import java.util.Arrays;
import java.util.List;

public class NotificationSttngActivity extends AppCompatActivity {

    private static final int PICK_RINGTONE_REQUEST = 1;

    TextView alertSound, alertVibra, alertLight;
    String Str_alertSound, Str_alertVibra, Str_alertLight;

    SharedPreferences mypreferences;

    List<String> alert_vibra_list, alert_light_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_sttng);

        alertSound  = findViewById(R.id.alert_tone);
        alertVibra  = findViewById(R.id.alert_vibrattion);
        alertLight  = findViewById(R.id.alert_light);

        mypreferences = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        Str_alertSound = mypreferences.getString("alertSound", "");
        Str_alertVibra = mypreferences.getString("alertVibrator", "");
        Str_alertLight = mypreferences.getString("alertLight", "");

        final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(Str_alertSound));
        alertSound.setText(ringtone.getTitle(getApplicationContext()));
        alertVibra.setText(Str_alertVibra);
        alertLight.setText(Str_alertLight);

        alert_vibra_list = Arrays.asList(getResources().getStringArray(R.array.alert_vibrate_array));
        alert_light_list = Arrays.asList(getResources().getStringArray(R.array.alert_light_array));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sttng_notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.reset_notf_sttng) {
            //Semuestra dialog para confirmar
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Reset all notifications settings, including custom notificatios settings for your chats?");
            alertDialogBuilder.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Str_alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
                    Str_alertVibra = "Default";
                    Str_alertLight = "White";

                    SharedPreferences.Editor editor = mypreferences.edit();
                    editor.putString("alertSound", Str_alertSound);
                    editor.putString("alertVibrator", "Default");
                    editor.putString("alertLight", "White");
                    editor.apply();

                    Uri tone_uri = Uri.parse(Str_alertSound);
                    final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), tone_uri);

                    alertSound.setText(ringtone.getTitle(getApplicationContext()));
                    alertVibra.setText(R.string.alertVibra_default);
                    alertLight.setText(R.string.alertLight_white);
                }
            });
            alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            return true;
        } else if (id == R.id.test_notf_sttng){
            String notfSound = mypreferences.getString("alertSound", "");
            String notfVibra = mypreferences.getString("alertVibrator", "");
            String notfLight = mypreferences.getString("alertLight", "");

            Intent notifIntent = new Intent(this, NotificationSttngActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notifIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder msgnotif = new NotificationCompat.Builder(this, "SH_CH_IDD");
            msgnotif.setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_siren_48))
                    .setSmallIcon(R.drawable.shieldlogo48)
                    .setContentTitle("Test Alarm Setting")
                    .setContentText("Verify the notification seettings")
                    .setContentIntent(pendingIntent);

            if (!notfSound.equals("silent")){
                msgnotif.setSound(Uri.parse(notfSound));
            }
            if (!notfVibra.equals("Off")){
                switch (notfVibra) {
                    case "Default":
                        msgnotif.setVibrate(new long[]{1000, 500, 1000, 500});
                        break;
                    case "Short":
                        msgnotif.setVibrate(new long[]{250, 150, 500, 100});
                        break;
                    case "Long":
                        msgnotif.setVibrate(new long[]{1000, 1000, 1000, 1000});
                        break;
                }
            }
            if (!notfLight.equals("None")){
                switch (notfVibra) {
                    case "White":
                        msgnotif.setLights(Color.WHITE, 0, 1);
                        break;
                    case "Red":
                        msgnotif.setLights(Color.RED, 0, 1);
                        break;
                    case "Yellow":
                        msgnotif.setLights(Color.YELLOW, 0, 1);
                        break;
                    case "Green":
                        msgnotif.setLights(Color.GREEN, 0, 1);
                        break;
                    case "Cyan":
                        msgnotif.setLights(Color.CYAN, 0, 1);
                        break;
                    case "Blue":
                        msgnotif.setLights(Color.BLUE, 0, 1);
                        break;
                    case "Purple":
                        msgnotif.setLights(Color.MAGENTA, 0, 1);
                        break;
                }
            }
            if (notificationManager != null) {
                notificationManager.notify(0, msgnotif.build());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void notificationSelect(View view) {
        SharedPreferences mypreferences = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        String notfSound = mypreferences.getString("alertSound", "");
        //Crear un intent para seleccionar un ringtone del dispositivo
        Intent rgtone_intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        rgtone_intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Notification tone")
                .putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        if (notfSound.equals("silent")){
            rgtone_intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        } else{
            rgtone_intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(notfSound));
        }

        startActivityForResult(rgtone_intent, PICK_RINGTONE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_RINGTONE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Capturar el valor de la Uri
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                //Procesar la Uri
                SharedPreferences.Editor editor = mypreferences.edit();

                if (uri == null){
                    alertSound.setText(R.string.aerSound_silent);
                    editor.putString("alertSound", "silent");
                } else {
                    final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                    alertSound.setText(ringtone.getTitle(getApplicationContext()));
                    editor.putString("alertSound", uri.toString());
                }
                editor.apply();
                //Toast.makeText(getApplicationContext(), rtone, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void vibrate_select(View view) {
        int alert_vibrate_pos = alert_vibra_list.indexOf(Str_alertVibra);
        final AlertDialog.Builder dialog  = new AlertDialog.Builder(NotificationSttngActivity.this)
                .setTitle("Vibrate");
        dialog.setSingleChoiceItems(R.array.alert_vibrate_array, alert_vibrate_pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Str_alertVibra = alert_vibra_list.get(which);
                alertVibra.setText(Str_alertVibra);

                SharedPreferences.Editor editor = mypreferences.edit();
                editor.putString("alertVibrator", Str_alertVibra);
                editor.apply();

                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void onAlertLight(View view) {
        int alert_light_pos = alert_light_list.indexOf(Str_alertLight);
        final AlertDialog.Builder dialog  = new AlertDialog.Builder(NotificationSttngActivity.this)
                .setTitle("Light");
        dialog.setSingleChoiceItems(R.array.alert_light_array, alert_light_pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Str_alertLight = alert_light_list.get(which);
                alertLight.setText(Str_alertLight);

                SharedPreferences.Editor editor = mypreferences.edit();
                editor.putString("alertLight", Str_alertLight);
                editor.apply();

                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
            }
        });
        dialog.show();
    }
}
