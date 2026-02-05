import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const NEXT_PUBLIC_API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

let stompClient: Client | null = null;

export function getStompClient(): Client {
  if (stompClient) return stompClient;

  stompClient = new Client({
    webSocketFactory: () => new SockJS(`${NEXT_PUBLIC_API_BASE_URL}/ws`),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  return stompClient;
}

export function subscribe(
  destination: string,
  callback: (message: IMessage) => void,
) {
  const client = getStompClient();

  if (client.connected) {
    return client.subscribe(destination, callback);
  }

  // 연결 후 구독
  const originalOnConnect = client.onConnect;
  client.onConnect = (frame) => {
    originalOnConnect?.(frame);
    client.subscribe(destination, callback);
  };

  if (!client.active) {
    client.activate();
  }

  return null;
}

export function disconnect() {
  if (stompClient?.active) {
    stompClient.deactivate();
  }
}
