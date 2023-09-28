package com.sagatrading.controller;

import com.sagatrading.model.Order;
import com.sagatrading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test-mq")
public class MessageTestController {
    @Autowired
    StringRedisTemplate template;

    @GetMapping
    public void addToMessageQueue() {
        template.convertAndSend("chat", "Publishing in chat");
    }
}
