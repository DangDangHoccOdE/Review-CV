package com.microservice.profile.config;

import com.microservice.profile.dto.ProfileDTO;
import com.microservice.profile.model.Profile;
import com.microservice.profile.model.TypeProfile;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // ProfileDTO có một trường typeProfile kiểu String, trong khi Profile có trường tYpeProfile kiểu TypeProfile (một enum).
        Converter<String, TypeProfile> toTypeProfiles = new Converter<String, TypeProfile>() {
            @Override
            public TypeProfile convert(MappingContext<String, TypeProfile> mappingContext) {
                String source = mappingContext.getSource();
                if(source == null || source.trim().isEmpty()) {
                    return null;
                }
                try{
                    return TypeProfile.valueOf(source);
                }catch (IllegalArgumentException e){
                    throw new RuntimeException("Invalid type profile: " + source);
                }
            }
        };

        modelMapper.addMappings(new PropertyMap<ProfileDTO, Profile>() {
            @Override
            protected void configure(){
                using(toTypeProfiles).map(source.getTypeProfile()).setTypeProfile(null);
            }
        });
// Khi ánh xạ từ Profile sang ProfileDTO, cần chuyển TypeProfile thành String để giữ định dạng đúng cho DTO.
        Converter<TypeProfile, String> toString = new Converter<TypeProfile, String>() {
            @Override
            public String convert(MappingContext<TypeProfile, String> mappingContext) {
                TypeProfile source = mappingContext.getSource();
                return source == null ? null : source.name();
            }
        };

        modelMapper.addMappings(new PropertyMap<Profile, ProfileDTO>() {
            @Override
            protected void configure(){
                using(toString).map(source.getTypeProfile()).setTypeProfile(null);
            }
        });

        return modelMapper;
    }
}
