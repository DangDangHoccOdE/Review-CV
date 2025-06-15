package com.microservice.notification.services.service;

import com.microservice.notification.dto.MessageDTO;
import com.microservice.notification.dto.NotificationDTO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface NotificationService {
    NotificationDTO create(NotificationDTO notificationDTO);
    NotificationDTO update(NotificationDTO notificationDTO);
    NotificationDTO findById(Integer id);
    NotificationDTO seenNotification(Integer id);
    List <NotificationDTO> getNotificationsByIdUser(Integer userId);
    void send(MessageDTO messageDTO);
}
