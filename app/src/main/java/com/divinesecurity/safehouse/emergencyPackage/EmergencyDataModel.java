package com.divinesecurity.safehouse.emergencyPackage;

/**
 * Created by ang3l on 4/3/2018.
 */

public class EmergencyDataModel {
    private String eName;
    private boolean eStatus;
    private String eDate;
    private String eAddress;
    private String eLatitude;
    private String eLongitude;
    private String eAcc;
    private String eID;

    public EmergencyDataModel(String name, boolean estatus, String edate, String eaddress, String elatitude, String elongitude, String eacc, String eid) {
        this.eName = name;
        this.eStatus = estatus;
        this.eDate = edate;
        this.eAddress = eaddress;
        this.eLatitude = elatitude;
        this.eLongitude = elongitude;
        this.eAcc = eacc;
        this.eID = eid;
    }

    public String getEName() {
        return eName;
    }

    public String getEDate() {
        return eDate;
    }

    public boolean getEStatus() {
        return eStatus;
    }

    public String getEAddress() {
        return eAddress;
    }

    public String getELatitude() {
        return eLatitude;
    }

    public String getELongitude() {
        return eLongitude;
    }

    public String getEAcc() {
        return eAcc;
    }

    public String getEID() {
        return eID;
    }
}
