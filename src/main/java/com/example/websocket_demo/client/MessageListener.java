package com.example.websocket_demo.client;

import com.example.websocket_demo.Message;

import java.util.ArrayList;
//La utilizaremos como un puente de comunicación desacoplado entre la capa de red y la interfaz gráfica
public interface MessageListener {
    /*
    Utilizamos el patrón Observer, la capa que gestiona los sockets no necesita saber nada de botones
    por lo tanto sólo necesitamos notificar eventos a cualquiera que implementa la interfaz
    */

    void onMessageRecieve(Message message); // Se invoca automaticamente cuando llega un nuevo mensaje de chat desde el servidor
    void onActiveUsersUpdated(ArrayList<String> users);//Se invoca automáticamente cuando cambia la lista de usuarios activos en el servidor
}