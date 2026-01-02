'use strict';

let stompClient = null;
let username = null;

const usernamePage = document.getElementById('username-page');
const chatpage = document.getElementById('chat-page');

function connect(event) {
  username = $('#name').val()?.trim();
  if (username !== null && username !== '') {
    // ユーザ名入力ページ -> チャットページへの遷移
    $('#username-page').addClass('d-none');
    $('#chat-page').removeClass('d-none');

    // WebSocket接続
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    /**
     * サーバへの接続
     * @param { object } data - サーバに送信するデータ
     *  ※ 認証情報等が該当するが、今回は認証はしないため空
     * @param { function } onConnected - 接続成功時に実行するコールバック関数
     * @param { function } onError - 接続失敗時に実行するコールバック関数
     */
    stompClient.connect({}, onConnected, onError);
  }

  // 今回はformの送信を独自の処理を実装して行うため、ブラウザ標準の送信処理を無効にする
  event.preventDefault();  
}

function onConnected() {
  /**
   * Topicをsubscribeする
   * @param { string } topicURI - subscribeするtopicのURI
   * @param { function } onMessage - メッセージ受信時に実行するコールバック処理
   */
  stompClient.subscribe('/topic/public', onMessage);

  /**
   * 他のチャット参加者にユーザ名を通知するためにユーザ名をサーバに送信する
   * @param { string } destURI - 送信先のURI
   * @param { object } headerInfo - メッセージと一緒に送信するヘッダに加える情報
   *  ※ 今回は無いので空
   * @param { object } message - メッセージ
   */
  stompClient.send(
    '/app/chat.addUser',
    {},
    JSON.stringify({sender: username, content: '', type: 'JOIN'})
  );

  // "Connecting"の表示を消す
  $('.connecting').addClass('d-none');
}

function onError(error) {
  const errorMessage = 'Could not connect to the WebSocket server. Please refresh this page to try again.'
  $('.connecting').text(errorMessage).css('color', 'red');
}

function sendMessage(event) {
  const messageContent = $('#message').val().trim();
  if (messageContent !== null && messageContent !== '') {
    if (stompClient) {
      stompClient.send(
        '/app/chat.sendMessage',
        {},
        JSON.stringify({sender: username, content: messageContent, type: 'CHAT'})
      );

      // 送信完了したらチャット欄を空欄に戻す
      $('#message').val('');
    }
  }

  // 今回はformの送信を独自の処理を実装して行うため、ブラウザ標準の送信処理を無効にする
  event.preventDefault();  
}

function onMessage(payload) {
  const message = JSON.parse(payload.body);

  switch(message.type) {
    case 'JOIN':
      $('#message-area').prepend(`<tr><td class='text-secondary fs-6'>${message.sender} joined</td></tr>`);
      break;
    case 'LEAVE':
      $('#message-area').prepend(`<tr><td class='text-secondary fs-6'>${message.sender} left</td></tr>`);
      break;
    case 'CHAT':
      $('#message-area').prepend(`<tr><td class='fs-5'><b>${message.sender} :</b>${message.content}</td></tr>`);
      break;
    default:
      console.error(`Unknown message type: ${message.type}`);
  }
}

// ボタンに対するイベントリスナー
$('#username-form').on('submit', connect);
$('#message-form').on('submit', sendMessage);
