package com.example.testing.controller;

import com.example.testing.exception.InvalidRequestException;
import com.example.testing.model.Product;
import com.example.testing.model.ProductRequest;
import com.example.testing.service.design.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> findAll() {
        log.info("ProductController :: findAll :: start");
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable("id") String id) {
        log.info("ProductController :: findById :: start");
        Product product = productService.findById(id);
        if (product == null) {
            throw new InvalidRequestException("Product not found with this id: " + id);
        }
        return product;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product save(@Valid @RequestBody ProductRequest productRequest) {
        log.info("ProductController :: save :: start");
        return productService.save(productRequest);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable("id") String id, @RequestBody ProductRequest productRequest) {
        log.info("ProductController :: update :: start");
        return new Product(id, productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getStock(), productRequest.getCategoryId());
    }

}
