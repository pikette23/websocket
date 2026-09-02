package com.example.websocket_demo.controller;


import com.example.websocket_demo.Message;
import com.example.websocket_demo.WebSocketSessionManager;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
//ESta clase no maneja peticiones HTTP, si no marcos de mensajes STOMP
@Controller
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebSocketSessionManager webSocketSessionManager;


    public WebSocketController(SimpMessagingTemplate simpMessagingTemplate, WebSocketSessionManager webSocketSessionManager) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    /*
    Cuando el cliente envía un mensaje a una ruta que empieza por /app,
    Spring busca dentro de esta clase un método con la anotación @MessageMapping que coincida con el resto de la URL.
    */

    //El cliente envía un JSON con el mensaje. Spring lo deserializa automáticamente a un objeto Java Message.
    @MessageMapping("/messages")//Escucha los mensajes dirigidos a /app/messages
    public void message(Message message ) {
        System.out.println("Received message from user: " + message.getUser() + ": "+message.getMessage());
        simpMessagingTemplate.convertAndSend("/topic/messages", message);//Toma el mensaje y lo difunde a todos: /topic/messages
        System.out.println("Sent message to /topic/messages: " + message.getUser() + ": "+message.getMessage());
    }

    //Recibe la cadena con el nombre de usuario que se acaba de conectar.
    @MessageMapping("/connect")
    public void connect(String username) {
        webSocketSessionManager.addUserName(username);//Lo guarda en la lista global en memoria
        webSocketSessionManager.broadCastActivateUsernames();//Un mensaje para que todos los clientes actualicen su panel lateral de usuarios conectados
        System.out.println(username+ " connected");
    }
    //Recibe el nombre del usuario que está cerrando la aplicación
    @MessageMapping("/disconnect")
    public void disconnect(String username) {
        webSocketSessionManager.removeUserName(username);//Sacarlo de lista global
        webSocketSessionManager.broadCastActivateUsernames();//Se notifica el cambio en la lista del canal
        System.out.println(username+ " disconnected");
    }

    //Permite a un cliente pedir la lista de usuarios activos en cualquier momento (por ejemplo, justo al terminar de conectarse) forzando una emisión a /topic/users.
    @MessageMapping("/request-users")
    public void requestUsers() {
        webSocketSessionManager.broadCastActivateUsernames();
        System.out.println("Requesting users");
    }

}
