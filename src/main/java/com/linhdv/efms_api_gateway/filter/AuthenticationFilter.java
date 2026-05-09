package com.linhdv.efms_api_gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linhdv.efms_api_gateway.dto.ApiResponse;
import com.linhdv.efms_api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/identity/auth/login",
            "/api/identity/auth/register",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (requiresAuthentication(path)) {
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("Missing Authorization header for path: {}", path);
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String token = "";
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }

            try {
                Claims claims = jwtUtil.getClaims(token);
                
                // Trích xuất thông tin an toàn (tránh null)
                String email = claims.getSubject();
                String userId = Objects.toString(claims.get("userId"), "");
                String companyId = Objects.toString(claims.get("companyId"), "");
                String permissions = extractPermissions(claims);

                // Thêm thông tin vào Header để forward cho các service phía sau
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Email", email != null ? email : "")
                        .header("X-User-Id", userId)
                        .header("X-User-Company-Id", companyId)
                        .header("X-User-Permission", permissions)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                log.error("JWT Validation failed for path {}: {}", path, e.getMessage());
                return onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
            }
        }

        return chain.filter(exchange);
    }

    private String extractPermissions(Claims claims) {
        try {
            Object permissionsObj = claims.get("permissions");
            if (permissionsObj instanceof List<?>) {
                List<?> list = (List<?>) permissionsObj;
                String joined = list.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","));
                return "[" + joined + "]";
            }
        } catch (Exception e) {
            log.error("Error extracting permissions from token: {}", e.getMessage());
        }
        return "[]";
    }

    private boolean requiresAuthentication(String path) {
        return OPEN_API_ENDPOINTS.stream().noneMatch(path::contains);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(httpStatus.value())
                .message(errorMessage)
                .build();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error formatting response", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}