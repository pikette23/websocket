package com.example.websocket_demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
Esta clase actúa como la puerta de entrada y la centralita del servidor.
Le indica a Spring Framework cómo gestionar el protocolo WebSocket y las rutas del broker de mensajes.
*/
@Configuration
@EnableWebSocketMessageBroker //Activa la gestión de canales de mensajes
//Con la interfaz WebSocketMessageBrokerConfigurer definimos nuestras propias reglas de red
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //Cualquier mensaje enviado a una ruta que empiece por /topic será retransmitido a todos los clientes que estén suscritos a ese canal.
        config.enableSimpleBroker("/topic");

        //Si un cliente envía algo a /app/..., no se retransmite a otros usuarios inmediatamente; primero pasa a ser procesado por una clase @Controller.
        config.setApplicationDestinationPrefixes("/app");
    }

    //STOMP: Simple text Oriented Protocol: Es un protocolo que nuestro websockets seguirán para facilitar la comunicación entre clientes
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws").withSockJS();//URL que usará nuestro cliente para conectarse a nuestro websocket
        //La llamada a sockjs: es una biblioteca que proporciona un comportamiento similar al de websocket para navegadores
        //que no lo admiten, para garantizar que todos se puedan conectar
    }
}
