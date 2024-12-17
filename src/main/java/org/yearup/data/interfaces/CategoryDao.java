package org.yearup.data.interfaces;

import org.yearup.models.Category;

import java.util.List;

public interface CategoryDao
{
    List<Category> getAllCategories();
    Category getById(int categoryId);
    Category getByName(String name);
    Category createCategory(Category category);
    void update(int categoryId, Category category);
    void delete(int categoryId);
}
