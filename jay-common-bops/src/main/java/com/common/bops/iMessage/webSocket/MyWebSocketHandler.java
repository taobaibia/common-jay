package com.common.bops.iMessage.webSocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("[连接建立] 客户端 ID: " + session.getId());
        session.sendMessage(new TextMessage("欢迎连接 WebSocket 服务端！"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String clientMessage = message.getPayload();
        System.out.println("[收到消息] 客户端 " + session.getId() + ": " + clientMessage);

        // 回复消息
        String reply = "服务端已收到: " + clientMessage;
        session.sendMessage(new TextMessage(reply));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("[连接关闭] 客户端 ID: " + session.getId());
    }
}