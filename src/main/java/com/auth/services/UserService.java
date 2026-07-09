package com.auth.services;

import com.auth.dtos.UserDto;
import com.auth.entities.Provider;
import com.auth.entities.User;
import com.auth.exceptions.EmailAlreadyExistsException;
import com.auth.exceptions.EmailNotFoundException;
import com.auth.exceptions.ResourceNotFoundException;
import com.auth.helper.UserHelper;
import com.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    public UserDto createUser(UserDto userDto){
        if(userDto.getEmail()==null||userDto.getEmail().isBlank()){
            throw new EmailNotFoundException("Email is required");
        }

        if(userRepository.existsByEmail(userDto.getEmail())){
            throw  new EmailAlreadyExistsException("Email already exists");
        }
        //Mapping dto to entity
        User user = modelMapper.map(userDto, User.class);

        //Setting provider
        user.setProvider(userDto.getProvider()!=null?userDto.getProvider(): Provider.LOCAL);

        //Assigning role
        //TODO
        User saveduser=userRepository.save(user);

        //converting saved user back to dto
        return modelMapper.map(saveduser,UserDto.class);
    }

    public Iterable<UserDto> getAllUsers(){
        return userRepository
                .findAll()
                .stream()
                .map(user->modelMapper.map(user, UserDto.class))
                .toList();
    }

    public UserDto getUserByEmail(String email){
        User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found with given id"));

        return modelMapper.map(user,UserDto.class);
    }
//
    public UserDto updateUser(UserDto userDto,String userId){
        UUID id = UserHelper.parseUUID(userId);
        User existinguser = userRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User not exists"));

        if(userDto.getName()!=null) existinguser.setName(userDto.getName());
        if(userDto.getProvider()!=null) existinguser.setProvider(userDto.getProvider());
        //todo: hashing based encoded remaining
        if(userDto.getPassword()!=null) existinguser.setPassword(userDto.getPassword());

        User updateduser=userRepository.save(existinguser);

        return modelMapper.map(updateduser,UserDto.class);
    }
//
    public void deleteUser(String userId){
        UUID userI=UserHelper.parseUUID(userId);
        User user=userRepository
                .findById(userI)
                .orElseThrow(()->new ResourceNotFoundException("User not exists"));
        userRepository.delete(user);
    }
//
    public UserDto getUserById(String userId){
        UUID userI=UserHelper.parseUUID(userId);
        User user = userRepository
                .findById(userI)
                .orElseThrow(()->new ResourceNotFoundException("User not exists"));

        return modelMapper.map(user,UserDto.class);
    }
}
