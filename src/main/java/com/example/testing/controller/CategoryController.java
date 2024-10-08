package com.example.testing.controller;

import com.example.testing.exception.InvalidRequestException;
import com.example.testing.model.Category;
import com.example.testing.model.CategoryRequest;
import com.example.testing.service.design.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public Category findById(@PathVariable("id") String id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            throw new InvalidRequestException("Category not found with this id: " + id);
        }
        return category;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category save(@Valid @RequestBody CategoryRequest categoryRequest) {
        return categoryService.save(categoryRequest);
    }
}
