package com.microservice.email.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class Mail {
    private String mailFrom;
    private String mailTo;
    private String mailCc;
    private String mailBcc;

    private String mailSubject;
    private String mailContent;
    private String contentType;
    private List<Objects> attachments;

    public Date getMailSendDate(){
        return new Date();
    }
}
