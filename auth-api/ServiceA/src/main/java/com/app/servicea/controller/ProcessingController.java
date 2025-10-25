package com.app.servicea.controller;

import com.app.servicea.dto.TransformationRequest;
import com.app.servicea.dto.TransformationResponse;
import com.app.servicea.entity.ProcessingLog;
import com.app.servicea.entity.Users;
import com.app.servicea.repository.ProcessingLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class ProcessingController {

    private final RestTemplate restTemplate;
    private final ProcessingLogRepository logRepository;

    @Value("${service.data-api-url}")
    private String dataApiUrl;

    @Value("${service.internal-token}")
    private String internalToken;

    public ProcessingController(RestTemplate restTemplate, ProcessingLogRepository logRepository) {
        this.restTemplate = restTemplate;
        this.logRepository = logRepository;
    }

    // Захищена кінцева точка
    @PostMapping("/process")
    public TransformationResponse processData(@RequestBody TransformationRequest request) {

        // 1. Отримання ID користувача з контексту безпеки
        // Примітка: JwtFilter гарантує, що Principal є об'єктом User
        Users currentUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Підготовка заголовків для виклику Service B
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Token", internalToken);
        HttpEntity<TransformationRequest> requestEntity = new HttpEntity<>(request, headers);

        TransformationResponse response;
        try {
            // 3. Виклик Service B
            String url = dataApiUrl + "/api/transform";
            ResponseEntity<TransformationResponse> responseEntity = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, TransformationResponse.class);

            response = responseEntity.getBody();

        } catch (Exception e) {
            // Обробка помилок (наприклад, якщо Service B недоступний)
            throw new RuntimeException("Error calling Service B: " + e.getMessage());
        }

        // 4. Логування обробки
        ProcessingLog log = new ProcessingLog();
        log.setUserId(currentUser.getId());
        log.setInputText(request.getText());
        log.setOutputText(response.getResult());
        log.setCreatedAt(LocalDateTime.now());

        logRepository.save(log);

        return response;
    }
}
