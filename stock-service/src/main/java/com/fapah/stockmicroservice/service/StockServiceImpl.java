package com.fapah.stockmicroservice.service;

import com.fapah.core.dto.ItemsDto;
import com.fapah.core.event.OrderCheckedEvent;
import com.fapah.core.event.OrderCreateEvent;
import com.fapah.stockmicroservice.entity.Product;
import com.fapah.stockmicroservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    private final ModelMapper modelMapper;

    @Override
    public Product findByName(String name) {
        try {
            return stockRepository.getProductsByName(regexName(name));
        } catch (NoSuchElementException e) {
            log.warn("No such element with name {} in db", name, e);
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
            log.warn("No such element with name {} in db", name, e);
            return "No such product";
        }
    }

    @Override
    public String saveProduct(Product product) {
        try {
            product.setName(regexName(product.getName()));
            return stockRepository.saveAndFlush(product).getName();
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate product {}", product, e);
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

    @Override
    @Transactional
    public OrderCheckedEvent checkStock(OrderCreateEvent order) {
        try {
            List<ItemsDto> inStock = new ArrayList<>();
            List<ItemsDto> outStock = new ArrayList<>();

            for (ItemsDto item : order.getOrderItemsDto()) {
                Product product = this.findByName(item.getProductName());
                if (product == null || product.getQuantity() == 0) {
                    log.error("Product with name {} does not exist, add to out of stock", item.getProductName());
                    outStock.add(item);
                } else if (product.getQuantity() >= item.getQuantity()) {
                    log.error("Add to in stock {}", item.getProductName());
                    inStock.add(item);
                    product.setQuantity(product.getQuantity() - item.getQuantity());
                    this.saveProduct(product);
                } else {
                    log.error("Add to out of stock {}", item.getProductName());
                    outStock.add(item);
                }
            }

            log.info("Return checked order with id {} and in stock items {} / out stock items {}", order.getOrderId(), inStock.size(), outStock.size());

            return new OrderCheckedEvent(order.getOrderId(), inStock, outStock);
        } catch (RuntimeException e) {
            log.error("Uncaught error while checking stock {}", order, e);
            return new OrderCheckedEvent(order.getOrderId(), Collections.emptyList(), Collections.emptyList());
        }
    }

    private String regexName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
}
