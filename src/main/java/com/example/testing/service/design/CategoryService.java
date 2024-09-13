package com.example.testing.service.design;

import com.example.testing.model.Category;
import com.example.testing.model.CategoryRequest;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    Category findById(String categoryId);

    Category save(CategoryRequest categoryRequest);
}
