package com.gestor_empresarial.auth_server.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gestor_empresarial.auth_server.repositories.IUserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final IUserRepository repository;

    public JpaUserDetailsService(IUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                         .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }


    
}
