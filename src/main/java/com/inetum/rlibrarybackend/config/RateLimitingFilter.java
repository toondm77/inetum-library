package com.inetum.rlibrarybackend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);
    private static final int REQUESTS_PER_MINUTE = 100;
    private static final Bandwidth LIMIT = Bandwidth.builder()
            .capacity(REQUESTS_PER_MINUTE)
            .refillGreedy(REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
            .build();

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Bucket bucket = buckets.computeIfAbsent(resolveKey(request), key -> Bucket.builder().addLimit(LIMIT).build());
        var probe = bucket.tryConsumeAndReturnRemaining(1);

        response.setHeader("X-RateLimit-Limit", String.valueOf(REQUESTS_PER_MINUTE));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));

        if (!probe.isConsumed()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", "60");
            response.getWriter().write("{\"message\":\"Too many requests: limit is 100 per minute per client IP\"}");
            log.warn("Rate limit exceeded for key={} path={}", resolveKey(request), request.getRequestURI());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveKey(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
