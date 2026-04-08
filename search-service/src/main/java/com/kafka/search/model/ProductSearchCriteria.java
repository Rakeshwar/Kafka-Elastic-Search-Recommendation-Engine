package com.kafka.search.model;

public class ProductSearchCriteria {
    public String query;
    public Double price;
    public String category;

    public ProductSearchCriteria(String query, Double price, String category) {
        this.query = query;
        this.price = price;
        this.category = category;
    }
}
