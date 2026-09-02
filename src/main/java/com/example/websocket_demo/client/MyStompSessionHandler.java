package com.example.websocket_demo.client;

import com.example.websocket_demo.Message;
import org.apache.catalina.User;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

//Se ejecuta una vez que la conexión física contra el WebSocket se ha completado con éxito.
public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private String username;
    private MessageListener messageListener;

    public MyStompSessionHandler(MessageListener messageListener,String username) {
        this.username = username;
        this.messageListener = messageListener;
    }

    //Este metodo se utilizará cuando el usuario se haya conectado correctamente al websocket
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Client connected");
        //Suscripcion al canal de mensajes
        session.subscribe("/topic/messages", new StompFrameHandler() {

            //Deserializar el JSON
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            //COmprueba si el payload es del tipo esperado
            @Override
            //Aquí recibimos lo que el servidor quiere darnos, y esta es la acción
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                try{
                    if (payload instanceof Message) {
                        Message message = (Message) payload;
                        messageListener.onMessageRecieve(message);
                        System.out.println("Received message: " + message.getUser() + ": "+message.getMessage());
                    }else {
                        System.out.println("Received unexpected payload type: " + payload.getClass());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Client Subscribe to /topic/messages");

        session.subscribe("/topic/users", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return new ArrayList<String>().getClass();
            }
            @Override
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                try{
                    if (payload instanceof ArrayList) {
                        ArrayList<String> activeUsers = (ArrayList<String>) payload;
                        messageListener.onActiveUsersUpdated(activeUsers);
                        System.out.println("Received active users: " + activeUsers);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Client Subscribe to /topic/users");

        session.send("/app/connect",username);

        session.send("/app/request-users","");
    }
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }
}
