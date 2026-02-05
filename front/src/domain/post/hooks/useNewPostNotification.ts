"use client";

import { useEffect, useRef, useState } from "react";

import { getStompClient } from "@/global/websocket/stompClient";

export interface PostNotification {
  id: number;
  title: string;
  authorId: number;
  authorName: string;
  authorProfileImgUrl: string;
  createdAt: string;
}

export function useNewPostNotification(
  onNewPost?: (post: PostNotification) => void,
) {
  const [latestPost, setLatestPost] = useState<PostNotification | null>(null);
  const callbackRef = useRef(onNewPost);

  useEffect(() => {
    callbackRef.current = onNewPost;
  }, [onNewPost]);

  useEffect(() => {
    const client = getStompClient();

    const onConnect = () => {
      client.subscribe("/topic/posts/new", (message) => {
        const post: PostNotification = JSON.parse(message.body);
        setLatestPost(post);
        callbackRef.current?.(post);
      });
    };

    client.onConnect = onConnect;

    if (!client.active) {
      client.activate();
    } else if (client.connected) {
      onConnect();
    }

    return () => {
      // 컴포넌트 언마운트 시 연결 유지 (다른 곳에서 사용할 수 있음)
    };
  }, []);

  return { latestPost };
}
