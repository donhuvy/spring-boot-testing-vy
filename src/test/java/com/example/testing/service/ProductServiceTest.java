package com.example.testing.service;

import com.github.javafaker.Faker;
import com.example.testing.model.Product;
import com.example.testing.model.ProductRequest;
import com.example.testing.repository.ProductRepository;
import com.example.testing.service.impl.ProductServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static com.example.testing.asserts.ProjectAssertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static Faker faker;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeAll
    static void initializeFaker() {
        faker = new Faker(Locale.ENGLISH);
    }

    @Test
    void shouldReturnProducts() {
        // Mock.
        List<Product> products = populateProductList();

        // Given.
        BDDMockito.given(productRepository.findAll()).willReturn(products);

        // When.
        List<Product> actualProducts = productService.findAll();

        // Then or assertions.
        Assertions.assertThat(actualProducts)
                .isNotNull()
                .doesNotContainNull()
                .hasSameSizeAs(products);
    }

    @Test
    void shouldReturnProductWithValidId() {
        // Mock.
        Product product = populateRandomProduct();

        // Given.
        BDDMockito.given(productRepository.findById(ArgumentMatchers.anyString())).willReturn(Optional.of(product));

        // When.
        Product actualProduct = productService.findById(product.getId());

        // Assertions or then.
        Assertions.assertThat(actualProduct)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    @Test
    void shouldSaveProductWhenValidRequest() {
        // Mock.
        ProductRequest productRequest = populateRandomPRoductRequest();

        Product product = populateValidProduct(productRequest);

        // Given.
        BDDMockito.given(productRepository.save(ArgumentMatchers.any(Product.class))).willReturn(product);

        // When.
        Product savedProduct = productService.save(productRequest);

        // Then or assertions.
        assertThat(savedProduct)
                .hasId()
                .hasName(productRequest.getName())
                .hasDescription(productRequest.getDescription())
                .hasCategoryId(productRequest.getCategoryId())
                .hasStock(productRequest.getStock())
                .hasPrice(productRequest.getPrice());
    }

    private Product populateValidProduct(ProductRequest productRequest) {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .categoryId(productRequest.getCategoryId())
                .build();
    }

    private Product populateRandomProduct() {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.commerce().productName())
                .description(faker.funnyName().name())
                .price(new BigDecimal(faker.commerce().price()))
                .stock(faker.number().numberBetween(1, 100))
                .categoryId(UUID.randomUUID().toString())
                .build();
    }

    private List<Product> populateProductList() {
        return Arrays.asList(
                Product.builder().id(UUID.randomUUID().toString()).name(faker.commerce().productName()).description(faker.funnyName().name()).price(new BigDecimal(faker.commerce().price())).categoryId(UUID.randomUUID().toString()).build(),
                Product.builder().id(UUID.randomUUID().toString()).name(faker.commerce().productName()).description(faker.funnyName().name()).price(new BigDecimal(faker.commerce().price())).categoryId(UUID.randomUUID().toString()).build(),
                Product.builder().id(UUID.randomUUID().toString()).name(faker.commerce().productName()).description(faker.funnyName().name()).price(new BigDecimal(faker.commerce().price())).categoryId(UUID.randomUUID().toString()).build()
        );
    }

    private ProductRequest populateRandomPRoductRequest() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName(faker.commerce().productName());
        productRequest.setDescription(faker.funnyName().name());
        productRequest.setPrice(new BigDecimal(faker.commerce().price()));
        productRequest.setStock(faker.number().numberBetween(1, 100));
        productRequest.setCategoryId(UUID.randomUUID().toString());
        return productRequest;
    }

}