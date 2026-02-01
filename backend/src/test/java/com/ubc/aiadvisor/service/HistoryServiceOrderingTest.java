package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.HistoryEntry;
import com.ubc.aiadvisor.repository.HistoryEntryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryServiceOrderingTest {

    @Mock
    HistoryEntryRepository repo;

    @InjectMocks
    HistoryService historyService;

    @Test
    @DisplayName("History entries returned newest first (R-H-02)")
    void returnsNewestFirst() {
        HistoryEntry older = new HistoryEntry("Q1","A1","C1","userX");
        older.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        HistoryEntry newer = new HistoryEntry("Q2","A2","C2","userX");
        newer.setCreatedAt(LocalDateTime.now());
        when(repo.findByUserIdOrderByCreatedAtDesc(anyString())).thenReturn(Arrays.asList(newer, older));

        List<HistoryEntry> result = historyService.getUserHistory("userX");
        assertTrue(result.get(0).getCreatedAt().isAfter(result.get(1).getCreatedAt()));
    }
}
