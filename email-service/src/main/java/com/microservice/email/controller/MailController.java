package com.microservice.email.controller;

import com.microservice.email.dto.ApiResponse;
import com.microservice.email.dto.MessageDTO;
import com.microservice.email.service.inter.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("email")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> send(@RequestBody MessageDTO messageDTO){
        mailService.send(messageDTO);
        ApiResponse<String> response = new ApiResponse<>(true, "Check user is successfully","true");
        return ResponseEntity.ok(response);
    }
}
