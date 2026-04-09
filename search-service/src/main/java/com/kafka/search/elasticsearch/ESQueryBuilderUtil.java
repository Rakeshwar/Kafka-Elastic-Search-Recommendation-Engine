package com.kafka.search.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.kafka.search.model.ProductSearchCriteria;

import java.util.ArrayList;
import java.util.List;

public class ESQueryBuilderUtil {

    public static Query buildProductSearchQuery(ProductSearchCriteria productSearchCriteria){
        List<Query> mustQueries = List.of(buildMatchQuery(productSearchCriteria.query));

        List<Query> shouldQuery = buildShouldMatch(productSearchCriteria.query);

        List<Query> filterQueries = buildFilterQueries(productSearchCriteria.price, productSearchCriteria.category);

        Query finalQuery = Query.of(qb ->
                qb.bool(b ->
                        b//.must(mustQueries)
                                .should(shouldQuery)
                                .filter(filterQueries)

                        ));

        return finalQuery;
    }

    //use multimatch instead of match to accommodate multiple field instead of single
    // ^3 used to boost the importance of field name
    //Add Fuzziness (Typos support)
    private static Query buildMatchQuery(String query){
        return Query.of(qb -> qb.multiMatch(m -> m.query(query)
                .fields("name^3", "description")
                .fuzziness("AUTO") // typo handling
                .prefixLength(1)
                .maxExpansions(50)
        ));
    }

    private static List<Query> buildShouldMatch(String query){
        List<Query> shouldQuery = new ArrayList<>();

        // Exact match boost
        shouldQuery.add(Query.of(qb -> qb.match(m -> m
                .field("name.keyword")
                .query(query)
                .boost(10.0f)
        )));

        // Phrase match boost
        shouldQuery.add(Query.of(qb -> qb.matchPhrase(m -> m
                .field("name")
                .query(query)
                .boost(5.0f)
        )));


        // Search-as-you-type for auto-complete
        shouldQuery.add(Query.of(qb -> qb.multiMatch(m -> m
                .query(query)
                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BoolPrefix)
                .fields("name", "name._2gram", "name._3gram")
        )));

        return shouldQuery;
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


    //product recommendation
}
