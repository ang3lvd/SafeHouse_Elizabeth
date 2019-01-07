package com.divinesecurity.safehouse.zonePackage;

/**
 * Created by ang3l on 7/3/2018.
 */

public class ZoneDataModel {

    private String zno;
    private String zdesc;
    private String zacc;

    ZoneDataModel(String zno, String zdesc, String zacc) {
        this.zno = zno;
        this.zdesc = zdesc;
        this.zacc = zacc;
    }

    public String getNo() {
        return zno;
    }

    public String getDesc() {
        return zdesc;
    }

    public String getAcc() {
        return zacc;
    }

}
