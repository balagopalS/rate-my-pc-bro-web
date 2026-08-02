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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebScraper {

    private static final String TAVILY_API_URL = "https://api.tavily.com/search";
    private static final String BRAVE_API_URL = "https://api.search.brave.com/res/v1/web/search";
    private static final String YAHOO_SEARCH_URL = "https://search.yahoo.com/search?p=";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

    @Value("${tavily.api-key:}")
    private String tavilyApiKey;

    @Value("${brave.search.api-key:}")
    private String braveApiKey;

    private final Map<String, String> queryCache = new ConcurrentHashMap<>();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Executes web search with smart multi-tier fallback:
     * 1. Tavily AI Search (TAVILY_API_KEY)
     * 2. Brave Search API (BRAVE_SEARCH_API_KEY)
     * 3. Jsoup Yahoo Search Scraper (Zero-config fallback)
     * @param query The search query
     * @param limit Maximum number of snippets to return
     * @return Formatted string of search snippets
     */
    public String search(String query, int limit) {
        if (queryCache.containsKey(query)) {
            log.debug("📦 [WebScraper] Cache HIT for query: '{}'", query);
            return queryCache.get(query);
        }

        String resultText;
        if (tavilyApiKey != null && !tavilyApiKey.isBlank()) {
            resultText = searchViaTavilyApi(query, limit);
        } else if (braveApiKey != null && !braveApiKey.isBlank()) {
            resultText = searchViaBraveApi(query, limit);
        } else {
            resultText = searchViaYahooScraper(query, limit);
        }

        queryCache.put(query, resultText == null ? "" : resultText);
        return resultText;
    }

    private String searchViaTavilyApi(String query, int limit) {
        log.debug("🏆 [Tavily Search API] Querying: '{}'", query);
        try {
            Map<String, Object> requestMap = Map.of(
                    "api_key", tavilyApiKey.trim(),
                    "query", query,
                    "max_results", limit
            );
            String requestBody = objectMapper.writeValueAsString(requestMap);

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
                JsonNode resultsNode = root.path("results");
                List<String> snippets = new ArrayList<>();
                for (JsonNode item : resultsNode) {
                    String content = item.path("content").asText("");
                    if (!content.isBlank()) {
                        snippets.add(content);
                    }
                    if (snippets.size() >= limit) break;
                }

                String resultText = String.join("\n- ", snippets);
                log.info("🏆 [Tavily Search API] Retrieved {} snippets for query: '{}'", snippets.size(), query);
                log.debug("🏆 [Tavily Snippets Content]:\n{}", resultText);
                return resultText;
            } else {
                log.warn("⚠️ [Tavily Search API] Returned HTTP {}. Falling back to Brave/Yahoo...", response.statusCode());
                return fallbackFromTavily(query, limit);
            }
        } catch (Exception e) {
            log.error("❌ [Tavily Search API] Error: {}. Falling back to Brave/Yahoo...", e.getMessage());
            return fallbackFromTavily(query, limit);
        }
    }

    private String fallbackFromTavily(String query, int limit) {
        if (braveApiKey != null && !braveApiKey.isBlank()) {
            return searchViaBraveApi(query, limit);
        }
        return searchViaYahooScraper(query, limit);
    }

    private String searchViaBraveApi(String query, int limit) {
        log.debug("🦁 [Brave Search API] Querying: '{}'", query);
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = BRAVE_API_URL + "?q=" + encodedQuery + "&count=" + limit;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("X-Subscription-Token", braveApiKey.trim())
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode resultsNode = root.path("web").path("results");
                List<String> snippets = new ArrayList<>();
                for (JsonNode item : resultsNode) {
                    String description = item.path("description").asText("");
                    if (!description.isBlank()) {
                        snippets.add(description);
                    }
                    if (snippets.size() >= limit) break;
                }

                String resultText = String.join("\n- ", snippets);
                log.info("🦁 [Brave Search API] Scraped {} snippets for query: '{}'", snippets.size(), query);
                log.debug("🦁 [Brave Snippets Content]:\n{}", resultText);
                return resultText;
            } else {
                log.warn("⚠️ [Brave Search API] Returned HTTP {}. Falling back to Yahoo scraper...", response.statusCode());
                return searchViaYahooScraper(query, limit);
            }
        } catch (Exception e) {
            log.error("❌ [Brave Search API] Error: {}. Falling back to Yahoo scraper...", e.getMessage());
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
