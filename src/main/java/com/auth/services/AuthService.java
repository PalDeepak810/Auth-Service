package com.auth.services;

import com.auth.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    public UserDto registerUser(UserDto userDto){
        UserDto userDto1=userService.createUser(userDto);

        return userDto1;
    }
}
