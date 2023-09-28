package com.sagatrading.activity;

public class Activity {
    private Integer userId;
    private String userName;
    private String activity;

    public Activity(String activity) {
        this.activity = activity;
    }

    public Activity(Integer userId, String userName, String activity) {
        this.userId = userId;
        this.userName = userName;
        this.activity = activity;
    }

    public Activity(Integer userId, String activity) {
        this.userId = userId;
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
