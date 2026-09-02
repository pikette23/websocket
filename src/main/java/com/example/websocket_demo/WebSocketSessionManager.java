package com.example.websocket_demo;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

//Clase que gestiona los usuarios conectados
/*
Esta clase es un servicio de Spring (@Service) encargado de mantener el estado global de las conexiones:
sabe exactamente quién está conectado en cada momento y notifica a todos los clientes cuando la lista de usuarios cambia.
*/
@Service
public class WebSocketSessionManager {
    //Lista en memoria donde se guardan los nombres de usuarios activos
    private final ArrayList<String> activeUserNames = new ArrayList<>();

    //Es la herramienta de spring que nos permite enviar mensajes a las rutas del broker(/topic/..)
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketSessionManager(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void addUserName(String userName){
        activeUserNames.add(userName);
    }
    public void removeUserName(String userName){
        activeUserNames.remove(userName);
    }

    public void broadCastActivateUsernames(){
        //La serializa a JSON y la transmite masivamente a todos los que estén escuchando /topic/users
        simpMessagingTemplate.convertAndSend("/topic/users",activeUserNames);
        System.out.println("Broadcast activate usernames to /topic/users: " + activeUserNames);
    }


}
