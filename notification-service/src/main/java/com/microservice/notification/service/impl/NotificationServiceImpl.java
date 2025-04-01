package com.microservice.notification.service.impl;

import com.microservice.notification.dto.MessageDTO;
import com.microservice.notification.exception.Error;
import com.microservice.notification.dto.NotificationDTO;
import com.microservice.notification.exception.CustomException;
import com.microservice.notification.model.Notification;
import com.microservice.notification.openFeign.UserClient;
import com.microservice.notification.repository.NotificationRepository;
import com.microservice.notification.service.inter.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserClient userClient;

    public Integer getGenerateId(){
        UUID uuid = UUID.randomUUID();
        return (int) (uuid.getMostSignificantBits() & 0xFFFFFFFFL);
    }

    private Notification convertToModel(NotificationDTO notificationDTO){
        return modelMapper.map(notificationDTO, Notification.class);
    }

    private NotificationDTO convertToDto(Notification notification){
        return modelMapper.map(notification, NotificationDTO.class);
    }

    private Notification save(NotificationDTO notificationDTO){
        try{
            log.info("Saving Notification");
            Notification notification = Notification.builder()
                    .id(getGenerateId())
                    .message(notificationDTO.getMessage())
                    .createdAt(LocalDateTime.now())
                    .isRead(notificationDTO.isRead())
                    .url(notificationDTO.getUrl())
                    .idUser(notificationDTO.getIsUser())
                    .build();
            return notificationRepository.save(notification);
        } catch (DataIntegrityViolationException e){
            throw new CustomException(Error.NOTIFICATION_UNABLE_TO_SAVE);
        } catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public NotificationDTO create(NotificationDTO notificationDTO) {
        log.info("Creating notification");
        return convertToDto(save(notificationDTO));
    }

    @Override
    public NotificationDTO update(NotificationDTO notificationDTO) {
        try {
            log.info("Updating notification id: {}", notificationDTO.getId());
            return convertToDto(notificationRepository.save(convertToModel(notificationDTO)));
        } catch (DataIntegrityViolationException e){
            throw new CustomException(Error.NOTIFICATION_UNABLE_TO_UPDATE);
        } catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public NotificationDTO findById(Integer id) {
        log.info("Find notification by id: {}", id);
        return convertToDto(
                notificationRepository.findById(id)
                .orElseThrow(()-> new CustomException(Error.NOTIFICATION_NOT_FOUND)));
    }

    @Override
    public NotificationDTO seenNotification(Integer id) {
        try{
            log.info("Update seen notification by id: {}", id);
            Notification notification = notificationRepository.findById(id).orElseThrow();
            notification.setRead(true);
            return convertToDto(notificationRepository.save(notification));
        }catch (DataIntegrityViolationException e){
            throw new CustomException(Error.NOTIFICATION_UNABLE_TO_UPDATE);
        }catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    @Override
    public List<NotificationDTO> getNotificationsByUserId(Integer userId) {
        try{
            log.info("Get notifications by userId: {}", userId);
            Boolean checkUser = userClient.checkId(userId).getData();
            if(!checkUser){
                throw new CustomException(Error.DATABASE_ACCESS_ERROR);
            }
            return convertToDTOList(notificationRepository.getNotificationsByUserId(userId));
        } catch (DataAccessException e){
            throw new CustomException(Error.DATABASE_ACCESS_ERROR);
        }
    }

    private List<NotificationDTO> convertToDTOList(List<Notification> notifications) {
        return notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class)) // 🚨 Lỗi ở đây
                .collect(Collectors.toList());
    }

    @Override
    public  void send(MessageDTO messageDTO){
        log.info("send notification"  );
        NotificationDTO notificationDTO=NotificationDTO.builder()
                .message(messageDTO.getMessage())
                .isRead(false)
                .isUser(messageDTO.getId())
                .build();
        create(notificationDTO);
    }
}
