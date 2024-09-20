package com.example.gateway.filter;

import com.auth0.jwt.JWT;
import com.example.gateway.JwtUtil;
import com.example.gateway.RouteValidator;
import com.example.gateway.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    private final WebClient.Builder webClientBuilder;

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                if (!jwtUtil.validateAccessToken(authHeader)) {
                    throw new RuntimeException("un authorized access to application");
                }

                String path = exchange.getRequest().getURI().getPath();
                if (!path.startsWith("/admin/") && !path.startsWith("/company") && !path.startsWith("/user")) {
                    return chain.filter(exchange);
                }

                return webClientBuilder.build()
                        .get()
                        .uri("http://localhost:8080/auth/validate-token?token=" + authHeader)
                        .retrieve()
                        .bodyToMono(UserDto.class)
                        .flatMap(user -> {
                            String role = getRoleForPath(path.substring(0, path.indexOf('/', 1)));

                            if (userHasRequiredRole(user, role)) {
                                ServerHttpRequest modifiedRequest = exchange.getRequest()
                                        .mutate()
                                        .header("X-auth-user-id", String.valueOf(user.id()))
                                        .build();

                                return chain.filter(exchange.mutate().request(modifiedRequest).build());
                            } else {
                                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access forbidden"));
                            }
                        });
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {

    }

    private boolean userHasRequiredRole(UserDto user, String requiredRole) {
        return user != null && user.roles().contains(requiredRole);
    }

    public String getRoleForPath(String path) {
        if (path.startsWith("/admin")) {
            return "admin";
        } else if (path.startsWith("/company")) {
            return "company";
        } else if (path.startsWith("/user")) {
            return "client";
        }
        return null;
    }
}
