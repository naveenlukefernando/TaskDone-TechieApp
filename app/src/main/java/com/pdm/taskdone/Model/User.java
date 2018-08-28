package com.pdm.taskdone.Model;

public class User {

    private String email;
    private String password;
    private String fname;
    private String lname;
    private String phone_num;

    public User() {
    }

    public User(String email, String password, String fname, String lname, String phone_num) {
        this.email = email;
        this.password = password;
        this.fname = fname;
        this.phone_num = phone_num;
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

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }


    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }
}
