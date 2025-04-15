package com.microservice.manager.openFeign;

import com.microservice.manager.dto.ApiResponse;
import com.microservice.manager.dto.MessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("email-service")
public interface EmailClient {
    @PostMapping("/email/create")
    ApiResponse<String> send(@RequestBody MessageDTO messageDTO) ;
}
