package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class ProxyAiProvider implements AiProvider {

    private final RestClient restClient;
    private final String proxyUrl;

    public ProxyAiProvider(RestClient.Builder restClientBuilder, @Value("${app.ai.proxy-url:http://localhost:9000/api/v1/proxy}") String proxyUrl) {
        this.restClient = restClientBuilder.build();
        this.proxyUrl = proxyUrl;
    }

    @Override
    public String getGeneralVerdict(PcSpecs specs) {
        return restClient.post()
                .uri(proxyUrl + "/verdict")
                .body(Map.of("specs", specs))
                .retrieve()
                .body(String.class);
    }

    @Override
    public String getSoftwareRunScore(PcSpecs specs, String type, String name) {
        return restClient.post()
                .uri(proxyUrl + "/run-score")
                .body(Map.of(
                    "specs", specs,
                    "type", type,
                    "name", name
                ))
                .retrieve()
                .body(String.class);
    }
}
