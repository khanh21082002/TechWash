package com.example.techwash.Model;

import java.io.Serializable;

public class TimeSlot implements Serializable {
    private String id; // Nhận diện từng slot
    private String userId; // ID người dùng
    private String autoId; // Liên kết với Auto
    private String date; // Ngày đặt
    private String startTime; // Thời gian bắt đầu
    private String endTime; // Thời gian kết thúc
    private SlotStatus status; // Trạng thái của slot

    // Enum cho trạng thái
    public enum SlotStatus {
        AVAILABLE, // Slot có sẵn
        BOOKED     // Slot đã được đặt
    }

    // Constructor mặc định
    public TimeSlot() {
        this.status = SlotStatus.AVAILABLE; // Mặc định là có sẵn
    }

    // Constructor đầy đủ
    public TimeSlot(String id, String userId, String autoId, String startTime, String endTime, SlotStatus status) {
        this.id = id;
        this.userId = userId;
        this.autoId = autoId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getter và Setter cho autoId
    public String getAutoId() {
        return autoId;
    }

    public void setAutoId(String autoId) {
        this.autoId = autoId;
    }

    // Getter và Setter khác
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
    }
}
