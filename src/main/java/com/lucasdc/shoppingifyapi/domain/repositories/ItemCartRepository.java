package com.lucasdc.shoppingifyapi.domain.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lucasdc.shoppingifyapi.domain.models.Cart;
import com.lucasdc.shoppingifyapi.domain.models.Item;
import com.lucasdc.shoppingifyapi.domain.models.ItemCart;

public interface ItemCartRepository extends JpaRepository<ItemCart, Long> {

    @Query("SELECT ic FROM ItemCart ic WHERE ic.item = ?1 AND ic.cart = ?2")
    Optional<ItemCart> findByItemAndCart(Item item, Cart cart);

    boolean existsByItemAndCart(Item item, Cart cart);


}
