package io.github.suho149.upbid.global.config;

import io.github.suho149.upbid.domain.user.entity.User;
import io.github.suho149.upbid.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 구글 로그인 시 email이 null인 경우 예외 처리 (카카오는 심사 전까지 null일 수 있음)
        if (attributes.getEmail() == null && "google".equals(registrationId)) {
            throw new OAuth2AuthenticationException("Email not found from Google");
        }

        User user = saveOrUpdate(attributes);

        // OAuth2User를 생성할 때, attributes에 email을 직접 추가해줍니다.
        // 이렇게 해야 SuccessHandler에서 oAuth2User.getAttribute("email")로 값을 꺼낼 수 있습니다.
        Map<String, Object> customAttributes = new java.util.HashMap<>(attributes.getAttributes());
        customAttributes.put("email", attributes.getEmail());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                customAttributes,
                attributes.getNameAttributeKey()
        );
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        // 이메일이 없는 경우(카카오 심사 전) 고유 ID를 기반으로 사용자를 찾도록 대체 로직 고려 가능
        // 지금은 이메일이 필수라고 가정합니다.
        String email = attributes.getEmail();
        if (email == null) {
            // 임시로 provider의 고유 ID를 이메일 대신 사용하거나, 예외를 발생시킬 수 있습니다.
            // 여기서는 예외를 발생시키겠습니다.
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }
}
