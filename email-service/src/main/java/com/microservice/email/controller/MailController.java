package com.microservice.email.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.email.MailService;
import com.microservice.email.dto.ApiResponse;
import com.microservice.email.dto.MailDTO;
import com.microservice.email.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("email")
public class MailController {
    @Autowired
    private MailService mailService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> send(@RequestBody MessageDTO messageDTO) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            String jsonMessage = mapper.writeValueAsString(messageDTO);
            kafkaTemplate.send("send-mail-topic", jsonMessage);
            return ResponseEntity.ok(new ApiResponse<>(true, "Sent to Kafka successfully","true"));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Error sending to kafka","false"));
        }
    }
}
