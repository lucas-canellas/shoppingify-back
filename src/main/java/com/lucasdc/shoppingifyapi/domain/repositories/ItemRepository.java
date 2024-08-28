package com.lucasdc.shoppingifyapi.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lucasdc.shoppingifyapi.domain.models.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByName(String name);

    boolean existsByName(String name);

    @Query(value = "SELECT i.name, count(ic.item_id) as total FROM item i INNER JOIN item_cart ic ON i.id = ic.item_id INNER JOIN cart c ON c.id = ic.cart_id INNER JOIN tb_user u ON u.id = c.user_id WHERE u.id = :userId GROUP BY i.id order by total desc limit 3", nativeQuery = true)
    List<Object[]> findItemsByUserId(Long userId); 
    
    


    
 
}
