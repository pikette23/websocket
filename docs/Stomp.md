## Ejemplo Práctico de Interacción Paso a Paso

Para ver cómo colaboran estas clases en un escenario real, analicemos qué ocurre exactamente cuando un usuario llamado **"Alex"** abre la aplicación y envía el mensaje **"Hola a todos"**.

---

## 1. Establecimiento de la Sesión de Red

```text
[ClientGUI] ──(1) instanciar──► [MyStompClient] ──(2) connectAsync()──► [ Servidor WebSocket ]
                                                                                │
                                                                                ▼ (Conexión OK)
                                                                    [StompSession Creada]
```
1. #### ClientGUI instancia a MyStompClient("Alex").

2. #### MyStompClient configura el transporte (SockJS), el convertidor JSON (JacksonJsonMessageConverter) y llama a:
```java
stompClient.connectAsync("ws://localhost:8080/ws", stompSessionHandler).get();
```
3. #### Spring abre el socket TCP/WebSocket con el servidor. Cuando el servidor acepta la conexión, Spring crea la instancia viva de StompSession (el tubo de red) y la inyecta inmediatamente a la siguiente fase.

---

## 2.Configuración del Handler (afterConnected)
Spring pasa el control al método afterConnected de MyStompSessionHandler, entregándole la StompSession recién abierta:
```text
[StompSession]
                                     │
                                     ▼
                        [MyStompSessionHandler]
                                     │
         ┌───────────────────────────┼───────────────────────────┐
         ▼                           ▼                           ▼
(1) session.subscribe()     (2) session.subscribe()     (3) session.send()
   Destino: /topic/messages    Destino: /topic/users       Destino: /app/connect
   Handler: StompFrameHandler  Handler: StompFrameHandler  Payload: "Alex"
```
1. #### Suscripción a Mensajes: Suscribe el cliente a /topic/messages pasando un StompFrameHandler anónimo. Indica que el payload esperado es Message.class.

2. #### Suscripción a Usuarios: Suscribe el cliente a /topic/users pasando otro StompFrameHandler anónimo. Indica que el payload esperado es ArrayList.class.

3. #### Registro de Presencia: Ejecuta session.send("/app/connect", "Alex") para avisar al servidor que "Alex" está activo.

4. #### Finalización: MyStompClient almacena la referencia de esta StompSession configurada en un atributo privado para usarla durante todo el ciclo de vida del chat.

## 3. Envío de un Mensaje desde la GUI
Cuando Alex escribe "Hola a todos" en la caja de texto y pulsa Enter:
```text
1. [ClientGUI] ──► Ejecuta myStompClient.sendMessage(new Message("Alex", "Hola a todos"))
                          │
                          ▼
2. [MyStompClient] ──► Utiliza stompSession.send("/app/messages", message)
                          │
                          ▼
3. [Jackson Converter] ──► Transforma el objeto Java a JSON:
                          {"user": "Alex", "message": "Hola a todos"}
                          │
                          ▼
4. [Red / WebSocket] ──► El marco STOMP SEND viaja por el socket hacia /app/messages
```
## 4. Procesamiento en Servidor y Distribución
```text
[ /app/messages ] ──► [WebSocketController.message()]
                             │
                             ▼
              simpMessagingTemplate.convertAndSend("/topic/messages", message)
                             │
                             ▼
                Broadcast masivo a todos los
                suscriptores de /topic/messages
```
1. #### El @MessageMapping("/messages") del WebSocketController recibe la petición.
2. #### Imprime el mensaje en los logs del servidor.
3. #### Invoca a simpMessagingTemplate.convertAndSend("/topic/messages", message), que retransmite el paquete JSON a todos los clientes que tengan una suscripción activa a esa ruta.

## 5. Recepción y Renderizado en los Clientes
```text
1. [Red / WebSocket] ──► Llega la trama JSON desde /topic/messages
                          │
                          ▼
2. [Jackson Converter] ──► Consulta getPayloadType() -> Message.class
                          Transforma el JSON en un objeto Java Message
                          │
                          ▼
3. [StompFrameHandler] ──► Invoca handleFrame(headers, payload)
                          │
                          ▼
4. [MessageListener]   ──► Invoca messageListener.onMessageRecieve(message)
                          │
                          ▼
5. [ClientGUI]         ──► Añade el nuevo JPanel con el texto y hace Scroll Down
```
