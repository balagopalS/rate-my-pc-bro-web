package com.ratemypcbro.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebScraper {

    private static final String SEARCH_URL = "https://search.yahoo.com/search?p=";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";
    
    // Simple in-memory cache to prevent hammering for popular hardware
    private final Map<String, String> queryCache = new ConcurrentHashMap<>();

    /**
     * Searches using the most permissive engine available (currently Yahoo Search) 
     * to bypass aggressive 429 filters.
     * @param query The search query
     * @param limit Maximum number of snippets to return
     * @return Formatted string of search snippets
     */
    public String search(String query, int limit) {
        if (queryCache.containsKey(query)) {
            log.debug("📦 [WebScraper] Cache HIT for query: '{}'", query);
            return queryCache.get(query);
        }

        try {
            log.debug("🌐 [WebScraper] Scraping internet via Yahoo for query: '{}'", query);
            // Maintain safety delay
            Thread.sleep(500);

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String searchUrl = SEARCH_URL + encodedQuery;

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .get();

            // The css selector for standard text snippets on Yahoo Search
            Elements snippets = doc.select(".compText");

            String resultText = snippets.stream()
                    .limit(limit)
                    .map(Element::text)
                    .collect(Collectors.joining("\n- "));
            
            if (!resultText.isBlank()) {
                log.debug("✅ [WebScraper] Successfully extracted {} snippets.", limit);
            } else {
                log.warn("⚠️ [WebScraper] No snippets found for query: '{}'", query);
            }
                    
            queryCache.put(query, resultText);
            return resultText;
        } catch (Exception e) {
            log.error("❌ [WebScraper] Failed to fetch data for query: '{}'", query, e);
            return "Failed to fetch data for query: " + query + " (Error: " + e.getMessage() + ")";
        }
    }
}
