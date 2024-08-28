package com.lucasdc.shoppingifyapi.api.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasdc.shoppingifyapi.api.dto.input.CartInput;
import com.lucasdc.shoppingifyapi.api.dto.input.Inputquantity;
import com.lucasdc.shoppingifyapi.api.dto.input.ItemCartInput;
import com.lucasdc.shoppingifyapi.api.dto.output.CartOutput;
import com.lucasdc.shoppingifyapi.api.dto.output.ItemCartOutput;
import com.lucasdc.shoppingifyapi.api.dto.output.ItemResumoOutput;
import com.lucasdc.shoppingifyapi.api.dto.output.UserOutput;
import com.lucasdc.shoppingifyapi.core.security.auth.AuthenticationService;
import com.lucasdc.shoppingifyapi.domain.models.Cart;
import com.lucasdc.shoppingifyapi.domain.models.Item;
import com.lucasdc.shoppingifyapi.domain.models.ItemCart;
import com.lucasdc.shoppingifyapi.domain.models.StatusCart;
import com.lucasdc.shoppingifyapi.domain.models.User;
import com.lucasdc.shoppingifyapi.domain.repositories.CartRepository;
import com.lucasdc.shoppingifyapi.domain.repositories.ItemCartRepository;
import com.lucasdc.shoppingifyapi.domain.repositories.ItemRepository;
import com.lucasdc.shoppingifyapi.domain.services.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/carts")
public class CartController {
    
    @Autowired
    private CartService cartService;

    @Autowired
    public CartRepository cartRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    public ItemRepository itemRepository;

    @Autowired
    public ItemCartRepository itemCartRepository;  

    @GetMapping()
    public ResponseEntity<List<CartOutput>> getAllCartsByUse() {
        User user = authenticationService.getUser();

        List<Cart> carts = cartRepository.findByUser(user.getId());

        return ResponseEntity.ok(carts.stream().map(this::toOutput).toList());
    }
    
    @GetMapping("/active")
    public ResponseEntity<CartOutput> getActiveCart() {
        User user = authenticationService.getUser();

        Cart cart = cartRepository.findByStatusAndUser(StatusCart.ACTIVE, user.getId()).orElse(null);

        if(cart == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toOutput(cart));
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<CartOutput> getCartById(@PathVariable Long id) {
        Cart cart = cartService.searchOrFail(id);

        return ResponseEntity.ok(toOutput(cart));
    }

    @PostMapping
    public ResponseEntity<Void> createCart(@Valid @RequestBody CartInput cartInput) {

        Cart cart = toDomainObject(cartInput);
        
        cart = cartService.save(cart);        
        
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-name")
    public ResponseEntity<CartOutput> updateNameCart(@Valid @RequestBody CartInput cartInput) {
        
        User user = authenticationService.getUser();

        Cart cart = cartRepository.findByStatusAndUser(StatusCart.ACTIVE, user.getId()).orElse(null);
        
        cart.setName(cartInput.getName());        
        cart = cartService.save(cart);       

        return ResponseEntity.ok(toOutput(cart));
    }

    @PutMapping("/item/{itemId}/update-quantity")
    public ResponseEntity<ItemCartOutput> updateQuantity(@PathVariable Long itemId, Inputquantity inputQuantity) {
        User user = authenticationService.getUser();

        Cart cart = cartRepository.findByStatusAndUser(StatusCart.ACTIVE, user.getId()).get();
        Item item = itemRepository.findById(itemId).get();

        ItemCart itemCart = cartService.updateQuantity(cart, item, inputQuantity.getQuantity());
        
        return ResponseEntity.ok(toItemCartOutput(itemCart));
    }
    
    @PutMapping("/save-cart")  
    public ResponseEntity<CartOutput> saveCart(@Valid @RequestBody CartInput cartInput) {
        User user = authenticationService.getUser();

        Cart cart = cartService.sendToHistory(this, cartInput, user);

        return ResponseEntity.ok(toOutput(cart));
    }

    private CartOutput toOutput(Cart cart) {
        CartOutput cartOutput = new CartOutput();
        cartOutput.setId(cart.getId());
        cartOutput.setName(cart.getName());
        cartOutput.setStatus(cart.getStatus());
        cartOutput.setCreated_at(cart.getCreated_at());
        UserOutput userOutput = new UserOutput();
        userOutput.setId(cart.getUser().getId());
        userOutput.setEmail(cart.getUser().getEmail());
        cartOutput.setUser(userOutput);

        List<ItemCartOutput> items = new ArrayList<>();

        if(cart.getItems() == null) {
            return cartOutput;
        }

        for (ItemCart itemCart : cart.getItems()) {
            ItemCartOutput itemCartOutput = toItemCartOutput(itemCart);

            items.add(itemCartOutput);
        }   

        cartOutput.setItems(items);
        
        return cartOutput;
    }

    private ItemCartOutput toItemCartOutput(ItemCart itemCart) {
        ItemCartOutput itemCartOutput = new ItemCartOutput();
        itemCartOutput.setId(itemCart.getId());
        itemCartOutput.setQuantity(itemCart.getQuantity());
        
        ItemResumoOutput itemResumoOutput = new ItemResumoOutput();
        itemResumoOutput.setId(itemCart.getItem().getId());
        itemResumoOutput.setName(itemCart.getItem().getName());
        itemResumoOutput.setCategory(itemCart.getItem().getCategory());
        
        itemCartOutput.setItem(itemResumoOutput);
        return itemCartOutput;
    }

    private Cart toDomainObject(CartInput cartInput) {

        Cart cart = new Cart();
        cart.setName(cartInput.getName());
        cart.setStatus(cartInput.getStatus());
        User user = authenticationService.getUser();
        cart.setUser(user);
        
        for(ItemCartInput itemCartInput : cartInput.getItems()) {
            Item item = itemRepository.findById(itemCartInput.getItemId()).get();
            ItemCart itemCart = new ItemCart();
            itemCart.setQuantity(itemCartInput.getQuantity());
            itemCart.setCart(cart);
            itemCart.setItem(item);
            itemCartRepository.save(itemCart);
        }

        return cart;
    }
    

    

}


