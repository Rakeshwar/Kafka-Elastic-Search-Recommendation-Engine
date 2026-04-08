package com.kafka.search.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.kafka.search.model.ProductSearchCriteria;

import java.util.ArrayList;
import java.util.List;

public class ESQueryBuilderUtil {

    public static Query buildProductQuery(ProductSearchCriteria productSearchCriteria){
        List<Query> mustQueries = List.of(buildMatchQuery(productSearchCriteria.query));
        List<Query> filterQueries = buildFilterQueries(productSearchCriteria.price, productSearchCriteria.category);

        Query finalQuery = Query.of(qb ->
                qb.bool(b ->
                        b.must(mustQueries)
                                .filter(filterQueries)
                        ));

        return finalQuery;
    }

    private static Query buildMatchQuery(String query){
        return Query.of(qb -> qb.match(m -> m.field("name").query(query)));
    }

    private static List<Query> buildFilterQueries(Double price, String category){
        List<Query> listOfFilterQueries = new ArrayList<>();

        if(price != null){
            Query filter = Query.of(qb -> qb.range(r -> r.field("price").lte(JsonData.of(price))));
            listOfFilterQueries.add(filter);
        }

        if(category != null){
            Query filter = Query.of(qb -> qb.term(r -> r.field("category").value(category)));
            listOfFilterQueries.add(filter);
        }

        return listOfFilterQueries;
    }
}
