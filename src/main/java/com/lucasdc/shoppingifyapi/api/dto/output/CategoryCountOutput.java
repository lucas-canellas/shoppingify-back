package com.lucasdc.shoppingifyapi.api.dto.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCountOutput {
    private String name;
    private Long total;
    private Long percent;
}
