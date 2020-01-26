package lv.events.security;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class AccessTokenValidator {

    @Value("${spring.social.facebook.appId}")
    private String appId;

    @Value("${spring.social.facebook.appSecret}")
    private String appSecret;

    public AccessValidationResult validateAccessToken (String clientAccessToken) throws InvalidAccessTokenException {
        RestTemplate restTemplate = new RestTemplate();
        String appAccessTokenUrl = String.format("https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&grant_type=client_credentials", appId, appSecret);
        ResponseEntity<String> response = restTemplate.getForEntity(appAccessTokenUrl, String.class);
        JSONObject jsonObject = new JSONObject(response.getBody());
        String appAccessToken = jsonObject.getString("access_token");

        String validateAccessTokenUrl = String.format("https://graph.facebook.com/debug_token?input_token=%s&access_token=%s", clientAccessToken, appAccessToken);
        response = restTemplate.getForEntity(validateAccessTokenUrl, String.class);
        jsonObject = new JSONObject(response.getBody());
        val data = jsonObject.getJSONObject("data");
        boolean isValid = data.getBoolean("is_valid");

        if (!isValid) {
            throw new InvalidAccessTokenException();
        }
        String userId = data.getString("user_id");
        return new AccessValidationResult(userId, isValid);
    }

}
