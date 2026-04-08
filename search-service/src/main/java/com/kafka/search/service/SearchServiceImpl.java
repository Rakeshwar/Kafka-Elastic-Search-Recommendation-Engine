package com.kafka.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.kafka.search.model.Product;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient client;
    private final Logger logger = Logger.getLogger(SearchServiceImpl.class.getName());

    public SearchServiceImpl() {
        RestClient restClient = RestClient
                .builder(new HttpHost("localhost", 9200))
                .build();
        this.client = new ElasticsearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }


    @Override
    public List<Product> callElasticSearchEngine(String query, Double price, String category) throws IOException {

        logger.info("Searching in elasticSearch");

        SearchResponse<Product> response = client.search(s -> {
                          return s.index("product-index")
                            .query(qb -> qb.bool(b -> {

                                //search
                                b.must(m -> m.match(mm -> mm.field("name").query(query)));

                                //filter price
                                if(price != null){
                                    b.filter(f -> f.range(r ->
                                            r.field("price").lte(JsonData.of(price)))
                                    );
                                }

                                // filter category
                                if(category !=null){
                                    b.filter(f -> f.term( t ->
                                            t.field("category").value(category)));
                                }

                                return b;
                            }));
                },
                Product.class
        );

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }
}

