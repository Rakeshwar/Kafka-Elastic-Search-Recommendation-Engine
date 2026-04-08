package com.kafka.search.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.search.model.Product;
import com.kafka.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    private final Logger logger = Logger.getLogger(SearchController.class.getName());

    @GetMapping
    public List<Product> search(@RequestParam String query,
                                            @RequestParam(required = false) Double price,
                                            @RequestParam(required = false) String category) throws Exception {
        logger.info("Searching for: " + query);
        List<Product> response = searchService.callElasticSearchEngine(query, price, category);
        return response;
    }
}