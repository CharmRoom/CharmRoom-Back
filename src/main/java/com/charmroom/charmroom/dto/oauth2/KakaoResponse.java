package com.charmroom.charmroom.dto.oauth2;

import java.util.HashMap;
import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakao_account;
    private final Map<String, Object> kakao_profile;

    public KakaoResponse(Map<String, Object> attributes) {

        this.attributes = attributes;
        this.kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        this.kakao_profile = (Map<String, Object>) this.kakao_account.get("profile");
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
        return kakao_account.get("email").toString();
    }

    @Override
    public String getName() {
        return kakao_profile.get("nickname").toString();
    }
}
