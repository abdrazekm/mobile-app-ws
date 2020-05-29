package com.app.ws.service;

import com.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(String email);
    UserDto getUserByUserId(String id);
    UserDto updateUser(String id, UserDto user);
    void deleteUser(String id);
    List<UserDto> getUsers(int page, int limit);
}
