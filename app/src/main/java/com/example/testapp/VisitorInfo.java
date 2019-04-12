package com.example.testapp;

class VisitorInfo {
    private String mVistorInfoPhoneNo;
    private String mPhotoInfoUri;
    private String mVisitorInfoName;
    private String mVistorInfoId;
    private int visit_count;
    public VisitorInfo() {
    }

    public VisitorInfo(int visit_count, String mVistorInfoPhoneNo, String mPhotoInfoUri, String mVisitorInfoName, String mVistorInfoId) {
        this.mVistorInfoPhoneNo = mVistorInfoPhoneNo;
        this.mPhotoInfoUri = mPhotoInfoUri;
        this.mVisitorInfoName = mVisitorInfoName;
        this.mVistorInfoId = mVistorInfoId;
        this.visit_count = visit_count;
    }

    public String getmVistorInfoPhoneNo() {
        return mVistorInfoPhoneNo;
    }

    public void setmVistorInfoPhoneNo(String mVistorInfoPhoneNo) {
        this.mVistorInfoPhoneNo = mVistorInfoPhoneNo;
    }

    public String getmPhotoInfoUri() {
        return mPhotoInfoUri;
    }

    public void setmPhotoInfoUri(String mPhotoInfoUri) {
        this.mPhotoInfoUri = mPhotoInfoUri;
    }

    public String getmVisitorInfoName() {
        return mVisitorInfoName;
    }

    public void setmVisitorInfoName(String mVisitorInfoName) {
        this.mVisitorInfoName = mVisitorInfoName;
    }

    public String getmVistorInfoId() {
        return mVistorInfoId;
    }

    public void setmVistorInfoId(String mVistorInfoId) {
        this.mVistorInfoId = mVistorInfoId;
    }

    public int getVisit_count() {
        return visit_count;
    }

    public void setVisit_count(int visit_count) {
        this.visit_count = visit_count;
    }
}
