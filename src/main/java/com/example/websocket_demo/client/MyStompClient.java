package com.example.websocket_demo.client;


import com.example.websocket_demo.Message;
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

//Esta clase se encarga de configurar la infraestructura de red en el cliente y establecer sesión con el backend
public class MyStompClient {
    private StompSession stompSession;//Nos permite conectarnos a servidores STOMP es decir a un intermediario (message broken)
    private String username;

    public MyStompClient(MessageListener messageListener, String username) throws ExecutionException, InterruptedException {
        this.username = username;
        //Esto sirve para crear un cliente Java capaz de conectarse a un servidor web que utilice  SockJS, al final SockJS es una libreria a nivel de aplicacion que permite la comunicacion nunca falle.
        List<Transport> transports = new ArrayList<>();//Crea una lista donde definirás los mecanismos de transporte (protocolos) que el cliente podrá intentar usar para comunicarse.
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));//Se utiliza StandarWeb.. para abrir la conexión WebSocket básica
        SockJsClient sockJsClient = new SockJsClient(transports);// Instancia el cliente de SockJS

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);//Esto permite que nuestro cliente usé el protocolo STOMP
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());//Esto sirve para serializar/deserializar a JSON el objeto a traves de la red

        //Instancia el manejador pasándole el messageListener y se conecta de forma asíncrona a la URL
        StompSessionHandler stompSessionHandler = new MyStompSessionHandler(messageListener,username);
        String url = "ws://localhost:8080/ws";
        stompSession = stompClient.connectAsync(url,stompSessionHandler).get();//Iniciamos una nueva sesión
    }

    public void sendMessage(Message message) {
        try{
            stompSession.send("/app/messages",message);
            System.out.println("Message sent: " + message.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disconnectUser(String username){
        stompSession.send("/app/disconnect",username);
        System.out.println("Disconnect user: " + username);
    }
}

