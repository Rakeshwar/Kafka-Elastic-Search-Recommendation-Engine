package com.kafka.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    public String id;
    public String name;
    public String category;
    public Double price;
    public Double rating;
    public String description;
}
