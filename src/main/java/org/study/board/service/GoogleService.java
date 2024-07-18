package org.study.board.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class GoogleService {

    private final static String GOOGLE_AUTH_URI = "https://oauth2.googleapis.com";
    private final static String GOOGLE_API_URI = "https://www.googleapis.com";

    private final String googleClientId;
    private final String googleClientSecret;
    private final String googleRedirectUri;

    @Autowired
    public GoogleService(Environment env) {
        Dotenv dotenv = Dotenv.configure().load();
        this.googleClientId = dotenv.get("GOOGLE_CLIENT_ID");
        this.googleClientSecret = dotenv.get("GOOGLE_CLIENT_SECRET");
        this.googleRedirectUri = dotenv.get("GOOGLE_REDIRECT_URI");
    }

    public String getGoogleAccessToken(String code) throws Exception {
        if (code == null) throw new Exception("Failed to get authorization code");

        String access_Token = "";
        String refresh_Token = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", googleClientId);
            params.add("client_secret", googleClientSecret);
            params.add("code", code);
            params.add("redirect_uri", googleRedirectUri);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    GOOGLE_AUTH_URI + "/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            if (response.getBody() != null) {
                JsonObject jsonObj = (JsonObject) JsonParser.parseString(response.getBody());
                if (jsonObj.has("access_token")) {
                    access_Token = jsonObj.get("access_token").getAsString();
                }
                if (jsonObj.has("refresh_token")) {
                    refresh_Token = jsonObj.get("refresh_token").getAsString();
                }
            } else {
                throw new Exception("Response body is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("API call failed", e);
        }

        return access_Token;
    }

    public HashMap<String, Object> getUserInfo(String access_Token) {
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = GOOGLE_API_URI+"/userinfo/v2/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonElement element = JsonParser.parseString(result);

            String email = element.getAsJsonObject().get("email").getAsString();
            String id = element.getAsJsonObject().get("id").getAsString();
            String name = element.getAsJsonObject().get("name").getAsString();

            userInfo.put("id",id);
            userInfo.put("name", name);
            userInfo.put("email", email);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return userInfo;
    }

}