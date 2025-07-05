package io.github.suho149.upbid.global.config;

import io.github.suho149.upbid.global.jwt.JwtTokenProvider;
import io.github.suho149.upbid.global.jwt.TokenInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 7;
    private static final String REDIRECT_URI = "http://localhost:5173/oauth2/redirect";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 1. Access/Refresh Token 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(email, authorities);
        log.info("로그인 성공. Refresh Token 발급. User: {}", email);

        // 2. Refresh Token을 Redis에 저장
        redisTemplate.opsForValue().set(
                email,
                tokenInfo.refreshToken(),
                REFRESH_TOKEN_VALIDITY_SECONDS,
                TimeUnit.SECONDS
        );

        // 3. Refresh Token을 HttpOnly 쿠키에 담아 클라이언트에 전달
        addRefreshTokenToCookie(response, tokenInfo.refreshToken());

        // 4. 프론트엔드로 리다이렉트 (Access Token 없이)
        getRedirectStrategy().sendRedirect(request, response, REDIRECT_URI);
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) REFRESH_TOKEN_VALIDITY_SECONDS);

        // HTTPS 환경이 아니므로, 로컬 개발 중에는 Secure 옵션을 빼야 쿠키가 정상적으로 설정됩니다.
        // refreshTokenCookie.setSecure(true);

        response.addCookie(refreshTokenCookie);
    }
}