package com.huangtao.meetingroom.model;

import com.huangtao.meetingroom.model.meta.Type;

public class User {
    String id;
    String enterpriceId;
    String phone;
    String password;
    String name;
    Type type;
    String faceFile;
    String featureFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFaceFile() {
        return faceFile;
    }

    public void setFaceFile(String faceFile) {
        this.faceFile = faceFile;
    }

    public String getFeatureFile() {
        return featureFile;
    }

    public void setFeatureFile(String featureFile) {
        this.featureFile = featureFile;
    }

    public String getEnterpriceId() {
        return enterpriceId;
    }

    public void setEnterpriceId(String enterpriceId) {
        this.enterpriceId = enterpriceId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
