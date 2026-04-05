package com.kafka.search.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public List<Map<String, Object>> search(@RequestParam String q) throws IOException {
        System.out.println("Searching for: " + q);
        SearchResponse<Object> response = searchService.callElasticSearchEngine(q);
        ObjectMapper objectMapper = new ObjectMapper();
        return response.hits().hits().stream()
                .map(hit -> (Map<String, Object>) hit.source())
                .toList();
    }
}