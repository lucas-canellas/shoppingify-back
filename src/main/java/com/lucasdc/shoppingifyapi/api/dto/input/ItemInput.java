package com.lucasdc.shoppingifyapi.api.dto.input;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemInput {

    @NotBlank
    @Column(unique = true)
    private String name;

    private String note;

    private String image;    
    
    @Valid
    @NotNull
    private CategoryIdInput category;

}
