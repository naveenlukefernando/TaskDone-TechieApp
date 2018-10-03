package com.pdm.taskdone.Model;

public class History_model {

    String workerId,WorkerName,WorkerProPic;
    String time_duration,paid_fee,address;
    String clientId,client_name,description;
    String rating,date;



    public History_model() {

    }


    public History_model(String description, String client_name, String address, String date, String paid_fee, String rating) {

        this.description = description;
        this.client_name = client_name;
        this.address = address;
        this.date = date;
        this.paid_fee = paid_fee;
        this.rating = rating;

    }


    public String getPaid_fee() {
        return paid_fee;
    }

    public void setPaid_fee(String paid_fee) {
        this.paid_fee = paid_fee;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
