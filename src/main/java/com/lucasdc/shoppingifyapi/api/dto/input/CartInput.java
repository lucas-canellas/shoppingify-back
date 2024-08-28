package com.lucasdc.shoppingifyapi.api.dto.input;

import java.util.List;

import com.lucasdc.shoppingifyapi.domain.models.StatusCart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartInput {
    private String name;

    private StatusCart status;    

    private List<ItemCartInput> items;

}
