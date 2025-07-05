package io.github.suho149.upbid.global.jwt;

import lombok.Builder;

@Builder
public record TokenInfo(String grantType, String accessToken, String refreshToken) {
}
