package com.divinesecurity.safehouse.alarmPackage;

/**
 * Created by ang3l on 6/3/2018.
 */

public class AlarmDataModel {

    private String aMsg;
    private String aDate;
    private String aName;
    private String aZone;
    private String aDesc;

    AlarmDataModel(String msg, String dt, String name, String zone, String desc) {
        this.aMsg = msg;
        this.aDate = dt;
        this.aName = name;
        this.aZone = zone;
        this.aDesc = desc;
    }

    public String getaMsg() {
        return aMsg;
    }

    public String getaDate() {
        return aDate;
    }

    public String getaName() {
        return aName;
    }

    public String getaZone() {
        return aZone;
    }

    public String getaDesc() {
        return aDesc;
    }
}
