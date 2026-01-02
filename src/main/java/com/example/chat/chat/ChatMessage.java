package com.example.chat.chat;

import lombok.*;

@Getter // Getter methodの自動生成
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
  private MessageType type;
  private String content;
  private String sender;
  // private String destination; 宛先情報はSTOMPのヘッダに含まれるためここでは定義不要
}
