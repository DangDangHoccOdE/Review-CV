package com.microservice.email.service.impl;

import com.microservice.email.dto.MailDTO;
import com.microservice.email.dto.MessageDTO;
import com.microservice.email.model.Mail;
import com.microservice.email.openFeign.UserClient;
import com.microservice.email.service.inter.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserClient userClient;

    @Override
    public void sendEmail(Mail mail) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try{
            log.info("Sending email to email: {}",mail.getMailFrom());
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setSubject(mail.getMailSubject());
            mimeMessageHelper.setFrom(new InternetAddress(mail.getMailFrom()));
            mimeMessageHelper.setTo(mail.getMailTo());
            mimeMessageHelper.setText(mail.getMailContent());
            mailSender.send(mimeMessage);
        }catch (MessagingException e){
            e.printStackTrace();
        }
    }

    @Override
    public Mail getMail(String mailTo, String content, String subject) {
        return Mail.builder()
                .mailTo(mailTo)
                .mailSubject(subject)
                .mailContent(content)
                .mailFrom("danghoangtest1@gmail.com")
                .contentType("text/plain")
                .build();
    }

    @Override
    public void send(MessageDTO messageDTO) {
        log.info("send mail");
        log.info("send mail: {}", messageDTO.getId());
        MailDTO mailDTO = MailDTO.builder()
                .mailContent(messageDTO.getMessage())
                .mailSubject(messageDTO.getMessage())
                .mailTo(userClient.findById(messageDTO.getId()).getData().getEmail())
                .build();
        sendEmail(getMail(mailDTO.getMailTo(), mailDTO.getMailSubject(), mailDTO.getMailContent()));

    }
}
