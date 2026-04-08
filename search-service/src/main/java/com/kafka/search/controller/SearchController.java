package com.kafka.search.controller;

import com.kafka.search.model.Product;
import com.kafka.search.service.ProductSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private ProductSearchService productSearchService;

    private final Logger logger = Logger.getLogger(SearchController.class.getName());

    @GetMapping("/product")
    public ResponseEntity<List<Product>> productSearch(@RequestParam String query,
                                                       @RequestParam(required = false) Double price,
                                                       @RequestParam(required = false) String category) throws Exception {
        logger.info("Searching for: " + query);
        List<Product> response = productSearchService.callElasticSearchEngine(query, price, category);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}