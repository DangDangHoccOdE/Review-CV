package com.microservice.user.config;

import com.microservice.user.dto.UserDTO;
import com.microservice.user.model.Role;
import com.microservice.user.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Custom mapping for User to UserDTO
        modelMapper.typeMap(User.class, UserDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getRole().name(), UserDTO::setRole);
        });

        // Custom mapping for UserDTO to User
        modelMapper.typeMap(UserDTO.class, User.class).addMappings(mapper -> {
            mapper.map(src -> Role.valueOf(src.getRole()), User::setRole);
        });

        return modelMapper;
    }
}
