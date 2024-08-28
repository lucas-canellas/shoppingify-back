package com.lucasdc.shoppingifyapi.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasdc.shoppingifyapi.api.dto.input.CategoryInput;
import com.lucasdc.shoppingifyapi.api.dto.output.CategoryOutput;
import com.lucasdc.shoppingifyapi.core.security.auth.AuthenticationService;
import com.lucasdc.shoppingifyapi.api.dto.output.CategoryCountOutput;
import com.lucasdc.shoppingifyapi.domain.models.Category;
import com.lucasdc.shoppingifyapi.domain.models.User;
import com.lucasdc.shoppingifyapi.domain.repositories.CategoryRepository;
import com.lucasdc.shoppingifyapi.domain.services.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    @Autowired
    public CategoryRepository categoryRepository;

    @Autowired
    private AuthenticationService authenticationService;
    
    

    @PostMapping()
    public ResponseEntity<CategoryOutput> save(@RequestBody @Valid CategoryInput categoryInput) {
        
        Category category = toDomainObject(categoryInput);   
        CategoryOutput categoryOutput = toOutput(categoryService.save(category)); 

        return ResponseEntity.ok(categoryOutput);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryOutput> find(@PathVariable Long categoryId) {
        Category category = categoryService.searchOrFail(categoryId);       
        
        return ResponseEntity.ok(toOutput(category));
    }

    @GetMapping
    public List<CategoryOutput> findAll() {
        return categoryRepository.findAll().stream().map(this::toOutput).toList();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryOutput> update(@PathVariable Long categoryId, @RequestBody @Valid CategoryInput categoryInput) {
        Category category = categoryService.searchOrFail(categoryId);
        category.setName(categoryInput.getName());
        return ResponseEntity.ok(toOutput(categoryService.save(category)));
    }

    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
    }

    @GetMapping("/top-categories")
    public List<CategoryCountOutput> findTop3() {
        User user = authenticationService.getUser();

        return categoryService.getTopCategories(user);
    }

    private CategoryOutput toOutput(Category category) {
        CategoryOutput categoryOutput = new CategoryOutput();
        categoryOutput.setName(category.getName());
        categoryOutput.setId(category.getId());
        return categoryOutput;
    }

    private Category toDomainObject(CategoryInput categoryInput) {
        Category category = new Category();
        category.setName(categoryInput.getName());
        return category;
    }


        
    


}
