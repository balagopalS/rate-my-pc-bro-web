package com.ratemypcbro.dto;

/**
 * Standard response from the Web Search Tool.
 * @param content The synthesized search results or raw text from the web.
 */
public record SearchResponse(String content) {}
