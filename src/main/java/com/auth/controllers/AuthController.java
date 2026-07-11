package com.auth.controllers;

import com.auth.dtos.LoginRequest;
import com.auth.dtos.TokenResponse;
import com.auth.dtos.UserDto;
import com.auth.entities.User;
import com.auth.exceptions.EmailNotFoundException;
import com.auth.repositories.UserRepository;
import com.auth.security.JwtService;
import com.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private final AuthenticationManager authenticationManger;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest){
        //authenticate
        Authentication authentication=authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(()->new EmailNotFoundException("email not found"));


        //checking user is enabled or not
        if(!user.isEnable()){
            throw  new DisabledException("User is disabled !!!");
        }

        //generate jwts token access token
        String accessToken= jwtService.generateAccessToken(user);
        TokenResponse tokenResponse=TokenResponse.of(accessToken,"",jwtService.getAccessTtlSeconds(),modelMapper.map(user,UserDto.class));

        return ResponseEntity.ok(tokenResponse);
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            return authenticationManger.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password()));
        }catch (Exception e){
            e.printStackTrace();
            throw new BadCredentialsException("username or password is not valid !!!");
        }
    }
}
