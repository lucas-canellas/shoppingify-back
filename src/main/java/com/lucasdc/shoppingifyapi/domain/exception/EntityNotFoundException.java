package com.lucasdc.shoppingifyapi.domain.exception;

public abstract class EntityNotFoundException extends NegocioException {

    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String mensagem) {
        super(mensagem);
    }
    
    
}
