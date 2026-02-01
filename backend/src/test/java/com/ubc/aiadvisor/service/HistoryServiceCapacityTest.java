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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceCapacityTest {

    @Mock
    HistoryEntryRepository historyEntryRepository;

    @InjectMocks
    HistoryService historyService;

    @Test
    @DisplayName("addEntry trims history to max 10 items")
    void addEntryTrimsHistoryToTen() {
        String userId = "userX";

        // Simulate existing 10 entries (newest first)
        List<HistoryEntry> existing = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HistoryEntry e = new HistoryEntry("Q"+i, "A"+i, null, userId);
            e.setCreatedAt(LocalDateTime.now().minusMinutes(i));
            existing.add(e);
        }

        HistoryEntry newEntry = new HistoryEntry("Q-new", "A-new", null, userId);
        when(historyEntryRepository.save(any())).thenReturn(newEntry);

        // After save, repository returns list with 11 entries (newest first)
        List<HistoryEntry> after = new ArrayList<>();
        after.add(newEntry);
        after.addAll(existing);

        when(historyEntryRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(after) // first call after save
                .thenReturn(after.subList(0, 10)); // subsequent calls (after trimming)

        HistoryEntry saved = historyService.addEntry("Q-new", "A-new", null, userId);
        assertNotNull(saved);
        assertEquals("Q-new", saved.getQuestion());

        // Verify deleteAll called with the overflow element(s)
        verify(historyEntryRepository, times(1)).deleteAll(argThat(iterable -> {
            int count = 0;
            for (Object o : iterable) { count++; }
            return count == 1;
        }));
    }
}
