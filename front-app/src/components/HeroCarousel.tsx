import { useState, useEffect, useCallback, useRef } from "react";
import { Link } from "react-router-dom";
import type { PostDto } from "../types/post";

const AUTO_INTERVAL = 4000;
const SWIPE_THRESHOLD = 50;

export default function HeroCarousel({ posts }: { posts: PostDto[] }) {
  const [current, setCurrent] = useState(0);
  const len = posts.length;
  const touchStart = useRef(0);
  const paused = useRef(false);

  const go = useCallback(
    (dir: 1 | -1) => {
      setCurrent((i) => (i + dir + len) % len);
      // 스와이프 후 자동 슬라이드 잠시 멈춤
      paused.current = true;
      setTimeout(() => { paused.current = false; }, AUTO_INTERVAL);
    },
    [len],
  );

  useEffect(() => {
    if (len <= 1) return;
    const id = setInterval(() => {
      if (!paused.current) setCurrent((i) => (i + 1) % len);
    }, AUTO_INTERVAL);
    return () => clearInterval(id);
  }, [len]);

  const onTouchStart = (e: React.TouchEvent) => {
    touchStart.current = e.touches[0].clientX;
  };

  const onTouchEnd = (e: React.TouchEvent) => {
    const diff = touchStart.current - e.changedTouches[0].clientX;
    if (Math.abs(diff) < SWIPE_THRESHOLD) return;
    if (diff > 0) go(1);   // 왼쪽 스와이프 → 다음
    else go(-1);            // 오른쪽 스와이프 → 이전
  };

  if (len === 0) return null;

  return (
    <div
      className="relative w-full aspect-[16/9] overflow-hidden bg-toss-gray touch-pan-y"
      onTouchStart={onTouchStart}
      onTouchEnd={onTouchEnd}
    >
      {posts.map((post, i) => (
        <Link
          key={post.id}
          to={`/posts/${post.id}`}
          className="absolute inset-0 transition-opacity duration-700"
          style={{ opacity: i === current ? 1 : 0, pointerEvents: i === current ? "auto" : "none" }}
        >
          {post.thumbnailImgUrl ? (
            <img
              src={post.thumbnailImgUrl}
              alt={post.title}
              className="w-full h-full object-cover"
              draggable={false}
            />
          ) : (
            <div className="w-full h-full bg-toss-gray" />
          )}
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent flex flex-col justify-end p-5">
            <h2 className="text-white text-xl font-bold leading-tight drop-shadow-sm">
              {post.title}
            </h2>
            <p className="text-white/80 text-sm mt-1">{post.authorName}</p>
          </div>
        </Link>
      ))}

      {/* Dots */}
      {len > 1 && (
        <div className="absolute bottom-4 right-4 flex gap-1.5 z-10">
          {posts.map((_, i) => (
            <button
              key={i}
              onClick={(e) => { e.preventDefault(); setCurrent(i); paused.current = true; setTimeout(() => { paused.current = false; }, AUTO_INTERVAL); }}
              className={`w-2 h-2 rounded-full transition-colors ${
                i === current ? "bg-white" : "bg-white/40"
              }`}
              aria-label={`슬라이드 ${i + 1}`}
            />
          ))}
        </div>
      )}
    </div>
  );
}
