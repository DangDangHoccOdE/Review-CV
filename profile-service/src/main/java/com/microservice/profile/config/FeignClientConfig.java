package com.microservice.profile.config;

import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.codec.Encoder;

@Configuration
public class FeignClientConfig {
    @Bean
    public Encoder feignEncoder() {
        return new SpringFormEncoder();
    }
}

// Mặc định, Feign sử dụng Jackson Encoder để serialize dữ liệu khi gửi request. Nhưng nếu bạn muốn gửi multipart/form-data (ví dụ: upload file) hoặc các form data request, bạn cần một encoder hỗ trợ Spring Form.