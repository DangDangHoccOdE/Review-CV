package com.microservice.user.services.serviceImpl;

import com.microservice.user.dto.ApiResponse;
import com.microservice.user.dto.UserDTO;
import com.microservice.user.exception.CustomException;
import com.microservice.user.exception.Error;
import com.microservice.user.utils.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservice.user.model.User;
import com.microservice.user.repository.UserRepository;
import com.microservice.user.services.service.UserService;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> convertToDTOList(List<User> users) {
        return users.stream()
                .map(project -> modelMapper.map(project, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findById(Integer id) {

        return convertToDto(userRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_FOUND)));
    }

    @Override
    public boolean checkUser(Integer id) {
        UserDTO userDTO = findById(id);
        if (userDTO != null) {
            return true;
        }
        return false;

    }

    @Override
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(Error.UNAUTHORIZED);
        }
        log.info("getCurrentUser auth: ");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.info("getCurrentUser usm: {}", userDetails.getUsername());
        return convertToDto(userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(Error.USER_NOT_FOUND)));
    }

    public UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User convertToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    @Override
    public List<UserDTO> getALl(String token) {
        try {
            log.info("Get all");
            jwtTokenUtil.extractUsername(token);
            return convertToDTOList(userRepository.findAll());
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public UserDTO update(String token, UserDTO userDTO) {
        try {
            log.info("Updating user by id: {}", userDTO.getId());

            User currentUser = userRepository.findById(userDTO.getId())
                    .orElseThrow(() -> new CustomException(Error.USER_NOT_FOUND));

            // Mã hóa mật khẩu mới nếu có thay đổi
            if (userDTO.getPassword() != null && !userDTO.getPassword().equals(currentUser.getPassword())) {

                userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            } else {
                userDTO.setPassword(currentUser.getPassword()); // Giữ mật khẩu hiện tại nếu không thay đổi
            }
            jwtTokenUtil.extractUsername(token);
            return convertToDto(userRepository.save(convertToEntity(userDTO)));
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.USER_UNABLE_TO_UPDATE);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public UserDTO updateIsActive(String token, Integer id) {
        try {
            log.info("Updating active status by id: {}", id);
            UserDTO user = findById(id);
            user.setActive(!user.isActive());
            return convertToDto(userRepository.save(convertToEntity(user)));
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(Error.USER_UNABLE_TO_UPDATE);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public UserDTO deleteUser(String token, Integer id) {
        try {
            log.info("Deleting user by id: {}", id);
            jwtTokenUtil.extractUsername(token);
            UserDTO user = findById(id);
            userRepository.deleteById(id);
            return user;
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<UserDTO> findUsersByIds(String token, List<Integer> idHR) {
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

    @Override
    public List<UserDTO> findAllUserByIds(List<Integer> idUsers) {
        try {
            log.info("Finding users by list of ids: {}", idUsers);
            List<User> users = userRepository.findAllById(idUsers);
            return convertToDTOList(users);
        } catch (DataAccessException e) {
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }
}
