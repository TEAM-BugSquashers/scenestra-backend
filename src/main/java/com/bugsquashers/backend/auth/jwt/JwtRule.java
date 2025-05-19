package com.bugsquashers.backend.auth.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JwtRule {
    JWT_ISSUE_HEADER("Set-Cookie"),
    JWT_RESOLVE_HEADER("Cookie"),
    ACCESS_PREFIX("access_token"),
    REFRESH_PREFIX("refresh_token");

    private final String value;
}