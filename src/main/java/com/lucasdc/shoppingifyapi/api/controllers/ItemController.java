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

import com.lucasdc.shoppingifyapi.api.dto.input.ItemInput;
import com.lucasdc.shoppingifyapi.api.dto.output.CategoryOutput;
import com.lucasdc.shoppingifyapi.api.dto.output.ItemOutput;
import com.lucasdc.shoppingifyapi.api.dto.output.ItemsCountOutput;
import com.lucasdc.shoppingifyapi.core.security.auth.AuthenticationService;
import com.lucasdc.shoppingifyapi.domain.models.Category;
import com.lucasdc.shoppingifyapi.domain.models.Item;
import com.lucasdc.shoppingifyapi.domain.models.User;
import com.lucasdc.shoppingifyapi.domain.repositories.ItemRepository;
import com.lucasdc.shoppingifyapi.domain.services.CategoryService;
import com.lucasdc.shoppingifyapi.domain.services.ItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/items")
public class ItemController {
    
    @Autowired
    private ItemService itemService;

    @Autowired
    public ItemRepository itemRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public List<ItemOutput> findAll() {
        return itemRepository.findAll().stream().map(this::toOutput).toList();
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemOutput> find(@PathVariable Long itemId) {
        Item item = itemService.searchOrFail(itemId);       
        return ResponseEntity.ok(toOutput(item));
    }

    @PostMapping
    public ResponseEntity<ItemOutput> save(@RequestBody @Valid ItemInput itemInput) {
        
        Item item = toDomainObject(itemInput);  
         
        ItemOutput itemOutput = toOutput(itemService.save(item)); 

        return ResponseEntity.ok(itemOutput);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemOutput> update(@PathVariable Long itemId, @RequestBody @Valid ItemInput itemInput) {        
        
        Item itemAtual = itemService.searchOrFail(itemId);

        copyToDomainObject(itemInput, itemAtual);

        itemAtual = itemService.save(itemAtual);

        return ResponseEntity.ok(toOutput(itemAtual));

    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top-items")
    public  List<ItemsCountOutput> hello() {
        
        User user = authenticationService.getUser();
        
        return itemService.getTopItems(user);
    }

    private ItemOutput toOutput(Item item) {
        ItemOutput itemOutput = new ItemOutput();
        itemOutput.setId(item.getId());
        itemOutput.setName(item.getName());
        itemOutput.setImage(item.getImage());
        itemOutput.setNote(item.getNote());
        
        CategoryOutput categoryOutput = new CategoryOutput();
        categoryOutput.setId(item.getCategory().getId());
        categoryOutput.setName(item.getCategory().getName());

        itemOutput.setCategory(categoryOutput);

        return itemOutput;
    }

    
    private Item copyToDomainObject(@Valid ItemInput itemInput, Item itemAtual) {

        itemAtual.setName(itemInput.getName());
        itemAtual.setImage(itemInput.getImage());
        itemAtual.setNote(itemInput.getNote());

        Category category = categoryService.searchOrFail(itemInput.getCategory().getId());
        itemAtual.setCategory(category);        

        return itemAtual;
    }

    private Item toDomainObject(ItemInput itemInput) {
        Item item = new Item();
        item.setName(itemInput.getName());
        item.setImage(itemInput.getImage());
        item.setNote(itemInput.getNote());

        Category category = categoryService.searchOrFail(itemInput.getCategory().getId());

        item.setCategory(category);
                
        return item;
    }




}
