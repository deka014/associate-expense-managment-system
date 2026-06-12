package com.adp.IdentityService.IdentityService.config;

import com.adp.IdentityService.IdentityService.entities.User;
import com.adp.IdentityService.IdentityService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> credential = repository.findByEmail(username);
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(credential.get().getRole().toString()));

        return mapUserToCustomUserDetails(credential, authorities);
    }

    private CustomUserDetails mapUserToCustomUserDetails(Optional<User> user, List<SimpleGrantedAuthority> authorities) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUsername(user.get().getEmail());
        customUserDetails.setPassword(user.get().getPassword());
        customUserDetails.setAuthorities(authorities);
        return customUserDetails;
    }
    
}
