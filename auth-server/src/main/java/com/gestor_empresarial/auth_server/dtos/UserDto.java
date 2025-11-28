package com.gestor_empresarial.auth_server.dtos;

import java.util.Set;


public record UserDto(
    Long id,          
    String firstName,   
    String lastName,    
    String email,      
    Set<String> roles 
) {}
