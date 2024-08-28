package com.lucasdc.shoppingifyapi.api.dto.output;

import com.lucasdc.shoppingifyapi.domain.models.Category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResumoOutput {
    private Long id;
    private String name;
    private Category category;

}
