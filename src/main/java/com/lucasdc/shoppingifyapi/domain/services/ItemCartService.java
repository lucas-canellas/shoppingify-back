package com.lucasdc.shoppingifyapi.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucasdc.shoppingifyapi.core.security.auth.AuthenticationService;
import com.lucasdc.shoppingifyapi.domain.exception.NegocioException;
import com.lucasdc.shoppingifyapi.domain.models.Cart;
import com.lucasdc.shoppingifyapi.domain.models.Item;
import com.lucasdc.shoppingifyapi.domain.models.ItemCart;
import com.lucasdc.shoppingifyapi.domain.models.StatusCart;
import com.lucasdc.shoppingifyapi.domain.models.User;
import com.lucasdc.shoppingifyapi.domain.repositories.CartRepository;
import com.lucasdc.shoppingifyapi.domain.repositories.ItemCartRepository;

@Service
public class ItemCartService {
    
    @Autowired
    private ItemCartRepository itemCartRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CartRepository cartRepository;

    public ItemCart save(ItemCart itemCart) {
        return itemCartRepository.save(itemCart);
    }

    public void delete(ItemCart itemCart) {
        itemCartRepository.delete(itemCart);
    }

    public ItemCart create(Item item) {
        User user = authenticationService.getUser();
        Cart cart = cartRepository.findByStatusAndUser(StatusCart.ACTIVE, user.getId()).orElseThrow(() -> new NegocioException("Não existe carrinho ativo para o usuário"));
                
        if (itemCartRepository.existsByItemAndCart(item, cart)) {
            ItemCart itemCart = itemCartRepository.findByItemAndCart(item, cart).orElseThrow(() -> new NegocioException("Não existe item no carrinho"));
            itemCart.setQuantity(itemCart.getQuantity() + 1);
            return itemCartRepository.save(itemCart);
        }

        ItemCart itemCart = new ItemCart();
        itemCart.setItem(item);
        itemCart.setCart(cart);
        itemCart.setQuantity(1);

        return itemCartRepository.save(itemCart);
    }



}
