package com.pdm.taskdone.Model;

import javax.xml.transform.sax.TemplatesHandler;

public class User_worker {

    private String email;
    private String password;
    private String name;
    private String phone;
    private String city;
    private String NIC;

    public User_worker() {
    }

    public User_worker(String email, String password, String name, String phone , String city ,String NIC) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.NIC = NIC;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNIC() {
        return NIC;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }
}
