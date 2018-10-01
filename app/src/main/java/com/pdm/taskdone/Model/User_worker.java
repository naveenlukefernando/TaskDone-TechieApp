package com.pdm.taskdone.Model;

import javax.xml.transform.sax.TemplatesHandler;

public class User_worker {

    private String id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String city;
    private String NIC;
    private String profession;
    private String pro_pic_URL;

    public User_worker() {
    }

    public User_worker(String id,String email, String password, String name, String phone , String city ,String NIC,String pro_pic_URL, String profession) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.NIC = NIC;
        this.profession = profession;
        this.pro_pic_URL = pro_pic_URL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getPro_pic_URL() {
        return pro_pic_URL;
    }

    public void setPro_pic_URL(String pro_pic_URL) {
        this.pro_pic_URL = pro_pic_URL;
    }
}
