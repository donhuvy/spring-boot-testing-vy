package com.example.testing.controller;

import com.github.javafaker.Faker;
import com.example.testing.asserts.ApiErrorAssert;
import com.example.testing.asserts.ProductAssert;
import com.example.testing.model.ApiError;
import com.example.testing.model.Product;
import com.example.testing.model.ProductRequest;
import com.example.testing.service.design.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

class ProductControllerTest extends GlobalSpringContext {

    private static Faker faker;
    private final String API_URL = "/api/v1/products";

    @MockBean
    private ProductService productService;

    @BeforeAll
    static void initializeFaker() {
        faker = new Faker(Locale.ENGLISH);
    }

    @Test
    void shouldReturnAllProducts() throws Exception {
        // Mock.
        List<Product> expectedProducts = populateRandomProducts();
        // Given.
        BDDMockito.given(productService.findAll()).willReturn(expectedProducts);
        // When or assertions or perform mocks.
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponseAsString).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(expectedProducts)
        );
    }

    @Test
    void shouldReturnProductWhenValidId() throws Exception {
        // Mock.
        Product product = populateRandomProduct();
        // Given.
        BDDMockito.given(productService.findById(ArgumentMatchers.anyString())).willReturn(product);
        // Assertion.
        mockMvc.perform(
                        MockMvcRequestBuilders.get(API_URL + "/{id}", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody().containsObjectAsJson(product, Product.class));
    }

    @Test
    void shouldThrowAnExceptionWhenInvalidProductId() throws Exception {
        // Given.
        BDDMockito.given(productService.findById(ArgumentMatchers.anyString())).willReturn(null);
        // Assertion.
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(API_URL + "/{id}", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        ApiError actualApiError = objectMapper.readValue(actualResponseAsString, ApiError.class);
        ApiErrorAssert.assertThat(actualApiError)
                .hasStatusCode("INVALID_REQUEST")
                .hasMessage("Product not found with this id: 123456")
                .hasPath("uri=" + API_URL + "/123456");
    }

    @Test
    void shouldSaveProductWhenValidData() throws Exception {
        // Mock.
        ProductRequest productRequest = populateProductRequest();
        Product expectedProduct = populateProductFromProductRequest(productRequest);
        // Given.
        BDDMockito.given(productService.save(ArgumentMatchers.any(ProductRequest.class))).willReturn(expectedProduct);
        // Assert.
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productRequest))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        Product actualProduct = objectMapper.readValue(actualResponseAsString, Product.class);
        ProductAssert.assertThat(actualProduct)
                .hasId()
                .hasName(expectedProduct.getName())
                .hasDescription(expectedProduct.getDescription())
                .hasPrice(expectedProduct.getPrice())
                .hasCategoryId(expectedProduct.getCategoryId())
                .hasStock(expectedProduct.getStock());
    }

    @Test
    void shouldThrowAnExceptionWhenInvalidProductRequest() throws Exception {
        // Mock.
        ProductRequest productRequest = new ProductRequest("", "", null, "", 0);
        // Assert.
        mockMvc.perform(
                        MockMvcRequestBuilders.post(API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productRequest))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody().containsError("name", "must not be blank"))
                .andExpect(ResponseBodyMatchers.responseBody().containsError("description", "must not be blank"))
                .andExpect(ResponseBodyMatchers.responseBody().containsError("price", "must not be null"))
                .andExpect(ResponseBodyMatchers.responseBody().containsError("categoryId", "must not be blank"))
                .andExpect(ResponseBodyMatchers.responseBody().containsError("stock", "must be greater than or equal to 1"));
    }


    private List<Product> populateRandomProducts() {
        return Arrays.asList(
                Product.builder().id(UUID.randomUUID().toString()).name(faker.commerce().productName()).description(faker.funnyName().name()).price(new BigDecimal(faker.commerce().price())).categoryId(UUID.randomUUID().toString()).build(),
                Product.builder().id(UUID.randomUUID().toString()).name(faker.commerce().productName()).description(faker.funnyName().name()).price(new BigDecimal(faker.commerce().price())).categoryId(UUID.randomUUID().toString()).build(),
                Product.builder().id(UUID.randomUUID().toString()).name(faker.commerce().productName()).description(faker.funnyName().name()).price(new BigDecimal(faker.commerce().price())).categoryId(UUID.randomUUID().toString()).build()
        );
    }

    private Product populateRandomProduct() {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .id(UUID.randomUUID().toString())
                .name(faker.commerce().productName())
                .description(faker.funnyName().name())
                .price(new BigDecimal(faker.commerce().price()))
                .stock(faker.number().numberBetween(1, 100))
                .categoryId(UUID.randomUUID().toString())
                .build();
    }

    private ProductRequest populateProductRequest() {
        return new ProductRequest(faker.commerce().productName(), faker.funnyName().name(), new BigDecimal(faker.commerce().price()), faker.commerce().department(), faker.number().numberBetween(1, 100));
    }

    private Product populateProductFromProductRequest(ProductRequest productRequest) {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .categoryId(productRequest.getCategoryId())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .build();
    }
}