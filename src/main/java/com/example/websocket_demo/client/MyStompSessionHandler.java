package com.example.websocket_demo.client;

import com.example.websocket_demo.Message;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

//Manejador de la sesión
public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private String username;

    public MyStompSessionHandler(String username) {
        this.username = username;
    }

    //Este metodo se utilizará cuando el usuario se haya conectado correctamente al websocket
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        //Luego de conectado nos querremos suscribir a la ruta de mensajes: /topic/message
        //Aquí también el servidor websocket transmitirá todos los mensajes de los usuarios conectados
        session.subscribe("/topic/messages", new StompFrameHandler() {
            //ESte metodo GET se utiliza para informar a nuestro cliente sobre el tipo esperado del payload
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }
            //COmprueba si el payload es del tipo esperado
            @Override
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                try{
                    if (payload instanceof Message) {
                        Message message = (Message) payload;
                        System.out.println("Received message: " + message.getUser() + ": "+message.getMessage());
                    }else {
                        System.out.println("Received unexpected payload type: " + payload.getClass());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        super.afterConnected(session, connectedHeaders);
    }
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }
}
