package com.labibkamran.nustfruta.Model;

public class NotificationModel {
    private String userId;
    private String message;
    private int imageResourceId;

    public NotificationModel() {
        // Default constructor required for calls to DataSnapshot.getValue(NotificationModel.class)
    }

    public NotificationModel(String message, int imageResourceId) {
        this.message = message;
        this.imageResourceId = imageResourceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
