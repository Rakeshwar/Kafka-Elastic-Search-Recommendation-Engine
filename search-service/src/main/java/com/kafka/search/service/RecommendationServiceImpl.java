package com.kafka.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.kafka.search.elasticsearch.ESQueryBuilderUtil;
import com.kafka.search.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService{

    @Autowired
    ElasticsearchClient elasticsearchClient;


    @Override
    public List<Product> getRecommendations(String productId) throws IOException {

        /**1. Fetch product from Elasticsearch
         2. Build similarity query (more_like_this)
         3. Add filters (category)
         4. Exclude same product
         5. Return results
         **/

        //1
        Product product = getProductById(productId);

        if(product == null) throw new RuntimeException("Product not found");

        //2, 3, 4
        Query silimarityQuery = ESQueryBuilderUtil.getRecommendationQuery(product);

        //5
        SearchResponse<Product> response = searchSimilarProducts(silimarityQuery);

        return response.hits().hits().stream().map(hit -> hit.source()).toList();
    }


    private Product getProductById(String productId) throws IOException {
        return elasticsearchClient.get(g ->
                g.index("product-index")
                        .id(productId),
                Product.class
                ).source();
    }

    private SearchResponse<Product> searchSimilarProducts(Query query) throws IOException {
        return elasticsearchClient.search(s ->
                s.index("product-index")
                        .query(query)
                        .size(10),
                Product.class
        );
    }

}
