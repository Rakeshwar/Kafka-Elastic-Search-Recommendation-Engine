package com.kafka.search.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.kafka.search.model.Product;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    List<Product> callElasticSearchEngine(String query, Double price, String category) throws IOException;
}
