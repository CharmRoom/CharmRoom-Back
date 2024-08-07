package com.charmroom.charmroom.dto.oauth2;

import java.util.HashMap;
import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakao_account;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakao_account = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return kakao_account.get("account_email").toString();
    }

    @Override
    public String getName() {
        return kakao_account.get("profile_nickname").toString();
    }
}
