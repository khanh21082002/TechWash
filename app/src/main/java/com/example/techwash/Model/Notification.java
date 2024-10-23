package com.example.techwash.Model;

public class Notification {

    private String content;
    private String receiverId;
    private String status;

    public Notification() {
    }

    public Notification(String content, String receiverId, String status) {
        this.content = content;
        this.receiverId = receiverId;
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
