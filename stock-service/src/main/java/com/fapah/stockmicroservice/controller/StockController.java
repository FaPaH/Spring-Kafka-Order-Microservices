package com.fapah.stockmicroservice.controller;

import com.fapah.stockmicroservice.entity.Product;
import com.fapah.stockmicroservice.service.StockService;
import jakarta.persistence.PostRemove;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = stockService.findAllByOrderByNameAsc();
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok().body(stockService.findAllByOrderByNameAsc());
    }

    @PostMapping("/createProduct")
    public ResponseEntity<String> createProduct(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.saveProduct(product));
    }

    @GetMapping("/deleteByName")
    public ResponseEntity<String> deleteByName(@RequestParam String productName) {
        return ResponseEntity.ok().body(stockService.deleteByName(productName));
    }

    @GetMapping("/findByName")
    public ResponseEntity<Product> findByName(@RequestParam String productName) {
        Product product = stockService.findByName(productName);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().body(stockService.findByName(productName));
    }

    @PostMapping("/updateProduct")
    public ResponseEntity<String> updateProduct(@RequestBody Product product) {
        return ResponseEntity.ok().body(stockService.updateProduct(product));
    }
}
