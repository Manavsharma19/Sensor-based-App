package com.find.me.model;

public class User {
    private String username;
    private String password;
    private String phone_number;
    private String DOB;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;


    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getIqama() {
        return iqama;
    }

    public void setIqama(String iqama) {
        this.iqama = iqama;
    }

    private String iqama;

    public User(String aUser, String aPass, String aIqama, String aDob,String aPhone){
        this.username = aUser;
        this.password = aPass;
        this.phone_number = aPhone;
        this.iqama = aIqama;
        this.DOB = aDob;
    }
    public User(String aUser, String aPass){
        this.username = aUser;
        this.password = aPass;
    }
    public User(String aUser, String nname, String phone_no){
        this.username = aUser;
        this.name = nname;
        this.phone_number = phone_no;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
