package io.github.suho149.upbid.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt") // "jwt" 라는 prefix를 가진 설정을 바인딩
public class JwtProperties {
    private String secret;
}
