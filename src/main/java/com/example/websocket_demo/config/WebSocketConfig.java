package com.example.websocket_demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");//Enviar mensaje al usuario del servidor WebSocket
        config.setApplicationDestinationPrefixes("/app");//Enviar mensaje al servidor webSocket
    }

    //STOMP: Simple text Oriented Protocol: Es un protocolo que nuestro websockets seguirán para facilitar la comunicación entre clientes
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws").withSockJS();//URL que usará nuestro cliente para conectarse a nuestro websocket
        //La llamada a sockjs: es una biblioteca que proporciona un comportamiento similar al de websocket para navegadores
        //que no lo admiten, para garantizar que todos se puedan conectar
    }
}
