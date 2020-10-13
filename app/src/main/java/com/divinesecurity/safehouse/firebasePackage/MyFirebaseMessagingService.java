package com.divinesecurity.safehouse.firebasePackage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.divinesecurity.safehouse.MainActivity;
import com.divinesecurity.safehouse.MyMapActivity;
import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.registerPackage.LoginActivity;
import com.divinesecurity.safehouse.toolsPackage.Tools;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    String userName;
    String tipo, alarmMessage, alarmDate, alarmZone, alarmDesc = "";
    int contid = 0;
    String zoneNo, zoneDesc;
    String info_title, notflength;
    String iName, iAddress, iNo, iDate, iDueDate, iItem, iQty, iDesc, iRate, iSubTotal, iTotal;
    String guestName, guestRole;
    String contName = "unknow", clientLong, clientLat, clientDir, group;

    String CHANNEL_ID = "my_channel_02";// The id of the channel.

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        tipo = remoteMessage.getData().get("tipo");
        PreActions(remoteMessage);
    }

    private void PreActions(RemoteMessage remoteMessage){
        switch (tipo) {
            case "alarm": {
                alarmMessage = remoteMessage.getData().get("msg");
                //alarmDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                alarmDate = remoteMessage.getData().get("dt");
                alarmZone = remoteMessage.getData().get("zn");
                userName  = remoteMessage.getData().get("auser");
                alarmDesc = remoteMessage.getData().get("edesc");

                SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                boolean[] typeArr = new boolean[]{true, true, false};
                typeArr[0] = mypreference.getString("evAlarm", "").equals("true");
                typeArr[1] = mypreference.getString("evOpenClose", "").equals("true");
                typeArr[2] = mypreference.getString("evEvent", "").equals("true");

                if((alarmDesc.equals("alarm") && typeArr[0]) || ((alarmDesc.equals("open") || alarmDesc.equals("close")) && typeArr[1]) || ((!alarmDesc.equals("alarm") && !alarmDesc.equals("open") && !alarmDesc.equals("close")) && typeArr[2])){
                    String screen_active = Tools.getScreen_active();
                    saveMsgDB();
                    switch (screen_active) {
                        case "none":
                            ShowNotification(alarmMessage);
                            break;
                        case "mainscreen": {
                            Intent intent = new Intent("broad-main");
                            intent.putExtra("pType", "alarm");
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        }
                        case "eventscreen": {
                            Intent intent = new Intent("broad-alarm");
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        }
                        default:
                            ShowNotification(alarmMessage);
                            break;
                    }
                }
                break;
            }
            case "invoice": {
                alarmMessage = "New invoice from Divine Security";
                iName     = remoteMessage.getData().get("name");
                iAddress  = remoteMessage.getData().get("address");
                iNo       = remoteMessage.getData().get("invno");
                iDate     = remoteMessage.getData().get("date");
                iItem     = remoteMessage.getData().get("item");
                iQty      = remoteMessage.getData().get("qty");
                iDesc     = remoteMessage.getData().get("desc");
                iRate     = remoteMessage.getData().get("rate");
                iSubTotal = remoteMessage.getData().get("subtotal");
                iTotal    = remoteMessage.getData().get("total");

                String[] splitDate = iDate.split("/");
                iDueDate = splitDate[0] + "/" + "30" + "/" + splitDate[2];

                String screen_active = Tools.getScreen_active();
                saveInvoiceDB();
                switch (screen_active) {
                    case "none":
                        ShowNotification(alarmMessage);
                        break;
                    case "mainscreen": {
                        Intent intent = new Intent("broad-main");
                        intent.putExtra("pType", "invoice");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    }
                    case "invoicescreen": {
                        Intent intent = new Intent("broad-invoice");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    }
                }
                break;
            }
            case "zone": {
                zoneNo = remoteMessage.getData().get("zno");
                zoneDesc = remoteMessage.getData().get("zdesc");
                userName = remoteMessage.getData().get("zuser");
                saveZoneDB();
                break;
            }
            case "info": {
                alarmMessage = remoteMessage.getData().get("msg");
                info_title = remoteMessage.getData().get("title");
                notflength = remoteMessage.getData().get("length");
                ShowNotification(alarmMessage);
                break;
            }
            case "guest": {
                guestName = remoteMessage.getData().get("name");
                guestRole = remoteMessage.getData().get("role");
                saveGuestDB();
                break;
            }
            case "deleteClient": {
                DeletePreference();
                break;
            }
            case "emergency": {
                clientLong = remoteMessage.getData().get("longitud");
                clientLat = remoteMessage.getData().get("latitud");
                clientDir = remoteMessage.getData().get("direccion");
                contName = remoteMessage.getData().get("user");
                group = remoteMessage.getData().get("group");

                ShowNotification("Contact " + contName + " has a problem");
                break;
            }
        }
    }

    private void ShowNotification(String msg) {
        String notif_title = null;
        Intent notifIntent = null;

        SharedPreferences mypreferences = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        String notfSound = mypreferences.getString("alertSound", "");
        String notfVibra = mypreferences.getString("alertVibrator", "");
        String notfLight = mypreferences.getString("alertLight", "");


        switch (tipo) {
            case "alarm": {
                notifIntent = new Intent(this, LoginActivity.class);
                notifIntent.putExtra("fromNotification", "true");
                notifIntent.putExtra("finalscreen", "event");

                notif_title = "NEW ALARM " + userName + " EVENT!!!";

                SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mypreference.edit();
                String alarmBagde = mypreference.getString("palarmbadge", "");
                if (alarmBagde.equals("")) {
                    alarmBagde = "0";
                }
                int bdge = Integer.parseInt(alarmBagde);
                bdge++;
                editor.putString("palarmbadge", "" + bdge);
                editor.apply();
                break;
            }
            case "invoice": {
                notifIntent = new Intent(this, MainActivity.class);
                notifIntent.putExtra("fromNotification", "true");
                notifIntent.putExtra("finalscreen", "invoice");

                notif_title = "NEW INVOICE EVENT!!!";

                SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mypreference.edit();
                String invoiceBagde = mypreference.getString("pinvbadge", "");
                if (invoiceBagde.equals("")) {
                    invoiceBagde = "0";
                }
                int bdge = Integer.parseInt(invoiceBagde);
                bdge++;
                editor.putString("pinvbadge", "" + bdge);
                editor.apply();
                break;
            }
            case "info": {
                notif_title = info_title;
                break;
            }
            case "emergency": {
                notifIntent = new Intent(this, MyMapActivity.class);
                notifIntent.putExtra("contName", contName);
                notifIntent.putExtra("contLat", clientLat);
                notifIntent.putExtra("contLong", clientLong);
                notifIntent.putExtra("contDir", clientDir);
                notifIntent.putExtra("group", group);
                notifIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                notif_title = "NEW EMERGENCY EVENT!!!";

                break;
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notifIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "SafeHouse Notification", NotificationManager.IMPORTANCE_HIGH);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "SH_CH_ID");
        notificationBuilder.setAutoCancel(true)
                //.setSmallIcon(R.drawable.shieldlogo48)
                .setTicker("Safe House Notification")
                .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                .setContentTitle(notif_title)
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setChannelId("my_channel_02");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.shieldlogo48);
            notificationBuilder.setColor(getResources().getColor(R.color.red));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.shieldlogo48);
        }

        if (!notfSound.equals("silent")){
            notificationBuilder.setSound(Uri.parse(notfSound));
        }
        if (!notfVibra.equals("Off")){
            switch (notfVibra) {
                case "Default":
                    notificationBuilder.setVibrate(new long[]{1000, 500, 1000, 500});
                    break;
                case "Short":
                    notificationBuilder.setVibrate(new long[]{250, 150, 500, 100});
                    break;
                case "Long":
                    notificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000});
                    break;
            }
        }
        if (!notfLight.equals("None")){
            switch (notfVibra) {
                case "White":
                    notificationBuilder.setLights(Color.WHITE, 0, 1);
                    break;
                case "Red":
                    notificationBuilder.setLights(Color.RED, 0, 1);
                    break;
                case "Yellow":
                    notificationBuilder.setLights(Color.YELLOW, 0, 1);
                    break;
                case "Green":
                    notificationBuilder.setLights(Color.GREEN, 0, 1);
                    break;
                case "Cyan":
                    notificationBuilder.setLights(Color.CYAN, 0, 1);
                    break;
                case "Blue":
                    notificationBuilder.setLights(Color.BLUE, 0, 1);
                    break;
                case "Purple":
                    notificationBuilder.setLights(Color.MAGENTA, 0, 1);
                    break;
            }
        }

        if(!tipo.equals("emergency")) {
            int dwbleIcon;
            switch (alarmDesc) {
                case "open":
                    notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_disarmed_48));
                    dwbleIcon = R.drawable.ic_disarmed_24;
                    break;
                case "close":
                    notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_armed_48));
                    dwbleIcon = R.drawable.ic_armed_24;
                    break;
                case "alarm":
                    notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_alarm_48));
                    dwbleIcon = R.drawable.ic_alarm_24;
                    break;
                default:
                    notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_siren_48));
                    dwbleIcon = R.drawable.ic_siren_24;
                    break;
            }
            notificationBuilder.addAction(dwbleIcon, "View", pendingIntent);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(contid++,notificationBuilder.build());
        }

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void saveInvoiceDB(){
        MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();
        dataBaseAdapter.insertEntry_I(iName, iAddress, iNo, iDate, iDueDate, iItem, iQty, iDesc, iRate, iSubTotal, iTotal, iName);

    }

    private void saveMsgDB(){
        MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();
        //dataBaseAdapter.deleteEntry("a", null);
        dataBaseAdapter.insertEntry_A(alarmMessage, alarmDate, userName, alarmZone, alarmDesc);
    }

    private void saveZoneDB(){

        MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        dataBaseAdapter.deleteEntry("z", userName);

        String[] zNo_Arr   = zoneNo.split(";");
        String[] zDesc_Arr = zoneDesc.split(";");
        if (zNo_Arr.length > 0){
            for (int i = 0; i< zNo_Arr.length; i++){
                dataBaseAdapter.insertEntry_Z(zNo_Arr[i], zDesc_Arr[i], userName ); //falta agregar el account name
            }
        }
    }

    private void saveGuestDB(){
        if (guestRole.equals("Admin")){
            SharedPreferences mypreferences = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mypreferences.edit();
            editor.putString("padmin", "false");
            editor.apply();
        }
        MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        dataBaseAdapter.insertEntry_G(guestName, guestRole);
    }

    private void DeletePreference() {
        //Deleting preferences
        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mypreference.edit();
        editor.clear();
        editor.apply();

        //Delete all the information in database
        MyDataBaseAdapter dataBaseAdapter = new MyDataBaseAdapter(this);
        dataBaseAdapter.open();
        if(dataBaseAdapter.db != null) {
            dataBaseAdapter.deletDB();
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //Toast.makeText(this, "token: " + s, Toast.LENGTH_SHORT).show();
        SharedPreferences mypreferences = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mypreferences.edit();

        editor.putString("ptoken", s);
        editor.apply();
    }
}
