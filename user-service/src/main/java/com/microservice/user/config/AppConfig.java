package com.microservice.user.config;

import com.microservice.user.dto.UserDto;
import com.microservice.user.model.Role;
import com.microservice.user.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(User.class, UserDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getRole().name(), UserDto::setRole);
        });

        modelMapper.typeMap(UserDto.class, User.class).addMappings(mapper -> {
            mapper.map(src -> Role.valueOf(src.getRole()), User::setRole);
        });

        return modelMapper;
    }
}
