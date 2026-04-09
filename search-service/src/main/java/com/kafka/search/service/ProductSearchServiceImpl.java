package com.kafka.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.kafka.search.elasticsearch.ESQueryBuilderUtil;
import com.kafka.search.model.Product;
import com.kafka.search.model.ProductSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    @Autowired
    private ElasticsearchClient client;

    private final Logger logger = Logger.getLogger(ProductSearchServiceImpl.class.getName());


    @Override
    public List<Product> callElasticSearchEngine(String query, Double price, String category) throws IOException {

        logger.info("Searching product using elasticSearch");


        /* client.search(
            REQUEST (what to search),
            RESPONSE TYPE (how to map)
        )
        */

        SearchResponse<Product> response = client.search(
               s -> s.index("product-index")
                       .query(ESQueryBuilderUtil.buildProductSearchQuery(new ProductSearchCriteria(query, price, category))),

                Product.class
        );

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }
}

