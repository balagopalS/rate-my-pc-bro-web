package com.ratemypcbro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebScraper {

    private static final String TAVILY_API_URL = "https://api.tavily.com/search";
    private static final String YAHOO_SEARCH_URL = "https://search.yahoo.com/search?p=";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

    @Value("${tavily.api-key:}")
    private String tavilyApiKey;

    private static final int MAX_CACHE_ENTRIES = 256;
    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private final Map<String, CachedSearch> queryCache = new ConcurrentHashMap<>();
    private record CachedSearch(String value, Instant expiresAt) {}
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Executes web search with smart multi-tier fallback:
     * 1. Tavily AI Search (TAVILY_API_KEY)
     * 2. Jsoup Yahoo Search Scraper (Zero-config fallback)
     * @param query The search query
     * @param limit Maximum number of snippets to return
     * @return Formatted string of search snippets
     */
    public String search(com.ratemypcbro.dto.SearchRequest req) {
        if (req == null || req.query() == null || req.query().isBlank()) {
            return "";
        }
        String depth = req.searchDepth() != null && !req.searchDepth().isBlank() ? req.searchDepth() : "basic";
        int limit = req.maxResults() != null && req.maxResults() > 0 ? req.maxResults() : 3;
        List<String> incDomains = req.includeDomains();
        List<String> excDomains = req.excludeDomains() != null && !req.excludeDomains().isEmpty() ? req.excludeDomains() : List.of("userbenchmark.com");
        String topic = req.topic() != null && !req.topic().isBlank() ? req.topic() : "general";

        return searchViaTavilyRich(req.query(), depth, limit, incDomains, excDomains, topic);
    }

    public String search(String query, int limit) {
        return search(query, limit, null);
    }

    public String search(String query, int limit, List<String> includeDomains) {
        String cacheKey = query + "|limit=" + limit + "|include="
                + (includeDomains != null ? String.join(",", includeDomains) : "")
                + "|exclude=userbenchmark.com|depth=basic|topic=general";
        CachedSearch cached = queryCache.get(cacheKey);
        if (cached != null && cached.expiresAt().isAfter(Instant.now())) {
            log.debug("📦 [WebScraper] Cache HIT for query: '{}'", cacheKey);
            return cached.value();
        }
        if (cached != null) queryCache.remove(cacheKey, cached);

        String resultText;
        if (tavilyApiKey != null && !tavilyApiKey.isBlank()) {
            resultText = searchViaTavilyRich(query, "basic", limit, includeDomains, List.of("userbenchmark.com"), "general");
        } else {
            resultText = searchViaYahooScraper(query, limit);
        }

        if (queryCache.size() >= MAX_CACHE_ENTRIES) {
            queryCache.entrySet().removeIf(entry -> !entry.getValue().expiresAt().isAfter(Instant.now()));
            if (queryCache.size() >= MAX_CACHE_ENTRIES) {
                String oldestKey = queryCache.entrySet().stream().min(java.util.Comparator.comparing(e -> e.getValue().expiresAt())).map(Map.Entry::getKey).orElse(null);
                if (oldestKey != null) queryCache.remove(oldestKey);
            }
        }
        queryCache.put(cacheKey, new CachedSearch(resultText == null ? "" : resultText, Instant.now().plus(CACHE_TTL)));
        return resultText;
    }

    private String searchViaTavilyRich(String query, String depth, int limit, List<String> includeDomains, List<String> excludeDomains, String topic) {
        log.debug("🏆 [Tavily Search API] Querying: '{}' (depth: {}, incDomains: {}, excDomains: {}, topic: {})", query, depth, includeDomains, excludeDomains, topic);
        try {
            java.util.Map<String, Object> requestMap = new java.util.HashMap<>();
            requestMap.put("api_key", tavilyApiKey.trim());
            requestMap.put("query", query);
            requestMap.put("search_depth", depth);
            requestMap.put("include_answer", true);
            requestMap.put("include_raw_content", false);
            requestMap.put("max_results", limit);
            requestMap.put("topic", topic);

            if (excludeDomains != null && !excludeDomains.isEmpty()) {
                requestMap.put("exclude_domains", excludeDomains);
            }
            if (includeDomains != null && !includeDomains.isEmpty()) {
                requestMap.put("include_domains", includeDomains);
            }

            String requestBody = objectMapper.writeValueAsString(requestMap);
            // Request body contains the Tavily API key. Never log it.

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TAVILY_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                
                // 1. Extract Tavily's pre-synthesized AI Answer (if available)
                String tavilyAnswer = root.path("answer").asText("").trim();
                
                // 2. Extract top snippet highlights
                JsonNode resultsNode = root.path("results");
                List<String> snippets = new ArrayList<>();
                for (JsonNode item : resultsNode) {
                    String content = item.path("content").asText("").trim();
                    if (!content.isBlank()) {
                        String title = item.path("title").asText("Source");
                        String url = item.path("url").asText("");
                        snippets.add("[" + title + "]" + (url.isBlank() ? "" : " " + url) + ": " + content);
                    }
                    if (snippets.size() >= limit) break;
                }

                StringBuilder resultBuilder = new StringBuilder();
                if (!tavilyAnswer.isBlank() && !tavilyAnswer.equalsIgnoreCase("null")) resultBuilder.append("Summary: ").append(tavilyAnswer);
                if (!snippets.isEmpty()) resultBuilder.append("\nSources:\n- ").append(String.join("\n- ", snippets));

                String resultText = resultBuilder.toString().trim();
                log.info("🏆 [Tavily Response AI Answer]: {}", tavilyAnswer.isBlank() ? "N/A" : tavilyAnswer);
                log.info("🏆 [Tavily Response Snippets Count]: {} item(s)", snippets.size());
                log.info("🏆 [Tavily Formatted Context Output]:\n{}", resultText);
                return resultText;
            } else {
                log.warn("⚠️ [Tavily Search API] Returned HTTP {}. Falling back to Yahoo scraper...", response.statusCode());
                return searchViaYahooScraper(query, limit);
            }
        } catch (Exception e) {
            log.error("❌ [Tavily Search API] Error: {}. Falling back to Yahoo scraper...", e.getMessage());
            return searchViaYahooScraper(query, limit);
        }
    }

    private String searchViaYahooScraper(String query, int limit) {
        log.debug("🌐 [WebScraper Yahoo] Scraping internet via Yahoo for query: '{}'", query);
        try {
            Thread.sleep(300);
            // Sanitize query to remove (R), (TM), (C) symbols that trigger Yahoo 500
            String sanitizedQuery = query.replaceAll("(?i)\\((R|TM|C)\\)", "").replaceAll("\\s+", " ").trim();
            String encodedQuery = URLEncoder.encode(sanitizedQuery, StandardCharsets.UTF_8);
            String searchUrl = YAHOO_SEARCH_URL + encodedQuery;

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .get();

            Elements snippets = doc.select(".compText");

            String resultText = snippets.stream()
                    .limit(limit)
                    .map(Element::text)
                    .collect(Collectors.joining("\n- "));

            if (!resultText.isBlank()) {
                log.info("✅ [WebScraper Yahoo] Scraped {} snippets for query: '{}'", limit, query);
                log.debug("🌐 [WebScraper Snippets Content]:\n{}", resultText);
            } else {
                log.warn("⚠️ [WebScraper Yahoo] No snippets found for query: '{}'", query);
            }

            return resultText;
        } catch (Exception e) {
            log.error("❌ [WebScraper Yahoo] Failed to fetch data for query: '{}'", query, e);
            return "";
        }
    }

    /**
     * Clears the in-memory web scraping cache.
     */
    public int clearCache() {
        int count = queryCache.size();
        queryCache.clear();
        log.info("🧹 [WebScraper] Cache cleared! Removed {} cached search queries.", count);
        return count;
    }
}
