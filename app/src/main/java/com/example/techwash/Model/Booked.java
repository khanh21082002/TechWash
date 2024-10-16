package com.example.techwash.Model;

import java.io.Serializable;

public class Booked implements Serializable {
    private String autoId;
    private String userId;
    private String timeSlotId;


    public Booked() {
        // Constructor rỗng cho Firestore
    }
    public Booked(String autoId, String userId, String timeSlotId) {
        this.autoId = autoId;
        this.userId = userId;
        this.timeSlotId = timeSlotId;}

    public String getAutoId() {
        return autoId;
    }

    public void setAutoId(String autoId) {
        this.autoId = autoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(String timeSlotId) {
        this.timeSlotId = timeSlotId;
    }
}
