package com.example.testing.controller;

import com.github.javafaker.Faker;
import com.example.testing.asserts.ApiErrorAssert;
import com.example.testing.asserts.CategoryAssert;
import com.example.testing.model.ApiError;
import com.example.testing.model.Category;
import com.example.testing.model.CategoryRequest;
import com.example.testing.service.design.CategoryService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

class CategoryControllerTest extends GlobalSpringContext {

    private static Faker faker;
    private final String API_URL = "/api/v1/categories";

    @MockBean
    private CategoryService categoryService;

    @BeforeAll
    static void initializeFaker() {
        faker = new Faker(Locale.ENGLISH);
    }

    @Test
    void shouldReturnAllCategories() throws Exception {
        // Mock.
        List<Category> expectedCategories = populateRandomCategories();
        // Given.
        BDDMockito.given(categoryService.findAll()).willReturn(expectedCategories);
        // When or perform the mock.
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(API_URL).contentType(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(actualResponseAsString).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(expectedCategories)
        );
    }

    @Test
    void shouldReturnCategoryWhenValidId() throws Exception {
        // Mock.
        Category category = new Category(UUID.randomUUID().toString(), "random name", "random category description");
        // Given.
        BDDMockito.given(categoryService.findById(ArgumentMatchers.anyString())).willReturn(category);
        // When, perform & assert.
        mockMvc.perform(
                        MockMvcRequestBuilders.get(API_URL + "/{id}", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody().containsObjectAsJson(category, Category.class));
    }

    @Test
    void shouldThrowAnExceptionWhenInvalidId() throws Exception {
        // Given.
        BDDMockito.given(categoryService.findById(ArgumentMatchers.anyString())).willReturn(null);
        // When, perform & assert.
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(API_URL + "/{id}", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        ApiError actualApiError = objectMapper.readValue(actualResponseAsString, ApiError.class);
        ApiError expectedApiError = populateError();

        ApiErrorAssert.assertThat(actualApiError)
                .isNotNull()
                .hasStatusCode(expectedApiError.getStatusCode())
                .hasMessage(expectedApiError.getMessage())
                .hasPath(expectedApiError.getPath());
    }

    @Test
    void shouldSaveCategoryWhenValidData() throws Exception {
        // Mock.
        CategoryRequest categoryRequest = new CategoryRequest("name", "some random description");
        Category category = new Category(UUID.randomUUID().toString(), categoryRequest.getName(), categoryRequest.getDescription());
        // Given.
        BDDMockito.given(categoryService.save(ArgumentMatchers.any(CategoryRequest.class))).willReturn(category);
        // When, verify & assertions.
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryRequest))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        String actualResponseAsString = mvcResult.getResponse().getContentAsString();
        Category actualCategory = objectMapper.readValue(actualResponseAsString, Category.class);
        CategoryAssert.assertThat(actualCategory)
                .hasId()
                .hasName(category.getName())
                .hasDescription(category.getDescription());
    }

    @Test
    void shouldThrowAnExceptionWhenInvalidData() throws Exception {
        // Mock.
        CategoryRequest categoryRequest = new CategoryRequest("", "");
        // When, verify & assertions.
        mockMvc.perform(
                        MockMvcRequestBuilders.post(API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryRequest))
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody().containsError("name", "must not be blank"))
                .andExpect(ResponseBodyMatchers.responseBody().containsError("description", "must not be blank"))
                .andReturn();
    }

    private List<Category> populateRandomCategories() {
        return Arrays.asList(
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build(),
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build(),
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build()
        );
    }

    private ApiError populateError() {
        return new ApiError("INVALID_REQUEST", "Category not found with this id: 123456", "uri=" + API_URL + "/123456");
    }

}