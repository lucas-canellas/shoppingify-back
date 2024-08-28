package com.lucasdc.shoppingifyapi.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lucasdc.shoppingifyapi.domain.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);    
    
    @Query(value = "SELECT ca.name, count(i.category_id) as total FROM category ca INNER JOIN item i ON i.category_id = ca.id INNER JOIN item_cart ic ON i.id = ic.item_id INNER JOIN cart c ON c.id = ic.cart_id INNER JOIN tb_user u ON u.id = c.user_id WHERE u.id = :userId GROUP BY ca.id order by total desc limit 3", nativeQuery = true)
    List<Object[]> findCategoriesByUserId(Long userId);
}
