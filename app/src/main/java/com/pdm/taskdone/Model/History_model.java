package com.pdm.taskdone.Model;

public class History_model {

    String workerId,WorkerName,WorkerProPic;
    String time_duration,paid_fee,worker_city;
    String clientId,clientName,description;
    String type, rating,date;



    public History_model() {

    }

//    public History_model(String date,String workerId, String workerName, String workerProPic, String time_duration, String paid_fee, String worker_city, String clientId, String clientName, String description, String type, String rating) {
//        this.workerId = workerId;
//        this.WorkerName = workerName;
//        this.WorkerProPic = workerProPic;
//        this.time_duration = time_duration;
//        this.paid_fee = paid_fee;
//        this.date = date;
//        this.worker_city = worker_city;
//        this.clientId = clientId;
//        this.clientName = clientName;
//        this.description = description;
//        this.type = type;
//        this.rating = rating;
//    }


    public History_model(String paid_fee, String worker_city, String clientName, String description, String rating, String date) {
        this.worker_city = worker_city;
        this.clientName = clientName;
        this.description = description;
        this.rating = rating;
        this.date = date;
        this.paid_fee = paid_fee;
    }

    public String getPaid_fee() {
        return paid_fee;
    }

    public void setPaid_fee(String paid_fee) {
        this.paid_fee = paid_fee;
    }

    public String getWorker_city() {
        return worker_city;
    }

    public void setWorker_city(String worker_city) {
        this.worker_city = worker_city;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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
