package com.microservice.email.service.inter;

import com.microservice.email.dto.MessageDTO;
import com.microservice.email.model.Mail;

public interface MailService {
    void sendEmail(Mail mail);
    Mail getMail(String mailTo, String content, String object);
    void send(MessageDTO messageDTO);
}
