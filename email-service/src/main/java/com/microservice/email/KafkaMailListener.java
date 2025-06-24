package com.microservice.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.email.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaMailListener {
    @Autowired
    private MailService mailService;

    @KafkaListener(topics = "send-mail-topic", groupId = "email-service-group")
    public void listen(String messageJson){
        ObjectMapper mapper = new ObjectMapper();
        try{
            MessageDTO messageDTO = mapper.readValue(messageJson, MessageDTO.class);
            log.info("Recevied message from Kafka: {}", messageDTO);
            mailService.send(messageDTO);
        }catch (JsonProcessingException e){
            log.error("Cannot parse message: {}", messageJson,e);
        }
    }
}

