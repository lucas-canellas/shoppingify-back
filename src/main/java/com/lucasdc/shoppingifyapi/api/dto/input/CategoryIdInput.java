package com.lucasdc.shoppingifyapi.api.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryIdInput {    

    @NotNull
    private Long id;
}
