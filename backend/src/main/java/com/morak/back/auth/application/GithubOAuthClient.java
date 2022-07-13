package com.morak.back.auth.application;

import com.morak.back.auth.application.dto.OAuthAccessTokenRequest;
import com.morak.back.auth.application.dto.OAuthAccessTokenResponse;
import com.morak.back.auth.application.dto.OAuthMemberInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubOAuthClient implements OAuthClient {

    private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String MEMBER_INFO_URL = "https://api.github.com/user";

    private static final RestTemplate restTemplate = new RestTemplate();

    private final String clientId;
    private final String clientSecret;

    public GithubOAuthClient(@Value("${security.oauth.github.client-id}") String clientId,
                             @Value("${security.oauth.github.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public OAuthAccessTokenResponse getAccessToken(String code) {
        // TODO: 2022/07/13 불필요한 header 설정 제거
//        HttpHeaders headers = new HttpHeaders();
//        HttpEntity<OAuthAccessTokenRequest> request = new HttpEntity<>(
//                new OAuthAccessTokenRequest(clientId, clientSecret, code),
//                headers
//        );
//
//        return restTemplate.postForObject(ACCESS_TOKEN_URL, request, OAuthAccessTokenResponse.class);

        return restTemplate.postForObject(
                ACCESS_TOKEN_URL,
                new OAuthAccessTokenRequest(clientId, clientSecret, code),
                OAuthAccessTokenResponse.class
        );
    }


    @Override
    public OAuthMemberInfoResponse getMemberInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<OAuthMemberInfoResponse> response = restTemplate.exchange(
                MEMBER_INFO_URL,
                HttpMethod.GET,
                request,
                OAuthMemberInfoResponse.class
        );

        return response.getBody();
    }
}
