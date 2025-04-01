package com.microservice.notification.service.inter;

import com.microservice.notification.dto.MessageDTO;
import com.microservice.notification.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    NotificationDTO create(NotificationDTO notificationDTO);
    NotificationDTO update(NotificationDTO notificationDTO);
    NotificationDTO findById(Integer id);
    NotificationDTO seenNotification(Integer id);
    List<NotificationDTO> getNotificationsByUserId(Integer userId);
    void send(MessageDTO messageDTO);
}
