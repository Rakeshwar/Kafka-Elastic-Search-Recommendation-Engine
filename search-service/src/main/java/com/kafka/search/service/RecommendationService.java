package com.kafka.search.service;

import com.kafka.search.model.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

public interface RecommendationService {
    public List<Product> getRecommendations(String productId) throws IOException;
}
