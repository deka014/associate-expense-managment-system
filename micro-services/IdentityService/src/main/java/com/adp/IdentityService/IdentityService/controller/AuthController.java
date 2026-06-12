package com.adp.IdentityService.IdentityService.controller;

import com.adp.IdentityService.IdentityService.entities.User;
import com.adp.IdentityService.IdentityService.Dto.AuthRequest;
import com.adp.IdentityService.IdentityService.Dto.UserDto;
import com.adp.IdentityService.IdentityService.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public ResponseEntity<String> addNewUser(@RequestBody User user) {
        return ResponseEntity.ok(service.saveUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> getToken(@RequestBody AuthRequest authRequest) {
        UserDto userDto = service.login(authRequest);
        //if(userDto == null) return ResponseEntity.notFound().build();
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        if (authenticate.isAuthenticated()) {
            userDto.setToken(service.generateToken(authenticate));
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.notFound().build();

    }


    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return ResponseEntity.ok("Token is Valid");
    }
}