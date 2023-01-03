package com.find.me.model;

public class Danger {
    String email;
    String latitude;
    String longitude;
    String paragraph;
    String title;
    String phone;
    String iqama;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIqama() {
        return iqama;
    }

    public void setIqama(String iqama) {
        this.iqama = iqama;
    }

    public String getRelname() {
        return relname;
    }

    public void setRelname(String relname) {
        this.relname = relname;
    }

    public String getRelphone() {
        return relphone;
    }

    public void setRelphone(String relphone) {
        this.relphone = relphone;
    }

    String relname;
    String relphone;

    public Danger(){
    }

    public Danger(String aemail, String alat,String along, String atitle, String aparagraph, String aphone, String aiqama, String arelname, String arelphone){
        this.email = aemail;
        this.latitude = alat;
        this.longitude = along;
        this.paragraph = aparagraph;
        this.title = atitle;
        this.phone = aphone;
        this.iqama = aiqama;
        this.relname = arelname;
        this.relphone = arelphone;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
