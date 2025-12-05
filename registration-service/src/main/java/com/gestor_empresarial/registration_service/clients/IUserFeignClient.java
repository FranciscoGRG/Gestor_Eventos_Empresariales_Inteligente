package com.gestor_empresarial.registration_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gestor_empresarial.registration_service.dtos.UserDto;

@FeignClient(name = "auth-server")
public interface IUserFeignClient {
    @GetMapping("api/auth/getUserEmailAndName/{id}")
    ResponseEntity<UserDto> getUserNameAndEmail(@PathVariable Long id);
}
