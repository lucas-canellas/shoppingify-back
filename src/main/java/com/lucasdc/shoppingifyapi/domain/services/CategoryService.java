package com.lucasdc.shoppingifyapi.domain.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucasdc.shoppingifyapi.api.dto.output.CategoryCountOutput;
import com.lucasdc.shoppingifyapi.domain.exception.CategoryNotFoundException;
import com.lucasdc.shoppingifyapi.domain.exception.NegocioException;
import com.lucasdc.shoppingifyapi.domain.models.Category;
import com.lucasdc.shoppingifyapi.domain.models.User;
import com.lucasdc.shoppingifyapi.domain.repositories.CategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public Category save(Category category) {
        Category categorySaved = categoryRepository.findByName(category.getName()).orElse(null);
        
        if (categorySaved != null) {
            throw new NegocioException(String.format("Já existe a categoria %s", category.getName()));
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = searchOrFail(id);
        categoryRepository.delete(category);
    }
    

    public Category searchOrFail(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public List<CategoryCountOutput> getTopCategories( User user) {
        List<Object[]> categories = categoryRepository.findCategoriesByUserId(user.getId());
    
        List<CategoryCountOutput> categoriesOutput = new ArrayList<>();
    
        for (Object[] category : categories) {
            CategoryCountOutput categoryOutput = new CategoryCountOutput();
            categoryOutput.setName((String) category[0]);
            categoryOutput.setTotal((Long) category[1]);
            categoriesOutput.add(categoryOutput);
        }
        
        Long total = categoriesOutput.stream().mapToLong(CategoryCountOutput::getTotal).sum();
        
        for (CategoryCountOutput categoryCountOutput : categoriesOutput) {
            categoryCountOutput.setPercent((categoryCountOutput.getTotal() * 100) / total);
        }
    
        return categoriesOutput;
    }


    
}
