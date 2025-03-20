package com.microservice.user.services.inter;

import com.microservice.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto findById(Integer id);
    boolean checkUser(Integer id);
    UserDto getCurrentUser();
    UserDto update(String token, UserDto userDto);
    List<UserDto> getAllUsers();
    List<UserDto> findAllUserByIds(List<Integer> ids);
    UserDto updateIsActive(String token, Integer id);
    UserDto delete(String token, Integer id);
    List<UserDto> findUserByIds(String token, List<Integer> ids);
}
