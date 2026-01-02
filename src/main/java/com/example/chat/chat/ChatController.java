package com.example.chat.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

  /**
   * チャットを受信し、チャット内容を取り出してTopicに登録する
   * @return ChatMessage message
   */
  @MessageMapping("/chat.sendMessage") // リクエストを受け付けるURI
  @SendTo("/topic/public") // 戻り値を送信する宛先URI (=Message BlokerのTopic)
  public ChatMessage sendMessage(
    @Payload ChatMessage chatMessage // HTTPリクエスト(JSONデータ)をChatMessageに自動的に変換
  ) {
    return chatMessage;
  }

  /**
   * 新規入室通知を受信し、ユーザ名を取り出してTopicに登録する
   * @return ChatMessage chatMessage
   */
  @MessageMapping("/chat.addUser")
  @SendTo("/topic/public")
  public ChatMessage addUser(
    @Payload ChatMessage chatMessage, // HTTPリクエスト(JSONデータ)をChatMessageに自動的に変換
    StompHeaderAccessor headerAccessor
  ) {

    // セッション属性にユーザ名を登録する
    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

    return chatMessage;
  }
}
