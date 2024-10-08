package com.example.testing.service.impl;

import com.example.testing.model.Product;
import com.example.testing.model.ProductRequest;
import com.example.testing.repository.ProductRepository;
import com.example.testing.service.design.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(String productId) {
        return productRepository.findById(productId).orElse(null);
    }

    @Override
    public Product save(ProductRequest productRequest) {
        Product product = Product.builder()
                .id(UUID.randomUUID().toString())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .categoryId(productRequest.getCategoryId())
                .stock(productRequest.getStock())
                .build();
        return productRepository.save(product);
    }
}
