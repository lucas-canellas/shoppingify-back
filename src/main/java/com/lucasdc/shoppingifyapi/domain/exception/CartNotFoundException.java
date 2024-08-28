package com.lucasdc.shoppingifyapi.domain.exception;

public class CartNotFoundException extends EntityNotFoundException {

    private static final long serialVersionUID = 1L;

    public CartNotFoundException(String mensagem) {
        super(mensagem);
    }
    
    public CartNotFoundException(Long categoryId) {
        this(String.format("Não existe um cadastro de cart com código %d", categoryId));
    }
    
}
