package com.kafka.search.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;

import java.io.IOException;

public interface SearchService {
    SearchResponse<Object> callElasticSearchEngine(String q) throws IOException;
}
