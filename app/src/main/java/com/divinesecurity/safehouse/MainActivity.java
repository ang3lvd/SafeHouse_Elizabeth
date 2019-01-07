package com.divinesecurity.safehouse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.divinesecurity.safehouse.accountPackage.AccountsListActivity;
import com.divinesecurity.safehouse.alarmPackage.EventListActivity;
import com.divinesecurity.safehouse.dbAdapterPackage.MyDataBaseAdapter;
import com.divinesecurity.safehouse.guestPackage.GuestListActivity;
import com.divinesecurity.safehouse.invoicePackage.InvoiceListActivity;
import com.divinesecurity.safehouse.pdfPackage.TemplatePDF;
import com.divinesecurity.safehouse.settingsPackage.SettingsActivity;
import com.divinesecurity.safehouse.toolsPackage.Tools;
import com.divinesecurity.safehouse.zonePackage.ZoneListActivity;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;

            Animation animationZoom;
    NotificationBadge invnBadge, alarmnBadge;

    private String[] header = {"Item", "Quantity", "Description", "Rate", "SubTotal"};
    private TemplatePDF templatePDF;

    int invbdge, alarmbdge;

    MyDataBaseAdapter dataBaseAdapter;

    String iName, iAddress, iDate, iDueDate, iNo, iTotal;
    String urole, upanel;

    //Buy
    Animation animationIn, animationOut;
    LinearLayout layoutBuy, layoutBuyContent;
    boolean helpShow = false;

    ImageView imlogo;

    private static Bitmap imageOriginal, imageScaled;
    private static Matrix matrix;

    private int dialerHeight, dialerWidth;

    private GestureDetector detector;
    // needed for detecting the inversed rotations
    private boolean[] quadrantTouched;
    private boolean allowRotating;

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

        SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
        String invoiceBagde = mypreference.getString("pinvbadge", "");
        String alarmBagde   = mypreference.getString("palarmbadge", "");
        String username     = mypreference.getString("puser", "");
        urole               = mypreference.getString("prole", "");
        upanel              = mypreference.getString("ppanel", "");
        if (invoiceBagde.equals("")){
            invoiceBagde = "0";
        }
        invbdge = Integer.parseInt(invoiceBagde);
        invnBadge.setNumber(invbdge);

        if (alarmBagde.equals("")){
            alarmBagde = "0";
        }
        alarmbdge = Integer.parseInt(alarmBagde);
        alarmnBadge.setNumber(alarmbdge);

        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.txt_nav_name);
        nav_user.setText(username);

        try{
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(username.toUpperCase());
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }

        layoutBuy        = findViewById(R.id.armdis_layoutBuy);
        layoutBuyContent = findViewById(R.id.armdis_layoutBuyContent);


        //
        animationIn  = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in);
        animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);

        /*TextView txtname = findViewById(R.id.txt_nav_name);
        txtname.setText(username);*/

        // create a instance of SQLite Database
        dataBaseAdapter = new MyDataBaseAdapter(getApplicationContext());
        dataBaseAdapter = dataBaseAdapter.open();

        Tools.setScreen_active("mainscreen");

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("broad-main"));




        // load the image only once
        if (imageOriginal == null) {
            imageOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.shieldlogo);
        }

        // initialize the matrix only once
        if (matrix == null) {
            matrix = new Matrix();
        } else {
            // not needed, you can also post the matrix immediately to restore the old state
            matrix.reset();
        }


        detector = new GestureDetector(this, new MyGestureDetector());
        // there is no 0th quadrant, to keep it simple the first value gets ignored
        quadrantTouched = new boolean[] { false, false, false, false, false };

        allowRotating = true;

        imlogo = findViewById(R.id.imgLogo);
        imlogo.setOnTouchListener(new MyOnTouchListener());
        imlogo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // method called more than once, but the values only need to be initialized one time
                if (dialerHeight == 0 || dialerWidth == 0) {
                    dialerHeight = imlogo.getHeight();
                    dialerWidth = imlogo.getWidth();

                    // resize
                    Matrix resize = new Matrix();
                    resize.postScale((float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getWidth(), (float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getHeight());
                    imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);

                    // translate to the image view's center
                    float translateX = dialerWidth / 2 - imageScaled.getWidth() / 2;
                    float translateY = dialerHeight / 2 - imageScaled.getHeight() / 2;
                    matrix.postTranslate(translateX, translateY);

                    imlogo.setImageBitmap(imageScaled);
                    imlogo.setImageMatrix(matrix);

                }
            }
        });

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



    public void store(View view) {
        view.startAnimation(animationZoom);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.dsag_url)));
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

    public void shoplink(View view) {
        view.startAnimation(animationZoom);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.dsag_url)));
        startActivity(intent);
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

            if (ptype.equals("alarm")) {
                SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mypreference.edit();
                String alarmBagde = mypreference.getString("palarmbadge", "");
                if (alarmBagde.equals("")) {
                    alarmBagde = "0";
                }
                int bdge = Integer.parseInt(alarmBagde);
                bdge++;
                editor.putString("palarmbadge", ""+bdge);
                editor.apply();
                alarmnBadge.setNumber(bdge);
            } else if (ptype.equals("invoice")) {
                SharedPreferences mypreference = getSharedPreferences("UserPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mypreference.edit();
                String invBagde = mypreference.getString("pinvbadge", "");
                if (invBagde.equals("")) {
                    invBagde = "0";
                }
                int bdge = Integer.parseInt(invBagde);
                bdge++;
                editor.putString("pinvbadge", ""+bdge);
                editor.apply();
                invnBadge.setNumber(bdge);
            }
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
            if (urole.equals("Client") || urole.equals("Client")) {
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
                /*if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission to call denied", Toast.LENGTH_SHORT).show();
                }*/
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.cellDS))));
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * @return The angle of the unit circle with the image view's center
     */
    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - (dialerWidth / 2d);
        double y = dialerHeight - yTouch - (dialerHeight / 2d);

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    /**
     * @return The selected quadrant.
     */
    private int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    /**
     * Rotate the dialer.
     *
     * @param degrees The degrees, the dialer should get rotated.
     */
    private void rotateDialer(float degrees) {
        matrix.postRotate(degrees, dialerWidth / 2, dialerHeight / 2);

        imlogo.setImageMatrix(matrix);
    }

    private class MyOnTouchListener implements View.OnTouchListener {

        private double startAngle;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // reset the touched quadrants
                    for (int i = 0; i < quadrantTouched.length; i++) {
                        quadrantTouched[i] = false;
                    }

                    allowRotating = false;

                    startAngle = getAngle(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_MOVE:
                    double currentAngle = getAngle(event.getX(), event.getY());
                    rotateDialer((float) (startAngle - currentAngle));
                    startAngle = currentAngle;
                    break;

                case MotionEvent.ACTION_UP:
                    allowRotating = true;
                    break;
            }

            // set the touched quadrant to true
            quadrantTouched[getQuadrant(event.getX() - (dialerWidth / 2), dialerHeight - event.getY() - (dialerHeight / 2))] = true;

            detector.onTouchEvent(event);

            return true;
        }
    }

    /**
     * Simple implementation of a {link SimpleOnGestureListener} for detecting a fling event.
     */
    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // get the quadrant of the start and the end of the fling
            int q1 = getQuadrant(e1.getX() - (dialerWidth / 2), dialerHeight - e1.getY() - (dialerHeight / 2));
            int q2 = getQuadrant(e2.getX() - (dialerWidth / 2), dialerHeight - e2.getY() - (dialerHeight / 2));

            // the inversed rotations
            if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math.abs(velocityY))
                    || (q1 == 3 && q2 == 3)
                    || (q1 == 1 && q2 == 3)
                    || (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math.abs(velocityY))
                    || ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
                    || ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
                    || (q1 == 2 && q2 == 4 && quadrantTouched[3])
                    || (q1 == 4 && q2 == 2 && quadrantTouched[3])) {

                imlogo.post(new FlingRunnable(-1 * (velocityX + velocityY)));
            } else {
                // the normal rotation
                imlogo.post(new FlingRunnable(velocityX + velocityY));
            }
            return true;
        }
    }

    /**
     * A {@link Runnable} for animating the the dialer's fling.
     */
    private class FlingRunnable implements Runnable {

        private float velocity;

        FlingRunnable(float velocity) {
            this.velocity = velocity;
        }

        @Override
        public void run() {
            if (Math.abs(velocity) > 5  && allowRotating) {
                rotateDialer(velocity / 75);
                velocity /= 1.0666F;

                // post this instance again
                imlogo.post(this);
            }
        }
    }

}
