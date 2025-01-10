package com.fapah.stockmicroservice.service;

import com.fapah.stockmicroservice.entity.Product;
import com.fapah.stockmicroservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public Product findByName(String name) {
        try {
            return stockRepository.getProductsByName(name);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public String deleteByName(String name) {
        try {
            stockRepository.deleteProductByName(name);
            return "Stock deleted successfully";
        } catch (NoSuchElementException e) {
            return "No such product";
        }
    }

    @Override
    public String saveProduct(Product product) {
        try {
            product.setName(regexName(product.getName()));
            return stockRepository.saveAndFlush(product).getName();
        } catch (DataIntegrityViolationException e) {
            return "Duplicate product";
        } catch (RuntimeException e) {
            return "Error while creating product";
        }
    }

    @Override
    public List<Product> findAllByOrderByNameAsc() {
        List<Product> products = stockRepository.findAllByOrderByNameAsc();
        if (products.isEmpty()) {
            return Collections.emptyList();
        }
        return products;
    }

    @Override
    @Transactional
    public String updateProduct(Product product) {
        try {
            stockRepository.updateProductPriceByName(regexName(product.getName()), product.getPrice(), product.getQuantity());
            return "Product updated successfully";
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return "Error while creating product";
        }
    }

    private String regexName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
}
