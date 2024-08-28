package com.lucasdc.shoppingifyapi.core.security.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lucasdc.shoppingifyapi.core.security.config.JwtService;
import com.lucasdc.shoppingifyapi.domain.exception.NegocioException;
import com.lucasdc.shoppingifyapi.domain.models.Cart;
import com.lucasdc.shoppingifyapi.domain.models.Role;
import com.lucasdc.shoppingifyapi.domain.models.StatusCart;
import com.lucasdc.shoppingifyapi.domain.models.User;
import com.lucasdc.shoppingifyapi.domain.repositories.CartRepository;
import com.lucasdc.shoppingifyapi.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    
    public AuthenticationResponse register(RegisterRequest request) {

        if(repository.existsByEmail(request.getEmail())) {
            throw new NegocioException("Email already in use");
        }

        var user = User.builder()
            .name(request.getName())  
            .email(request.getEmail())          
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        
            repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getPassword()
            )
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        Cart cart = cartRepository.findByStatusAndUser(StatusCart.ACTIVE, user.getId()).orElse(null);

        if(cart == null) {
            Cart cart2 = new Cart();
            cart2.setName("Lista de compras");
            cart2.setStatus(StatusCart.ACTIVE);

            cart2.setUser(user);
            cartRepository.save(cart2);    

        }
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).get();
        return user;
    }
}