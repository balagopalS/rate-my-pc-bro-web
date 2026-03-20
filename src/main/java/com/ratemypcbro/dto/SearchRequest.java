package com.ratemypcbro.dto;

/**
 * Standard request for the Web Search Tool.
 * @param query The search string to be sent to the internet.
 */
public record SearchRequest(String query) {}
