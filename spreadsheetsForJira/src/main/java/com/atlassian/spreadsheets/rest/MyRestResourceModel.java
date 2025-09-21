package com.atlassian.spreadsheets.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class MyRestResourceModel {

    @XmlElement(name = "value")
    private String message;

    public MyRestResourceModel() {
    }

    public MyRestResourceModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}