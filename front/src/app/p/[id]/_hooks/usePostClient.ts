import { MutableRefObject, useEffect, useRef, useState } from "react";

import type { components } from "@/global/backend/apiV1/schema";
import client from "@/global/backend/client";
import { toast } from "sonner";

import { processMarkdownContent } from "@/lib/business/markdownUtils";

type PostWithContentDto = components["schemas"]["PostWithContentDto"];
type RsDataVoid = components["schemas"]["RsDataVoid"];

const POLLING_INTERVAL = 10000; // 10초

// eslint-disable-next-line @typescript-eslint/no-explicit-any
type EditorRef = MutableRefObject<any>;

export default function usePostClient(initialPost: PostWithContentDto) {
  const [post, setPost] = useState<PostWithContentDto | null>(initialPost);
  const lastModifyDateAfterRef = useRef(initialPost.modifiedAt);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const editorRef = useRef<any>(null);
  const hitCalledRef = useRef(false);

  // 조회수 증가: 클라이언트 사이드에서 한 번만 호출
  useEffect(() => {
    if (hitCalledRef.current) return;
    hitCalledRef.current = true;

    client
      .POST("/post/api/v1/posts/{id}/hit", {
        params: {
          path: { id: initialPost.id },
        },
      })
      .then((res) => {
        const hitCount = res.data?.data?.hitCount;
        if (hitCount !== undefined) {
          setPost((prev) => (prev ? { ...prev, hitCount } : prev));
        }
      })
      .catch(() => {
        // 조회수 증가 실패 무시
      });
  }, [initialPost.id]);

  // 라이브 리로드: 10초마다 변경 확인
  useEffect(() => {
    let timeoutId: NodeJS.Timeout;
    let isComponentMounted = true;
    let isPollingInProgress = false;

    const checkForUpdates = async () => {
      if (!isComponentMounted || document.hidden) {
        return;
      }

      if (isPollingInProgress) {
        return;
      }

      isPollingInProgress = true;

      try {
        const res = await client.GET("/post/api/v1/posts/{id}", {
          params: {
            path: { id: initialPost.id },
            query: {
              lastModifyDateAfter: lastModifyDateAfterRef.current,
            },
          },
        });

        if (!isComponentMounted) return;

        // 200이면 업데이트가 있음
        if (res.response.status === 200 && res.data) {
          lastModifyDateAfterRef.current = res.data.modifiedAt;

          // Toast UI Editor 직접 업데이트
          if (editorRef.current?.getInstance) {
            const processedContent = processMarkdownContent(
              res.data.content,
              res.data.id,
            );
            editorRef.current.getInstance().setMarkdown(processedContent);
          }

          setPost((prev) =>
            prev
              ? {
                  ...prev,
                  title: res.data.title,
                  content: res.data.content,
                  modifiedAt: res.data.modifiedAt,
                  likesCount: res.data.likesCount,
                  commentsCount: res.data.commentsCount,
                  hitCount: res.data.hitCount,
                }
              : prev,
          );

          toast("문서 업데이트", {
            description: "새로운 내용으로 업데이트되었습니다.",
          });
        }
        // 412면 변경 없음, 무시
      } catch {
        // 에러 무시 (네트워크 오류 등)
      } finally {
        isPollingInProgress = false;
      }

      if (isComponentMounted) {
        scheduleNextPoll();
      }
    };

    const scheduleNextPoll = () => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(checkForUpdates, POLLING_INTERVAL);
    };

    const handleVisibilityChange = () => {
      if (!document.hidden && isComponentMounted) {
        clearTimeout(timeoutId);
        checkForUpdates();
      }
    };

    const handleFocus = () => {
      if (isComponentMounted && !document.hidden) {
        clearTimeout(timeoutId);
        checkForUpdates();
      }
    };

    const handleOnline = () => {
      if (isComponentMounted && !document.hidden) {
        clearTimeout(timeoutId);
        checkForUpdates();
      }
    };

    // 초기 폴링 시작
    scheduleNextPoll();

    document.addEventListener("visibilitychange", handleVisibilityChange);
    window.addEventListener("focus", handleFocus);
    window.addEventListener("online", handleOnline);

    return () => {
      isComponentMounted = false;
      clearTimeout(timeoutId);
      document.removeEventListener("visibilitychange", handleVisibilityChange);
      window.removeEventListener("focus", handleFocus);
      window.removeEventListener("online", handleOnline);
    };
  }, [initialPost.id]);

  const deletePost = (id: number, onSuccess: () => void) => {
    client
      .DELETE("/post/api/v1/posts/{id}", {
        params: {
          path: {
            id,
          },
        },
      })
      .then((res) => {
        if (res.error) {
          toast.error(res.error.msg);
          return;
        }

        toast.success(res.data.msg);
        onSuccess();
      });
  };

  const modifyPost = (
    id: number,
    title: string,
    content: string,
    onSuccess: (res: RsDataVoid) => void,
  ) => {
    client
      .PUT("/post/api/v1/posts/{id}", {
        params: {
          path: {
            id,
          },
        },
        body: {
          title,
          content,
        },
      })
      .then((res) => {
        if (res.error) {
          toast.error(res.error.msg);
          return;
        }

        onSuccess(res.data);
      });
  };

  const toggleLike = (id: number) => {
    client
      .POST("/post/api/v1/posts/{id}/like", {
        params: {
          path: {
            id,
          },
        },
      })
      .then((res) => {
        if (res.error) {
          toast.error(res.error.msg);
          return;
        }

        if (post) {
          setPost({
            ...post,
            actorHasLiked: res.data.data.liked,
            likesCount: res.data.data.likesCount,
          });
        }

        toast.success(res.data.msg);
      });
  };

  return {
    post,
    setPost,
    editorRef: editorRef as EditorRef,
    deletePost,
    modifyPost,
    toggleLike,
  };
}
