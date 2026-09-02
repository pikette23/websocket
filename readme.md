# Real-Time Chat Application

Aplicación de chat multiusuario en tiempo real construida con **Spring Boot 3.x / Spring 7** en el backend y una interfaz gráfica **Java Swing** en el cliente, utilizando **WebSockets**, **STOMP** y **SockJS**.

---

## 🚀 Características

* **Comunicación bi-direccional en tiempo real:** Mensajería instantánea mediante WebSockets y el protocolo STOMP.
* **Compatibilidad garantizada (SockJS):** Mecanismo de reserva (*fallback*) para asegurar la conexión en entornos donde los WebSockets puros estén bloqueados.
* **Gestión de presencia:** Detección de usuarios conectados/desconectados y actualización en vivo del panel lateral.
* **Interfaz Swing responsive** 
* **Arquitectura desacoplada:** Separación limpia entre la capa de red y la interfaz visual usando el patrón *Observer* (`MessageListener`).

---

## 🛠️ Tecnologías Utilizadas

* **Java:** 17+
* **Backend:** Spring Boot (Spring Messaging, WebSocket, STOMP)
* **Frontend / Cliente:** Java Swing, FlatLaf / Custom UI
* **Formato de Datos:** JSON (Jackson Message Converter)
* **Gestión de Dependencias:** Maven

---

## 🏗️ Arquitectura del Sistema

El proyecto sigue una estructura Cliente-Servidor desacoplada:

```text
[ Cliente Swing ]
       │
       ▼ (Envío de mensajes a /app/...)
[ MyStompClient ] ──(STOMP / JSON)──► [ Servidor Spring Boot ]
       ▲                                       │
       │                               (WebSocketController)
       │                                       │
  (Notificación)                        (Lógica / Presencia)
       │                                       │
       │                                       ▼
[ MyStompSessionHandler ] ◄──(Broadcast)── [ SimpMessagingTemplate ]