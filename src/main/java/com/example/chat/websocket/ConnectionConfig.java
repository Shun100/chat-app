package com.example.chat.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ConnectionConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    /**
     * - "/ws": WebSocket EndpointのURI
     * - withSockJS: fallback optionを追加する. ブラウザが古かったり、エラー等でWebSocket通信できない場合にHTTP通信で動作するよう切り替えてくれる.
     */
    registry.addEndpoint("/ws").withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // クライアントからサーバにメッセージを送信するときの宛先URI
    registry.setApplicationDestinationPrefixes("/app");

    /**
     * Message Broker内のtopicのURI
     * サーバは"/app"宛に送信されたメッセージを受信すると"/topic"にアクセスしてメッセージを登録する.
     */
    registry.enableSimpleBroker("/topic");
  }
}
