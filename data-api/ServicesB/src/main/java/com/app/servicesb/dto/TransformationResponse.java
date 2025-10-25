package com.app.servicesb.dto;

public class TransformationResponse {

    private String result;

    public TransformationResponse() {
            }

    public TransformationResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
