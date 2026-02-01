package com.ubc.aiadvisor.service;

import com.ubc.aiadvisor.model.HistoryEntry;
import com.ubc.aiadvisor.repository.HistoryEntryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryServiceEmptyStateTest {

    @Mock
    HistoryEntryRepository historyEntryRepository;

    @InjectMocks
    HistoryService historyService;

    @Test
    @DisplayName("getUserHistory returns empty list when no entries exist")
    void getUserHistoryEmpty() {
        when(historyEntryRepository.findByUserIdOrderByCreatedAtDesc("userEmpty"))
                .thenReturn(Collections.emptyList());

        List<HistoryEntry> entries = historyService.getUserHistory("userEmpty");
        assertNotNull(entries);
        assertTrue(entries.isEmpty());
    }
}
