package com.kafka.search.service;

import com.kafka.search.model.Product;

import java.io.IOException;
import java.util.List;

public interface ProductSearchService {
    List<Product> callElasticSearchEngine(String query, Double price, String category) throws IOException;
}
