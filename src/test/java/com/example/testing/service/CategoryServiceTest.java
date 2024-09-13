package com.example.testing.service;

import com.github.javafaker.Faker;
import com.example.testing.model.Category;
import com.example.testing.model.CategoryRequest;
import com.example.testing.repository.CategoryRepository;
import com.example.testing.service.impl.CategoryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static com.example.testing.asserts.ProjectAssertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static Faker faker;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeAll
    static void initializeFaker() {
        faker = new Faker(Locale.ENGLISH);
    }

    @Test
    void shouldReturnCategories() {
        // Mock.
        List<Category> categories = populateCategoriesList();

        // Given.
        BDDMockito.given(categoryRepository.findAll()).willReturn(categories);

        // When.
        List<Category> retrievedCategories = categoryService.findAll();

        // Then or assertions.
        Assertions.assertThat(retrievedCategories).isNotNull().hasSameSizeAs(categories).doesNotContainNull();
    }

    @Test
    void shouldReturnCategoryByValidCategoryId() {
        // Mock.
        Category category = populateRandomCategory();

        // Given.
        BDDMockito.given(categoryRepository.findById(ArgumentMatchers.anyString())).willReturn(Optional.of(category));

        // When.
        Category retrievedCategory = categoryService.findById("123456");

        // Then or assertions.
        Assertions.assertThat(retrievedCategory).isNotNull();
        Assertions.assertThat(retrievedCategory.getId()).isEqualTo(category.getId());
        Assertions.assertThat(retrievedCategory.getName()).isEqualTo(category.getName());
        Assertions.assertThat(retrievedCategory.getDescription()).isEqualTo(category.getDescription());
    }

    @Test
    void shouldSaveCategoryWithValidData() {
        // Mock.
        CategoryRequest categoryRequest = populateRandomCategoryRequest();

        Category category = populateSavedCategory(categoryRequest);
        // Given.
        BDDMockito.given(categoryRepository.save(ArgumentMatchers.any(Category.class))).willReturn(category);

        // When.
        Category savedCategory = categoryService.save(categoryRequest);

        // Then or assertions.
        assertThat(savedCategory)
                .hasId()
                .hasName(categoryRequest.getName())
                .hasDescription(categoryRequest.getDescription());
    }

    private List<Category> populateCategoriesList() {
        return Arrays.asList(
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build(),
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build(),
                Category.builder().id(UUID.randomUUID().toString()).name(faker.commerce().department()).description(faker.funnyName().name()).build()
        );
    }

    private Category populateRandomCategory() {
        return Category.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.commerce().department())
                .description(faker.funnyName().name())
                .build();
    }

    private CategoryRequest populateRandomCategoryRequest() {
        return new CategoryRequest(faker.commerce().department(), faker.funnyName().name());
    }

    private Category populateSavedCategory(CategoryRequest categoryRequest) {
        return Category.builder()
                .id(UUID.randomUUID().toString())
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
    }
}