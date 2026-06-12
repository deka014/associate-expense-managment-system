package com.adp.IdentityService.IdentityService.service;

import com.adp.IdentityService.IdentityService.entities.User;
import com.adp.IdentityService.IdentityService.Dto.AuthRequest;
import com.adp.IdentityService.IdentityService.Dto.UserDto;
import com.adp.IdentityService.IdentityService.exceptions.AppException;
import com.adp.IdentityService.IdentityService.mapper.UserMapper;
import com.adp.IdentityService.IdentityService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;


	private final UserMapper userMapper;

	public String saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return "User added";
	}

	public String generateToken(Authentication authentication) {
		return jwtService.generateToken(authentication);
	}

	public void validateToken(String token) {
		jwtService.validateToken(token);
	}

	public UserDto login(AuthRequest authRequest){
		Optional<User> user = userRepository.findByEmail(authRequest.getEmail());
		if(user.isEmpty()) return null;
		System.out.println(user.get().getEmail());
		if(passwordEncoder.matches(CharBuffer.wrap(authRequest.getPassword()), user.get().getPassword()))
			return userMapper.toUserDto(user.get());
		throw new AppException("Invalid Password",HttpStatus.BAD_REQUEST);

	}

}