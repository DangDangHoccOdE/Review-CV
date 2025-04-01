package com.microservice.notification.controller;

import com.microservice.notification.dto.ApiResponse;
import com.microservice.notification.dto.MessageDTO;
import com.microservice.notification.dto.NotificationDTO;
import com.microservice.notification.service.inter.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("notification")
@RestController
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> create(@RequestBody MessageDTO messageDTO){
       notificationService.send(messageDTO);
       return ResponseEntity.ok(new ApiResponse<>(true,"Create is success","true"));
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<NotificationDTO>> update(@RequestBody NotificationDTO notificationDTO){
        NotificationDTO notificationDTO1 = notificationService.update(notificationDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Update is success", notificationDTO1));
    }

    @GetMapping("/user/findByUser")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> findByUser(@RequestParam Integer userId){
        List<NotificationDTO> notificationDTOS = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Find is success", notificationDTOS));
    }
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<String>> ok(){

        return ResponseEntity.ok(new ApiResponse<>(true, "Find is success", "ok"));
    }
}
