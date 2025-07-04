package com.microservice.user.services.service;

import com.microservice.user.dto.UserDTO;

import java.util.List;

import org.springframework.stereotype.Service;


@Service
public interface UserService {
    UserDTO findById( Integer id);
    boolean checkUser(Integer id);
    UserDTO getCurrentUser();
    UserDTO update(String token, UserDTO userDTO);
    List<UserDTO> getALl(String token);
    List<UserDTO> findAllUserByIds(List<Integer> idUsers);
    UserDTO updateIsActive(String token,Integer id);
    UserDTO deleteUser(String token,Integer id);
    List<UserDTO> findUsersByIds(String token, List<Integer> idHR);
}

