package com.trycatch.wasuradananjith.smartkubura2.Model;

public class PaddyField {
    private String waterLevel;
    private String paddyFieldName;
    private String imeiNumber;
    private String phone;
    private Integer isFilling;

    public PaddyField() {
    }

    public PaddyField(String waterLevel, String paddyFieldName, String imeiNumber, String phone,Integer isFilling) {
        this.waterLevel = waterLevel;
        this.paddyFieldName = paddyFieldName;
        this.imeiNumber = imeiNumber;
        this.phone = phone;
        this.isFilling = isFilling;
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

    public Integer getIsFilling() {
        return isFilling;
    }

    public void setIsFilling(Integer isFilling) {
        this.isFilling = isFilling;
    }
}
