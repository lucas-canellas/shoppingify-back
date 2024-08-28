package com.lucasdc.shoppingifyapi.domain.exception;

public class ItemNotFoundException extends EntityNotFoundException {

    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Long itemId) {
        this(String.format("Não existe um cadastro de item com código %d", itemId));
    }


    


}
