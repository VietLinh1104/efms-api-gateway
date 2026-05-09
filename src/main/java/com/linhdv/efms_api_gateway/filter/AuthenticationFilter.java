package com.linhdv.efms_api_gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linhdv.efms_api_gateway.dto.ApiResponse;
import com.linhdv.efms_api_gateway.util.JwtUtil;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    // Danh sách các API KHÔNG yêu cầu Token (Whitelist)
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

        // 1. Kiểm tra xem request này có cần xác thực Token không
        if (requiresAuthentication(path)) {

            // 2. Kiểm tra Header Authorization
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7); // Bỏ chữ "Bearer " để lấy token thật
            } else {
                return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }

            // 3. Validate Token & Extract Claims
            Claims claims;
            try {
                claims = jwtUtil.getClaims(authHeader);
            } catch (Exception e) {
                log.error("JWT Validation failed: {}", e.getMessage());
                return onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
            }

            // 4. Forward User Info via Headers
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Email", claims.getSubject())
                    .header("X-User-Id", String.valueOf(claims.get("userId")))
                    .header("X-User-Company-Id", String.valueOf(claims.get("companyId")))
                    .header("X-User-Permission", extractPermissions(claims))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }

        return chain.filter(exchange);
    }

    private String extractPermissions(Claims claims) {
        Object permissions = claims.get("permissions");
        if (permissions instanceof List) {
            List<String> permissionList = (List<String>) permissions;
            return "[" + String.join(",", permissionList) + "]";
        }
        return "[]";
    }

    // Đổi tên và sửa lại logic cho dễ hiểu: Trả về TRUE nếu API CẦN xác thực (không nằm trong whitelist)
    private boolean requiresAuthentication(String path) {
        return OPEN_API_ENDPOINTS.stream().noneMatch(path::contains);
    }

    // Hàm trả về lỗi chuẩn hóa theo ApiResponse của hệ thống EFMS
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
        return -1; // Đảm bảo Filter này chạy trước khi routing
    }
}