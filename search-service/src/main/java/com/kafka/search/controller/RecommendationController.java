package com.kafka.search.controller;

import com.kafka.search.model.Product;
import com.kafka.search.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/recommend")
public class RecommendationController {

    Logger logger = Logger.getLogger(RecommendationController.class.getName());

    @Autowired
    RecommendationService recommendationService;

    @GetMapping("/product")
    public ResponseEntity<List<Product>> getProductRecommendation(@RequestParam String productId) throws Exception {

        logger.info("Content-based recommendations for productId:" + productId);

        List<Product> response = recommendationService.getRecommendations(productId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
