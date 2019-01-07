package com.divinesecurity.safehouse.guestPackage;

/**
 * Created by ang3l on 6/3/2018.
 */

public class GuestDataModel {

    private String gName;
    private String gNo;
    private String gRole;
    private boolean gSelect;
    private String iacc;

    GuestDataModel(String no, String name, String role, String iacc, boolean select) {
        this.gName = name;
        this.gNo = no;
        this.gRole = role;
        this.gSelect = select;
        this.iacc = iacc;
    }

    public String getgName() {
        return gName;
    }

    /*public String getgNo() {
        return gNo;
    }*/

    public String getgRole(){ return gRole; }


    public boolean getgSelect() {
        return gSelect;
    }

    public void setgSelect(boolean gSelect) {
        this.gSelect = gSelect;
    }

    /*public String getAcc() {
        return iacc;
    }*/
}
