package com.divinesecurity.safehouse.dbAdapterPackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ang3l on 3/3/2018.
 */

public class MyDataBaseAdapter {

    static final String I_TABLE_NAME = "INVOICES";
    static final String A_TABLE_NAME = "ALARMS";
    static final String Z_TABLE_NAME = "ZONES";
    static final String G_TABLE_NAME = "GUESTS";
    static final String ACC_TABLE_NAME = "ACCOUNTS";

    // TODO: Create public field for each column in your table.
    // SQL Statement to create a new database.
    static final String DATABASE_CREATE_I = "create table "+I_TABLE_NAME+
            "(IDI integer primary key autoincrement, ACCNAME text not null, ACCADDRESS text, INVNO text not null, INVDATE text, INVDUEDATE text, " +
            "INVITEM text, INVQTY text, INVDESC text, INVRATE text, INVSUBTOTAL text, INVTOTAL text, ACCOUNTNAME text);";
    static final String DATABASE_CREATE_A = "create table "+A_TABLE_NAME+
            "(IDA integer primary key autoincrement, AMSG text not null, ADATE text not null, ACCOUNTNAME text not null, AZONE text default '99', ADESC text 'test');";
    static final String DATABASE_CREATE_Z = "create table "+Z_TABLE_NAME+
            "(IDZ integer primary key autoincrement, ZNO text not null, ZDESC text, ACCOUNTNAME text);";
    static final String DATABASE_CREATE_G = "create table "+G_TABLE_NAME+
            "(IDG integer primary key autoincrement, GUESTNAME text not null, GUESTROLE text, ACCOUNTNAME text);";
    static final String DATABASE_CREATE_ACC = "create table "+ACC_TABLE_NAME+
            "(IDACC integer primary key autoincrement, ACCOUNTNAME text not null, PASSW text, SPASSW text, ROLE text, EMAIL text, ACCNO text, ISADMIN text);";

    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Database open/upgrade helper
    private DataBaseHelper dbHelper;
    private Context context;

    public  MyDataBaseAdapter(Context _context)
    {
        // Context of the application using the database.
        context = _context;
        dbHelper = new DataBaseHelper(context);
    }
    public  MyDataBaseAdapter open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
        db.close();
    }

    public void deletDB(){
        context.deleteDatabase(db.getPath());
    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    //INSERT FUNCTIONS
    public void insertEntry_I(String accName, String accAddress, String invNo, String invDate, String invDueDate, String invItem, String invQty,
                              String invDesc, String invRate, String invSubtotal, String invTotal, String iAcc)
    {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("ACCNAME",accName);
        newValues.put("ACCADDRESS", accAddress);
        newValues.put("INVNO",invNo);
        newValues.put("INVDATE",invDate);
        newValues.put("INVDUEDATE",invDueDate);
        newValues.put("INVITEM",invItem);
        newValues.put("INVQTY", invQty);
        newValues.put("INVDESC",invDesc);
        newValues.put("INVRATE",invRate);
        newValues.put("INVSUBTOTAL",invSubtotal);
        newValues.put("INVTOTAL",invTotal);
        newValues.put("ACCOUNTNAME",iAcc);

        // Insert the row into your table
        db.insert(I_TABLE_NAME, null, newValues);
    }

    public void insertEntry_A(String alarmMsg, String aDate, String aAcc, String aZone, String aDesc)
    {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("AMSG",alarmMsg);
        newValues.put("ADATE",aDate);
        newValues.put("ACCOUNTNAME",aAcc);
        newValues.put("AZONE",aZone);
        newValues.put("ADESC",aDesc);

        // Insert the row into your table
        db.insert(A_TABLE_NAME, null, newValues);
    }

    public void insertEntry_Z(String zNo, String zDesc, String zAcc)
    {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("ZNO",zNo);
        newValues.put("ZDESC",zDesc);
        newValues.put("ACCOUNTNAME",zAcc);

        // Insert the row into your table
        db.insert(Z_TABLE_NAME, null, newValues);
    }

    public void insertEntry_G(String gName, String gRole)
    {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        //newValues.put("IDG",gNo);
        newValues.put("GUESTNAME",gName);
        newValues.put("GUESTROLE",gRole);

        // Insert the row into your table
        db.insert(G_TABLE_NAME, null, newValues);
    }

    public void insertEntry_ACC(String accName, String accpassw, String accspassw, String accrole, String accemail, String accno, String isadmin)
    {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("ACCOUNTNAME",accName);
        newValues.put("PASSW",accpassw);
        newValues.put("SPASSW",accspassw);
        newValues.put("ROLE",accrole);
        newValues.put("EMAIL",accemail);
        newValues.put("ACCNO",accno);
        newValues.put("ISADMIN",isadmin);

        // Insert the row into your table
        db.insert(ACC_TABLE_NAME, null, newValues);
    }


    //DELET FUNCTIONS
    public void deleteEntry(String table, String entry)
    {
        //String id=String.valueOf(ID);
        String where="ID=?";
        String tabla = " ";
        switch (table) {
            case "i":
                tabla = I_TABLE_NAME;
                where = "INVNO=?";
                break;
            case "a":
                tabla = A_TABLE_NAME;
                where = "ADATE=?";
                break;
            case "z":
                tabla = Z_TABLE_NAME;
                where = "ACCOUNTNAME=?";
                break;
            case "g":
                tabla = G_TABLE_NAME;
                where = "GUESTNAME=?";
                break;
            case "acc":
                tabla = ACC_TABLE_NAME;
                where = "ACCOUNTNAME=?";
                break;
        }

        if (entry == null){
            db.delete(tabla, "1", null);
        }

        db.delete(tabla, where, new String[]{entry});
    }


    public void deleteZonesFromAcc(String accname){
        /*String deleteM_query = "DELETE FROM "+ Z_TABLE_NAME + " WHERE ACCOUNTNAME=?";
        try {
            Cursor cursor = db.rawQuery(deleteM_query, new String[]{accname});
            cursor.close();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }*/

        db.delete(Z_TABLE_NAME, "ACCOUNTNAME=?", new String[]{accname});
    }

    //GENERAL GET FUNCTIONS
    public Cursor getEntry_I_List()
    {
        return db.query(I_TABLE_NAME,  new String[]{"INVNO", "INVDATE", "INVTOTAL", "ACCOUNTNAME"}, null, null, null, null, "INVDATE DESC");
    }

    public Cursor getEntry_A_List()
    {
        return db.query(A_TABLE_NAME,  new String[]{"AMSG", "ADATE", "ACCOUNTNAME, AZONE, ADESC"}, null, null, null, null, "IDA DESC");
    }

    public Cursor getEntry_Z_List()
    {
        return db.query(Z_TABLE_NAME,  new String[]{"ZNO", "ZDESC", "ACCOUNTNAME"}, null, null, null, null, null);
    }

    public Cursor getEntry_G_List()
    {
        return db.query(G_TABLE_NAME,  new String[]{"IDG", "GUESTNAME", "GUESTROLE", "ACCOUNTNAME"}, null, null, null, null, null);
    }

    public Cursor getEntry_I(String invNo)
    {
        return db.query(I_TABLE_NAME, new String[]{"ACCNAME", "ACCADDRESS", "INVNO", "INVDATE", "INVDUEDATE", "INVITEM", "INVQTY", "INVDESC", "INVRATE", "INVSUBTOTAL", "INVTOTAL"}, " INVNO=?", new String[]{invNo}, null, null, null);
    }

    public Cursor getEntry_Acc_List()
    {
        return db.query(ACC_TABLE_NAME,  new String[]{"ACCOUNTNAME"}, null, null, null, null, null);
    }

    public Cursor getEntry_AllAcc_List()
    {
        return db.query(ACC_TABLE_NAME,  new String[]{"ACCOUNTNAME", "ACCNO"}, null, null, null, null, null);
    }


    public Cursor getLastInvoice(){
        //(tareas) cambiar el rawquery para query
        String selectM_query = "SELECT * FROM "+ I_TABLE_NAME + " ORDER BY IDI DESC LIMIT 1";
        return db.rawQuery(selectM_query, null);
    }





}
