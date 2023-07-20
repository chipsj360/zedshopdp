package com.ecommerce.zedshop.service;

import com.ecommerce.zedshop.model.Category;
import com.ecommerce.zedshop.model.dto.CategoryDto;
import com.ecommerce.zedshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repo;



    public List<Category> findAll() {
        return repo.findAll();
    }


    public Category save(Category category) {
        Category categorySave = new Category(category.getName());
        return repo.save(categorySave);
    }


    public Category findById(Long id) {
        return repo.findById(id).get();
    }


    public Category update(Category category) {
        Category categoryUpdate = null;
        try {
            categoryUpdate= repo.findById(category.getId()).get();
            categoryUpdate.setName(category.getName());
            categoryUpdate.set_activated(category.is_activated());
            categoryUpdate.set_deleted(category.is_deleted());
        }catch (Exception e){
            e.printStackTrace();
        }
        return repo.save(categoryUpdate);
    }


    public void deleteById(Long id) {
        Category category = repo.getById(id);
        category.set_deleted(true);
        category.set_activated(false);
        repo.save(category);
    }


    public void enabledById(Long id) {
        Category category = repo.getById(id);
        category.set_activated(true);
        category.set_deleted(false);
        repo.save(category);
    }


    public List<Category> findAllByActivated() {
        return repo.findAllByActivated();
    }


    public List<CategoryDto> getCategoryAndProduct() {
        return repo.getCategoryAndProduct();
    }
}
