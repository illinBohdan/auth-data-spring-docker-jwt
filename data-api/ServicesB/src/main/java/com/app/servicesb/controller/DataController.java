package com.app.servicesb.controller;

import com.app.servicesb.dto.TransformationRequest;
import com.app.servicesb.dto.TransformationResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataController {
    @PostMapping("/transform")
    public TransformationResponse transform(@RequestBody TransformationRequest request) {
        String originalText = request.getText();
        String transformedText = new StringBuilder(originalText)
                .reverse()
                .toString()
                .toUpperCase();
        return new TransformationResponse(transformedText);
    }
}
