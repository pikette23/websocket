package com.example.websocket_demo.client;


import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyStompClient {
    private StompSession stompSession;//Nos permite conectarnos a servidores STOMP es decir a un intermediario (message broken)
    private String username;

    public MyStompClient(String username) throws ExecutionException, InterruptedException {
        this.username = username;
        //Esto sirve para crear un cliente Java capaz de conectarse a un servidor web que utilice  SockJS, al final SockJS es una libreria a nivel de aplicacion que permite la comunicacion nunca falle.
        List<Transport> transports = new ArrayList<>();//Crea una lista donde definirás los mecanismos de transporte (protocolos) que el cliente podrá intentar usar para comunicarse.
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));//Se utiliza StandarWeb.. para abrir la conexión WebSocket básica
        SockJsClient sockJsClient = new SockJsClient(transports);// Instancia el cliente de SockJS
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);//Esto permite que nuestro cliente usé el protocolo STOMP
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());//Esto sirve para serializar/deserializar a JSON el objeto a traves de la red
        StompSessionHandler stompSessionHandler = new MyStompSessionHandler(username);//pasaremos el nuevo nombre del usuario
        String url = "ws://localhost:8080/ws";

        stompSession = stompClient.connectAsync(url,stompSessionHandler).get();//Iniciamos una nueva sesión
    }
}

