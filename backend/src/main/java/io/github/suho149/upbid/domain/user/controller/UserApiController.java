package io.github.suho149.upbid.domain.user.controller;

import io.github.suho149.upbid.domain.user.entity.User;
import io.github.suho149.upbid.domain.user.repository.UserRepository;
import io.github.suho149.upbid.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserApiController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh_token") String refreshToken) {
        // 1. Refresh Token 유효성 검증
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }

        // 2. Refresh Token에서 사용자 이메일(subject) 추출
        String email = jwtTokenProvider.getSubject(refreshToken);

        // 3. Redis에 저장된 Refresh Token과 일치하는지 확인
        String redisRefreshToken = redisTemplate.opsForValue().get(email);
        if (!refreshToken.equals(redisRefreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token does not match");
        }

        // 4. 사용자 정보 DB에서 조회
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        User user = userOptional.get();

        // 5. 새로운 Access Token 생성
        String authorities = user.getRole().getKey();
        String newAccessToken = jwtTokenProvider.generateAccessToken(email, authorities);

        // 6. Access Token을 Body에 담아 응답
        return ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
    }
}
