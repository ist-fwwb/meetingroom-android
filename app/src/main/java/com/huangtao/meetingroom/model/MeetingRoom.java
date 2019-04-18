package com.huangtao.meetingroom.model;

import com.huangtao.meetingroom.model.meta.MeetingRoomUtils;
import com.huangtao.meetingroom.model.meta.Size;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class MeetingRoom implements Serializable {
    String id;
    Set<MeetingRoomUtils> utils;
    Size size;
    String location;
    String deviceId;
    List<String> images;

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = id;
    }

    public Set<MeetingRoomUtils> getUtils() {
        return utils;
    }

    public void setUtils(Set<MeetingRoomUtils> utils) {
        this.utils = utils;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
