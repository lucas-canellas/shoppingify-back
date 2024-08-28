package com.lucasdc.shoppingifyapi.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lucasdc.shoppingifyapi.domain.models.Cart;
import com.lucasdc.shoppingifyapi.domain.models.StatusCart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.status = ?1 AND c.user.id = ?2")
    Optional<Cart> findByStatusAndUser(StatusCart status, Long userId);

    Optional<Cart> findByName(String name);  
    
    @Query("SELECT c FROM Cart c WHERE c.user.id = ?1 AND c.status != 'ACTIVE'")
    List<Cart> findByUser(Long userId);
}
