package com.lucasdc.shoppingifyapi.api.dto.output;

import java.time.LocalDateTime;
import java.util.List;

import com.lucasdc.shoppingifyapi.domain.models.StatusCart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartOutput {
    private Long id;
    private String name;
    private StatusCart status;
    private UserOutput user;
    private LocalDateTime created_at;
    List<ItemCartOutput> items;
}
