# Conceptos Claves: WebSockets, SockJS y SockJsClient en Java

Documento de referencia técnica sobre la arquitectura de comunicación en tiempo real, mecanismos de tolerancia a fallos (*fallback*) y el funcionamiento de **SockJS** tanto en el cliente como en el servidor.

---

## 1. ¿Qué es SockJS y a qué capa pertenece?

**SockJS NO es un protocolo nuevo de la capa de transporte**, ni pertenece formalmente al modelo OSI en capas de bajo nivel como TCP o IP.

**SockJS es una librería/protocolo de nivel de aplicación (un *wrapper* o "envoltorio")** diseñado para garantizar que la comunicación en tiempo real entre el navegador/cliente y el servidor nunca falle.

> **Filosofía de SockJS:**
> *"Intenta usar WebSockets (que es rápido y bidireccional), pero si algo falla en la red, utiliza alternativas sobre HTTP para simulárselo al desarrollador."*

---

## 2. ¿Nos permite utilizar otros protocolos aparte de WebSocket?

No son protocolos distintos en el sentido de añadir capas de transporte totalmente nuevas, sino **técnicas de transporte de reserva (mecanismos de *fallback*) sobre HTTP**.

Si un firewall corporativo, un antivirus o un proxy antiguo en la red bloquea la conexión WebSocket (`ws://` o `wss://`), SockJS conmuta automáticamente a técnicas basadas en HTTP como:

* **HTTP Streaming:** Mantiene una conexión HTTP abierta para que el servidor vaya enviando datos en un flujo continuo.
* **HTTP Long Polling:** El cliente hace una petición HTTP, el servidor la mantiene abierta en espera hasta que tiene datos que enviar. Una vez responde la petición, el cliente vuelve a abrir otra inmediatamente para mantener la escucha activa.

> **Ventaja para el desarrollador:**
> El código sigue siendo idéntico. Envías y recibes mensajes como si fuera un WebSocket estándar, pero por debajo SockJS decide qué "tubo" o mecanismo de transporte utilizar según el estado de la red.

---

## 3. ¿Qué es un *Handshake* (Apretón de manos)?

Un **Handshake** en redes es el acuerdo previo o negociación inicial que realizan dos equipos antes de empezar a transmitirse datos reales.

Es análogo a un saludo verbal previo a una conversación:
* **Cliente:** *"Hola, hablo español y quiero comunicarme contigo usando el protocolo X."*
* **Servidor:** *"Hola, de acuerdo, hablo español y acepto el protocolo X. Empecemos."*

En un **WebSocket estándar (puro)**, el cliente envía una petición HTTP inicial pidiendo cambiar la conexión a un WebSocket mediante la cabecera `Upgrade: websocket`. Si el servidor acepta, la conexión cambia de protocolo y se mantiene abierta.

---

## 4. El *Handshake* propio de SockJS

Un servidor configurado con SockJS (por ejemplo, en **Spring Boot** mediante `.withSockJS()`) no espera únicamente la petición estándar de WebSocket, sino que impone una pequeña **estructura de URLs y llamadas previas (su propio "saludo inicial")**:

1. **Info Request:** El cliente realiza primero una petición `GET /tu-endpoint/info`. El servidor le responde con metadatos como:
   * Si soporta WebSockets.
   * Si requiere o no cookies.
   * Tiempo de *timeout*, entre otros.
2. **Elección de transporte:** Con esa información, SockJS genera una URL con identificadores de sesión y servidor (por ejemplo, `/tu-endpoint/354/a8d9f1/websocket`) y abre la conexión real.

> **Nota importante:**
> Si usas un cliente WebSocket "puro" e intentas conectar a un servidor que tiene habilitado `.withSockJS()`, la conexión fallará. El cliente puro intentará conectar directamente sin realizar las llamadas de `/info` iniciales que el servidor SockJS requiere para establecer la sesión.

---

## 5. ¿Qué es `SockJsClient` en Java?

`SockJsClient` es simplemente la **implementación en Java** de esa librería cliente. 

Te permite simular desde una aplicación Java (como una app de consola, un microservicio backend o un test automatizado) el mismo comportamiento flexible y tolerante a fallos que tendría un navegador web utilizando la librería oficial de JavaScript `sockjs.js`.

### Ejemplo de instanciación en Java:

```java
List<Transport> transports = new ArrayList<>();
transports.add(new WebSocketTransport(new StandardWebSocketClient()));

SockJsClient sockJsClient = new SockJsClient(transports);