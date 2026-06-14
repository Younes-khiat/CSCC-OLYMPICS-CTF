package com.example.demo.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(10);

    private final Map<String, AttemptState> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        AttemptState state = attempts.get(key);
        if (state == null) {
            return false;
        }

        Instant now = Instant.now();
        if (state.firstFailure.isBefore(now.minus(WINDOW))) {
            attempts.remove(key);
            return false;
        }

        return state.failures >= MAX_ATTEMPTS;
    }

    public void recordFailure(String key) {
        Instant now = Instant.now();
        attempts.compute(key, (ignored, current) -> {
            if (current == null || current.firstFailure.isBefore(now.minus(WINDOW))) {
                return new AttemptState(1, now);
            }
            return new AttemptState(current.failures + 1, current.firstFailure);
        });
    }

    public void recordSuccess(String key) {
        attempts.remove(key);
    }

    public String buildKey(String username, String remoteAddress) {
        String normalizedUsername = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        String normalizedAddress = remoteAddress == null ? "unknown" : remoteAddress.trim();
        return normalizedAddress + "|" + normalizedUsername;
    }

    private record AttemptState(int failures, Instant firstFailure) {
    }
}