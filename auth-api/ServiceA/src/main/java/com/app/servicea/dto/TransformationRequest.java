package com.app.servicea.dto;

public class TransformationRequest {

    private String text;

    public TransformationRequest() {
    }

    public TransformationRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
