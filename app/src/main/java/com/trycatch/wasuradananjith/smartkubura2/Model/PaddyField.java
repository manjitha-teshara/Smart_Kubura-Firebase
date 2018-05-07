package com.trycatch.wasuradananjith.smartkubura2.Model;

public class PaddyField {
    private String waterLevel;
    private String paddyFieldName;
    private String imeiNumber;
    private String phone;

    public PaddyField() {
    }

    public PaddyField(String waterLevel, String paddyFieldName, String imeiNumber, String phone) {
        this.waterLevel = waterLevel;
        this.paddyFieldName = paddyFieldName;
        this.imeiNumber = imeiNumber;
        this.phone = phone;
    }

    public String getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(String waterLevel) {
        this.waterLevel = waterLevel;
    }

    public String getPaddyFieldName() {
        return paddyFieldName;
    }

    public void setPaddyFieldName(String paddyFieldName) {
        this.paddyFieldName = paddyFieldName;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
