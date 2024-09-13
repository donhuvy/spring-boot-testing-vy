package com.example.testing.asserts;

import com.example.testing.model.Category;
import com.example.testing.model.Product;

public class ProjectAssertions {

    public static ProductAssert assertThat(Product actual) {
        return new ProductAssert(actual);
    }

    public static CategoryAssert assertThat(Category actual) {
        return new CategoryAssert(actual);
    }
}
