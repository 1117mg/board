package org.study.board.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class NaverService {

    private final static String NAVER_AUTH_URI = "https://nid.naver.com";
    private final static String NAVER_API_URI = "https://openapi.naver.com";

    public String getNaverAccessToken (String code) throws Exception{
        if (code == null) throw new Exception("Failed get authorization code");

        String access_Token = "";
        String refresh_Token = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type"   , "authorization_code");
            params.add("client_id"    , "_Cc29jbVJrmPVE5B8SfS");
            params.add("client_secret", "A9kaJoE_0L");
            params.add("code"         , code);
            params.add("redirect_uri" , "http://localhost:8080/naver-login");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    NAVER_AUTH_URI + "/oauth2.0/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            JsonObject jsonObj = (JsonObject) JsonParser.parseString(response.getBody());
            access_Token = String.valueOf(jsonObj.get("access_token"));
            refresh_Token = String.valueOf(jsonObj.get("refresh_token"));
        } catch (Exception e) {
            throw new Exception("API call failed");
        }

        return access_Token;
    }

    public HashMap<String, Object> getUserInfo(String access_Token) {
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = NAVER_API_URI+"/v1/nid/me";
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
            JsonObject naver_account = element.getAsJsonObject().get("response").getAsJsonObject();

            String email = naver_account.get("email").getAsString();
            String id = naver_account.get("id").getAsString();
            String name = naver_account.get("name").getAsString();

            userInfo.put("id",id);
            userInfo.put("name", name);
            userInfo.put("email", email);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return userInfo;
    }

}