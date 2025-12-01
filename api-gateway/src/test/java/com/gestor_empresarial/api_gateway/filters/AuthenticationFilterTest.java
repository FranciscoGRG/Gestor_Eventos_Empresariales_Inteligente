package com.gestor_empresarial.api_gateway.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    private GatewayFilterChain filterChain;
    private String secretKey;

    @BeforeEach
    void setUp() {
        filterChain = mock(GatewayFilterChain.class);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Generate a secure key and encode it in Base64
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

        ReflectionTestUtils.setField(authenticationFilter, "secretKey", secretKey);
    }

    private String generateToken(String subject) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    @Test
    void apply_ShouldPass_WhenEndpointIsOpen() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        filter.filter(exchange, filterChain).block();

        verify(filterChain).filter(exchange);
    }

    @Test
    void apply_ShouldFail_WhenNoAuthHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/events").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        filter.filter(exchange, filterChain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void apply_ShouldPass_WhenTokenIsValid() {
        String token = generateToken("123");
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        filter.filter(exchange, filterChain).block();

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain).filter(captor.capture());

        ServerWebExchange capturedExchange = captor.getValue();
        assertEquals("123", capturedExchange.getRequest().getHeaders().getFirst("X-User-ID"));
    }

    @Test
    void apply_ShouldFail_WhenTokenIsInvalid() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        filter.filter(exchange, filterChain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }
}
