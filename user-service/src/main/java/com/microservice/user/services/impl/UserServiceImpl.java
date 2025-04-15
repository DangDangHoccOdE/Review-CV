package com.microservice.user.services.impl;

import com.microservice.user.dto.UserDto;
import com.microservice.user.exception.CustomException;
import com.microservice.user.exception.Error;
import com.microservice.user.model.User;
import com.microservice.user.repository.UserRepository;
import com.microservice.user.services.inter.UserService;
import com.microservice.user.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ModelMapper modelMapper;

    public List<UserDto> convertToDTOList(List<User> users) {
        return users.stream()
                .map(user-> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    public UserDto convertToDTO(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    @Override
    public UserDto findById(Integer id) {
        return convertToDTO(userRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_FOUND)));
    }

    @Override
    public boolean checkUser(Integer id) {
        UserDto userDto = findById(id);
        if(userDto != null) {
            return true;
        }
        return false;
    }

    @Override
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(Error.UNAUTHORIZED);
        }

        log.info("getCurrentUser auth: {} ", authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.info("getCurrentUser usm: {} ", userDetails.getUsername());
        return convertToDTO(userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(Error.USER_NOT_FOUND)));
    }

    @Override
    public UserDto update(String token, UserDto userDto) {
        try{
            log.info("Updating user by id: {}", userDto.getId());

            User currentUser = userRepository.findById(userDto.getId())
                    .orElseThrow(() -> new CustomException(Error.USER_NOT_FOUND));

            if(userDto.getPassword() != null && !userDto.getPassword().equals(currentUser.getPassword())) {
                userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }else{
                userDto.setPassword(currentUser.getPassword());
            }
            jwtTokenUtil.extractUsername(token);
            return convertToDTO(userRepository.save(convertToEntity(userDto)));
        }catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.USER_UNABLE_TO_UPDATE);
        }catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<UserDto> getAllUsers(String token) {
        try{
            log.info("Get all");
            jwtTokenUtil.extractUsername(token);
            return convertToDTOList(userRepository.findAll());
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public UserDto updateIsActive(String token, Integer id) {
        try{
            log.info("Updating active user by id: {}", id);
            UserDto userDto = findById(id);
            userDto.setActive(!userDto.isActive());
            return convertToDTO(userRepository.save(convertToEntity(userDto)));
        }catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.USER_UNABLE_TO_UPDATE);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<UserDto> findAllUserByIds(List<Integer> idUsers) {
        try {
            log.info("Finding users by list of ids: {}", idUsers);
            List<User> users = userRepository.findAllById(idUsers);
            return convertToDTOList(users);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public UserDto delete(String token, Integer id) {
        try{
            log.info("Deleting user by id: {}", id);
            jwtTokenUtil.extractUsername(token);
            UserDto userDto = findById(id);
            userRepository.deleteById(userDto.getId());
            return userDto;
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<UserDto> findUserByIds(String token, List<Integer> idHR) {
        try {
            log.info("Finding users by list of ids: {}", idHR);
            jwtTokenUtil.extractUsername(token);
            List<User> users = userRepository.findAllById(idHR);
            if (users.isEmpty()) {
                throw new CustomException(Error.USER_NOT_FOUND);
            }
            return convertToDTOList(users);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }
}
