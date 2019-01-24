package com.huangtao.meetingroom.model;

import com.huangtao.meetingroom.model.meta.MeetingType;
import com.huangtao.meetingroom.model.meta.Status;

import java.io.Serializable;
import java.util.Map;

public class Meeting implements Serializable {
    String id;

    String heading;
    String description;
    String roomId;
    String date;
    String location;
    int startTime;
    int endTime;
    String hostId;
    Map<String, String> attendants;
    boolean needSignIn;
    String attendantNum;  // a four digit number to attend the meeting
    Status status;
    MeetingType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Map<String, String> getAttendants() {
        return attendants;
    }

    public void setAttendants(Map<String, String> attendants) {
        this.attendants = attendants;
    }

    public boolean isNeedSignIn() {
        return needSignIn;
    }

    public void setNeedSignIn(boolean needSignIn) {
        this.needSignIn = needSignIn;
    }

    public String getAttendantNum() {
        return attendantNum;
    }

    public void setAttendantNum(String attendantNum) {
        this.attendantNum = attendantNum;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public MeetingType getType() {
        return type;
    }

    public void setType(MeetingType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id='" + id + '\'' +
                ", heading='" + heading + '\'' +
                ", description='" + description + '\'' +
                ", roomId='" + roomId + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", hostId='" + hostId + '\'' +
                ", attendants=" + attendants +
                ", needSignIn=" + needSignIn +
                ", attendantNum='" + attendantNum + '\'' +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
