package com.fapah.stockmicroservice.service;

import com.fapah.stockmicroservice.entity.Product;
import com.fapah.stockmicroservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public Product findByName(String name) {
        try {
            return stockRepository.getProductsByName(name);
        } catch (NoSuchElementException e) {
            log.error("No such element with name {} in db", name, e);
            return null;
        }
    }

    @Override
    public String deleteByName(String name) {
        try {
            stockRepository.deleteProductByName(name);
            log.info("Product with name {} has been deleted", name);
            return "Stock deleted successfully";
        } catch (NoSuchElementException e) {
            log.error("No such element with name {} in db", name, e);
            return "No such product";
        }
    }

    @Override
    public String saveProduct(Product product) {
        try {
            product.setName(regexName(product.getName()));
            return stockRepository.saveAndFlush(product).getName();
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate product {}", product, e);
            return "Duplicate product";
        } catch (RuntimeException e) {
            log.error("Uncaught error while saving product {}", product, e);
            return "Error while creating product";
        }
    }

    @Override
    public List<Product> findAllByOrderByNameAsc() {
        List<Product> products = stockRepository.findAllByOrderByNameAsc();
        if (products.isEmpty()) {
            log.error("No products found");
            return Collections.emptyList();
        }
        log.info("Found {} products", products.size());
        return products;
    }

    @Override
    @Transactional
    public String updateProduct(Product product) {
        try {
            stockRepository.updateProductPriceByName(regexName(product.getName()), product.getPrice(), product.getQuantity());
            log.info("Product with name {} has been updated to {}", product.getName(), product);
            return "Product updated successfully";
        } catch (RuntimeException e) {
            log.error("Uncaught error while updating product {}", product, e);
            return "Error while creating product";
        }
    }

    private String regexName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
}
