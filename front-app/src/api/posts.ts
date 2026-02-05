import useSWR from "swr";
import type {
  PostPageResponse,
  PostWithContentDto,
  PostSearchKeywordType,
} from "../types/post";
import { getMockPostList, getMockPost } from "./mock";

const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";
const USE_MOCK = import.meta.env.VITE_USE_MOCK === "true";

const fetcher = async (url: string) => {
  const res = await fetch(url);
  if (!res.ok) throw new Error(`API error: ${res.status}`);
  return res.json();
};

export function usePosts(
  page: number,
  kw?: string,
  kwType: PostSearchKeywordType = "ALL",
) {
  const params = new URLSearchParams({
    page: String(page),
    pageSize: "10",
    kwType,
  });
  if (kw) params.set("kw", kw);

  return useSWR<PostPageResponse>(
    `posts?page=${page}&kw=${kw ?? ""}&kwType=${kwType}`,
    USE_MOCK
      ? () => getMockPostList(page, 10, kw)
      : () => fetcher(`${API_BASE}/post/api/v1/posts?${params}`),
  );
}

export function usePost(id: string) {
  return useSWR<PostWithContentDto>(
    `posts/${id}`,
    USE_MOCK
      ? () => getMockPost(id)
      : () => fetcher(`${API_BASE}/post/api/v1/posts/${id}`),
  );
}

export async function incrementHit(id: string) {
  if (USE_MOCK) return;
  await fetch(`${API_BASE}/post/api/v1/posts/${id}/hit`, { method: "POST" });
}
