package com.charmroom.charmroom.service.oauth2;

import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.dto.oauth2.CustomOAuth2User;
import com.charmroom.charmroom.dto.oauth2.GoogleResponse;
import com.charmroom.charmroom.dto.oauth2.KakaoResponse;
import com.charmroom.charmroom.dto.oauth2.NaverResponse;
import com.charmroom.charmroom.dto.oauth2.OAuth2Response;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        System.out.println(username);
        Optional<User> existData = userRepository.findByUsername(username);

        UserDto userDto = null;
        User user = null;
        if (existData.isEmpty()) {
            user = User.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .password("aserskljd")
                    .nickname(oAuth2Response.getName())
                    .level(UserLevel.ROLE_BASIC)
                    .build();
            userRepository.save(user);
        } else {
            user = existData.get();
            user.updateEmail(oAuth2Response.getEmail());
            user.updateNickname(oAuth2Response.getName());
            userRepository.save(user);

        }
        userDto = UserMapper.toDto(user);

        return new CustomOAuth2User(userDto);
    }
}
