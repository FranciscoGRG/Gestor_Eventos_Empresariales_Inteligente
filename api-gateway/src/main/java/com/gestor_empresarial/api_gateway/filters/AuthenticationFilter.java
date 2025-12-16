package com.gestor_empresarial.api_gateway.filters;

import java.security.Key;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.gestor_empresarial.api_gateway.exceptions.ExpiredTokenException;
import com.gestor_empresarial.api_gateway.exceptions.InvalidTokenException;

import reactor.core.publisher.Mono;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login"
    );

    public AuthenticationFilter() {
        super(Config.class);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims validateAndExtractClaims(String token) {
        String jwt = token.replace("Bearer ", "");
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Token expirado", e);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Token inválido o mal formado", e);
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            // 1️⃣ Manejar preflight CORS
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return handlePreflight(exchange);
            }

            String requestPath = exchange.getRequest().getURI().getPath();

            // 2️⃣ Endpoints públicos (sin JWT)
            if (OPEN_API_ENDPOINTS.stream().anyMatch(requestPath::startsWith)) {
                return chain.filter(exchange);
            }

            // 3️⃣ Endpoints protegidos (JWT)
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            try {
                Claims claims = validateAndExtractClaims(authHeader);
                String userIdString = claims.getSubject();

                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(r -> r.header("X-User-ID", userIdString))
                        .build();

                return chain.filter(mutatedExchange);

            } catch (RuntimeException e) {
                return onError(exchange, "Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    // Maneja preflight OPTIONS con headers CORS correctos
    private Mono<Void> handlePreflight(ServerWebExchange exchange) {
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "http://localhost:8080");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "*");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        return exchange.getResponse().setComplete();
    }

    // Maneja errores con headers CORS para que el navegador no bloquee
    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "http://localhost:8080");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "*");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("X-Error-Reason", error);
        return exchange.getResponse().setComplete();
    }

    @SuppressWarnings("java:S2972")
    public static final class Config {
    }
}
