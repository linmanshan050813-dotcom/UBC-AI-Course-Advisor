package com.ubc.aiadvisor.misc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PomLicenseScanTest {

    @Test
    @DisplayName("POM contains no obvious proprietary vendor groups (R-N-06 heuristic)")
    void pomNoProprietaryGroups() throws Exception {
        Path pom = Path.of("pom.xml");
        List<String> lines = Files.readAllLines(pom);
        List<String> banned = List.of("oracle", "com.microsoft.sqlserver", "com.ibm");
        List<String> hits = lines.stream()
                .map(String::toLowerCase)
                .filter(l -> banned.stream().anyMatch(l::contains))
                .collect(Collectors.toList());
        assertTrue(hits.isEmpty(), "Detected possible proprietary dependencies: " + hits);
    }
}
