package com.microservice.email;

import com.microservice.email.dto.MessageDTO;
import com.microservice.email.model.Mail;
import org.springframework.stereotype.Service;


@Service
public interface MailService {
    void sendMail(Mail mail);
    Mail getMail(String mailTo,String content,String subject);
    void  send(MessageDTO messageDTO);
}
