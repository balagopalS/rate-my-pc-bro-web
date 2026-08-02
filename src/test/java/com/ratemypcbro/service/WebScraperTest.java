package com.ratemypcbro.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScraperTest {

    private WebScraper webScraper;

    @BeforeEach
    void setUp() {
        webScraper = new WebScraper();
    }

    @Test
    @DisplayName("Verify WebScraper caches repeated search queries (Cache HIT vs MISS)")
    void testQueryCaching() {
        String query = "RTX 3070 Ti Passmark benchmark score";

        // First call: Cache MISS & execute scrape
        String result1 = webScraper.search(query, 3);
        assertNotNull(result1);

        // Second call: Cache HIT (should return cached string immediately)
        String result2 = webScraper.search(query, 3);
        assertEquals(result1, result2, "Cache HIT should return identical cached search results");
    }

    @Test
    @DisplayName("Verify WebScraper clearCache() resets in-memory cache")
    void testClearCache() {
        webScraper.clearCache(); // Start fresh
        webScraper.search("RTX 3070 Ti benchmark summary", 1);
        webScraper.search("Intel i7-12650H benchmark summary", 1);

        int cleared = webScraper.clearCache();
        assertTrue(cleared >= 2, "clearCache should return number of removed entries");

        // Verify cache is empty by performing a new clearCache
        int clearedAgain = webScraper.clearCache();
        assertEquals(0, clearedAgain, "Subsequent clearCache on empty cache should return 0");
    }
}
