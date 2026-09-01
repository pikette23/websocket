package com.example.websocket_demo.controller;


import com.example.websocket_demo.Message;
import com.example.websocket_demo.config.WebSocketConfig;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    public WebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/message")//Este metodo significa que vamos a vincular a la ruta de mensajes de la aplicacion app/message
    public void message(Message message ) {
        simpMessagingTemplate.convertAndSend("/topic/messages", message);//Cada vez que se envíe un mensaje de vuelta se escuchará por todos los suscritos
    }

}
