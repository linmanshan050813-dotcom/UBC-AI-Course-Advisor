package com.ubc.aiadvisor.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * UptimeMonitor (support for R-N-02) - tracks start time and failure count.
 */
@Service
public class UptimeMonitor {
    private final Instant start = Instant.now();
    private final AtomicLong failureCount = new AtomicLong(0);
    private final AtomicLong totalChecks = new AtomicLong(0);

    /**
     * Record a system health check result.
     * 
     * @param success true if healthy, false otherwise
     */
    public void recordCheck(boolean success) {
        totalChecks.incrementAndGet();
        if (!success) failureCount.incrementAndGet();
    }

    /**
     * Calculate uptime percentage based on health checks.
     * 
     * @return uptime percentage (0.0 to 100.0)
     */
    public double getUptimePercent() {
        long checks = totalChecks.get();
        if (checks == 0) return 100.0; // assume 100% until first check
        long failures = failureCount.get();
        return ((double)(checks - failures) / checks) * 100.0;
    }

    /**
     * Get system runtime in seconds.
     * 
     * @return runtime seconds
     */
    public long getRuntimeSeconds() {
        return Duration.between(start, Instant.now()).toSeconds();
    }
}
