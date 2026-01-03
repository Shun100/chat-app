package com.example.chat.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.chat.chat.ChatMessage;
import com.example.chat.chat.MessageType;

import lombok.RequiredArgsConstructor;

/**
 * WebSocketの切断(=チャットルームからの退出)を検知して全ユーザに通知する
 */
@Component // アプリ起動時に本クラスが自動的にインスタンス化される。他クラスから@Autowiredで注入できる
/**
 * @RequiredArgsConstructor - コンストラクタの自動生成
 * 今回の場合は、以下のコンストラクタを自動生成してくれる
 * public ConnectionEventListener(SimpMessageSendingOperations messageTemplate) {
 *   this.messageTemeplate = messageTemplate;
 * }
 */
@RequiredArgsConstructor
public class ConnectionEventListener {
  private final SimpMessageSendingOperations messageTemplate;
  
  @EventListener // WebSocketの接続を検知し、この@EventListenerが付与されたメソッドを自動的に呼び出す
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    // セッション属性に登録されているユーザ名を取得する
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String username = (String)headerAccessor.getSessionAttributes().get("username");

    if (username != null) {
      // 退出メッセージの生成
      ChatMessage message = ChatMessage.builder()
        .type(MessageType.LEAVE)
        .sender(username)
        .build();

      /**
       * チャットメッセージの送信
       * 今回は退出を全員に通知する必要があるので、送信先は全員("/topic/public")となる
       * @param String destURL - メッセージの送信先
       * @param ChatMessage message - 送信するメッセージ
       */
      messageTemplate.convertAndSend("/topic/public", message);
    }
  }
}
