package com.lucasdc.shoppingifyapi.domain.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucasdc.shoppingifyapi.api.controllers.CartController;
import com.lucasdc.shoppingifyapi.api.dto.input.CartInput;
import com.lucasdc.shoppingifyapi.api.dto.input.ItemCartInput;
import com.lucasdc.shoppingifyapi.domain.exception.CartNotFoundException;
import com.lucasdc.shoppingifyapi.domain.exception.ItemNotFoundException;
import com.lucasdc.shoppingifyapi.domain.exception.NegocioException;
import com.lucasdc.shoppingifyapi.domain.models.Cart;
import com.lucasdc.shoppingifyapi.domain.models.Item;
import com.lucasdc.shoppingifyapi.domain.models.ItemCart;
import com.lucasdc.shoppingifyapi.domain.models.StatusCart;
import com.lucasdc.shoppingifyapi.domain.models.User;
import com.lucasdc.shoppingifyapi.domain.repositories.CartRepository;
import com.lucasdc.shoppingifyapi.domain.repositories.ItemCartRepository;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemCartService itemCartService;

    @Autowired
    private ItemCartRepository itemCartRepository;

    public Cart save(Cart cart) {        
        return cartRepository.save(cart);
    }

    public Cart sendToHistory(CartController cartController, CartInput cartInput, User user) {
        Cart cart = cartController.cartRepository.findByStatusAndUser(StatusCart.ACTIVE, user.getId()).orElseThrow(() -> new CartNotFoundException("Cart not found"));
        cart.setStatus(cartInput.getStatus());
        cart.setCreated_at(LocalDateTime.now());
    
        for(ItemCartInput itemCartInput : cartInput.getItems()) {
            Item item = cartController.itemRepository.findById(itemCartInput.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item not found"));
            ItemCart itemCart = new ItemCart();
            itemCart.setItem(item);            
            itemCart.setCart(cart);
            itemCart.setQuantity(itemCartInput.getQuantity());
            cartController.itemCartRepository.save(itemCart);
        }
             
        cart = save(cart);
    
        Cart newCart = new Cart();
        newCart.setName("Lista de compras");
        newCart.setStatus(StatusCart.ACTIVE);
        newCart.setUser(user);
        newCart = save(newCart);
        return cart;
    }

    public void delete(Long cartId) {
        Cart cart = searchOrFail(cartId);
        cartRepository.delete(cart);
    }

    public ItemCart addOne(Cart cart, Item item) {
        ItemCart itemCart = itemCartRepository.findByItemAndCart(item, cart).orElseThrow(() -> new CartNotFoundException(cart.getId()));

        itemCart.setQuantity(itemCart.getQuantity() + 1);
        itemCartService.save(itemCart);

        return itemCart;
    }

    public ItemCart updateQuantity(Cart cart, Item item, Integer quantity) {
        ItemCart itemCart = itemCartRepository.findByItemAndCart(item, cart).orElseThrow(() -> new CartNotFoundException(cart.getId()));

        if(quantity <= 0) {
            throw new NegocioException("Não é possível diminuir a quantidade do item abaixo de 1");
        }

        itemCart.setQuantity(quantity);
        itemCartService.save(itemCart);

        return itemCart;
    }

    public ItemCart removeOne(Cart cart, Item item) {
        ItemCart itemCart = itemCartRepository.findByItemAndCart(item, cart).orElseThrow(() -> new CartNotFoundException(cart.getId()));
        
        if(itemCart.getQuantity() <= 1) {
            throw new NegocioException("Não é possível diminuir a quantidade do item abaixo de 1");
        }
        
        itemCart.setQuantity(itemCart.getQuantity() - 1);
        itemCartService.save(itemCart);

        return itemCart;
    }

    public void statusCompleted(Cart cart) {
        cart.setStatus(StatusCart.COMPLETED);
        cartRepository.save(cart);
    }

    public void statusCanceled(Cart cart) {
        cart.setStatus(StatusCart.CANCELED);
        cartRepository.save(cart);
    }

    public Cart searchOrFail(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
    }



}
