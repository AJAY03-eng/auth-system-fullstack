package com.example.demo;


import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final AppuserRepository repository;

    public CustomUserDetailsService(
            AppuserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        AppUser appUser = repository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "User not found"
                    )
                );

        return User.builder()
                .username(appUser.getEmail())
                .password(appUser.getPassword())
//                .authorities(
//                    new SimpleGrantedAuthority(
//                        "ROLE_" + appUser.getRole()
//                    )
//                )
                .build();
    }
}


