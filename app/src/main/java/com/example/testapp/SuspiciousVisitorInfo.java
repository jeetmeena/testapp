package com.example.testapp;

public class SuspiciousVisitorInfo {
    private String mSuspiciousUserPhoneNo;
    private String mSuspiciousUserPhotoURL;
    private String mSuspiciousVistorId;
    public SuspiciousVisitorInfo() {
    }

    public SuspiciousVisitorInfo(String mSuspiciousUserPhoneNo, String mSuspiciousUserPhotoURL, String mSuspiciousVistorId) {
        this.mSuspiciousUserPhoneNo = mSuspiciousUserPhoneNo;
        this.mSuspiciousUserPhotoURL = mSuspiciousUserPhotoURL;
        this.mSuspiciousVistorId = mSuspiciousVistorId;
    }

    public String getmSuspiciousUserPhoneNo() {
        return mSuspiciousUserPhoneNo;
    }

    public void setmSuspiciousUserPhoneNo(String mSuspiciousUserPhoneNo) {
        this.mSuspiciousUserPhoneNo = mSuspiciousUserPhoneNo;
    }

    public String getmSuspiciousUserPhotoURL() {
        return mSuspiciousUserPhotoURL;
    }

    public void setmSuspiciousUserPhotoURL(String mSuspiciousUserPhotoURL) {
        this.mSuspiciousUserPhotoURL = mSuspiciousUserPhotoURL;
    }

    public String getmSuspiciousVistorId() {
        return mSuspiciousVistorId;
    }

    public void setmSuspiciousVistorId(String mSuspiciousVistorId) {
        this.mSuspiciousVistorId = mSuspiciousVistorId;
    }
}
