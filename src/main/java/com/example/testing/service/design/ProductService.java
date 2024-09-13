package com.example.testing.service.design;

import com.example.testing.model.Product;
import com.example.testing.model.ProductRequest;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    Product findById(String productId);

    Product save(ProductRequest productRequest);
}
