package org.study.board.service;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String SECRET_KEY = "6LfG_vwpAAAAAOhYtD7xejU8iLJEB_LLgpJy2eIC";

    public boolean verifyRecaptcha(String recaptchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        String params = "?secret=" + SECRET_KEY + "&response=" + recaptchaResponse;
        String url = RECAPTCHA_VERIFY_URL + params;

        RecaptchaResponse response = restTemplate.postForObject(url, null, RecaptchaResponse.class);
        return response != null && response.isSuccess();
    }

    @Data
    static class RecaptchaResponse {
        private boolean success;
    }
}