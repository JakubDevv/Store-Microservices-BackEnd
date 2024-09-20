package com.example.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/eureka",
            "/products/**",
            "/auth/access-token",
            "/auth/user-id",
            "/auth/validate",
            "/auth/validate-token",
            "/auth/userName",
            "/auth/companyName",
            "/auth/shortUserInfo2",
            "/auth/company",
            "/auth/users",
            "/auth/user/"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}