package com.kafka.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient client;

    public SearchServiceImpl() {
        RestClient restClient = RestClient
                .builder(new HttpHost("localhost", 9200))
                .build();
        this.client = new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }


    @Override
    public SearchResponse<Object> callElasticSearchEngine(String q) throws IOException {

        SearchResponse<Object> response = client.search(s -> s
                        .index("product-index")
                        .query(qb -> qb
                                .match(m -> m
                                        .field("name")
                                        .query(q)
                                )
                        ),
                Object.class
        );


        return response;
    }
}

