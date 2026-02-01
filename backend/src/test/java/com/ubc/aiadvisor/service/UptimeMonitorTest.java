package com.ubc.aiadvisor.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UptimeMonitorTest {

    @Test
    @DisplayName("Uptime percent >= 99% after simulated checks (R-N-02)")
    void uptimeMaintained() {
        UptimeMonitor monitor = new UptimeMonitor();
        for (int i = 0; i < 500; i++) {
            boolean success = i % 200 != 0; // introduce a single failure at i=0,200,400 -> 3 failures
            monitor.recordCheck(success);
        }
        double uptime = monitor.getUptimePercent();
        assertTrue(uptime >= 99.0, "Uptime below 99%: " + uptime);
    }
}
