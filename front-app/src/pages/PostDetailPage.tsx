import { useEffect, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { usePost, incrementHit } from "../api/posts";

const numberFormat = new Intl.NumberFormat("ko-KR");
const dateFormat = new Intl.DateTimeFormat("ko-KR", {
  year: "numeric",
  month: "2-digit",
  day: "2-digit",
});

export default function PostDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: post, isLoading } = usePost(id!);
  const hitSent = useRef(false);

  useEffect(() => {
    if (id && !hitSent.current) {
      hitSent.current = true;
      incrementHit(id);
    }
  }, [id]);

  if (isLoading) {
    return (
      <div className="relative flex min-h-screen w-full flex-col max-w-[480px] mx-auto">
        <div className="sticky top-0 z-50 flex h-14 items-center px-4 bg-white/95 backdrop-blur-sm">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center justify-center w-10 h-10 text-toss-text-primary focus-visible:ring-2 focus-visible:ring-primary focus-visible:outline-none rounded-full"
            aria-label="뒤로가기"
          >
            <span className="material-symbols-outlined" style={{ fontSize: 24 }}>
              arrow_back
            </span>
          </button>
        </div>
        <div className="w-full h-[280px] bg-toss-gray animate-pulse" />
        <div className="px-5 pt-6 flex flex-col gap-3">
          <div className="h-7 w-3/4 bg-toss-gray rounded animate-pulse" />
          <div className="h-4 w-1/3 bg-toss-gray rounded animate-pulse" />
          <div className="h-4 w-full bg-toss-gray rounded animate-pulse mt-4" />
          <div className="h-4 w-full bg-toss-gray rounded animate-pulse" />
          <div className="h-4 w-2/3 bg-toss-gray rounded animate-pulse" />
        </div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen max-w-[480px] mx-auto">
        <p className="text-toss-text-secondary text-[15px]">
          게시글을 찾을 수 없어요
        </p>
        <button
          onClick={() => navigate("/")}
          className="mt-4 text-primary text-[15px] font-medium focus-visible:ring-2 focus-visible:ring-primary focus-visible:outline-none rounded-lg px-3 py-1"
        >
          홈으로 돌아가기
        </button>
      </div>
    );
  }

  return (
    <>
      <div className="relative flex w-full flex-col max-w-[480px] mx-auto">
        {/* Top App Bar */}
        <div className="sticky top-0 z-50 flex h-14 w-full items-center justify-between bg-white/95 px-4 backdrop-blur-sm">
          <button
            onClick={() => navigate(-1)}
            aria-label="뒤로가기"
            className="flex size-10 items-center justify-start text-toss-text-primary transition-opacity active:opacity-60 focus-visible:ring-2 focus-visible:ring-primary focus-visible:outline-none rounded-full"
          >
            <span className="material-symbols-outlined" style={{ fontSize: 24 }}>
              arrow_back
            </span>
          </button>
          <div className="size-10" />
        </div>

        {/* Hero Image */}
        <div className="w-full px-0">
          <div className="relative h-[280px] w-full bg-toss-gray rounded-b-2xl overflow-hidden">
            {post.thumbnailImgUrl ? (
              <img
                src={post.thumbnailImgUrl}
                alt={post.title}
                className="h-full w-full object-cover"
              />
            ) : null}
            <div className="absolute inset-0 bg-gradient-to-t from-black/10 to-transparent" />
          </div>
        </div>

        {/* Content */}
        <div className="flex flex-col px-5 pt-6">
          <h1 className="text-[24px] font-bold leading-tight text-toss-text-primary text-wrap-balance">
            {post.title}
          </h1>

          <div className="mt-2 flex items-center gap-1.5 text-[13px] text-toss-text-tertiary">
            <span className="font-medium">{post.authorName}</span>
            <span>·</span>
            <span>{dateFormat.format(new Date(post.createdAt))}</span>
          </div>

          <div className="mt-6 flex flex-col gap-4 text-[15px] leading-relaxed text-toss-text-secondary">
            {post.content.split("\n\n").map((paragraph, i) =>
              paragraph.trim() ? <p key={i}>{paragraph}</p> : null,
            )}
          </div>

          {/* Stats */}
          <div className="mt-8 mb-4 flex items-center gap-4 border-t border-gray-100 pt-6">
            <div className="flex items-center gap-1 text-[13px] text-toss-text-tertiary">
              <span
                className="material-symbols-outlined"
                aria-hidden="true"
                style={{ fontSize: 18 }}
              >
                visibility
              </span>
              <span style={{ fontVariantNumeric: "tabular-nums" }}>
                조회 {numberFormat.format(post.hitCount)}
              </span>
            </div>
            <div className="flex items-center gap-1 text-[13px] text-toss-text-tertiary">
              <span
                className="material-symbols-outlined"
                aria-hidden="true"
                style={{ fontSize: 18, fontVariationSettings: "'FILL' 0" }}
              >
                favorite
              </span>
              <span style={{ fontVariantNumeric: "tabular-nums" }}>
                좋아요 {numberFormat.format(post.likesCount)}
              </span>
            </div>
          </div>
        </div>

        {/* CTA 높이만큼 여백 확보 */}
        <div className="h-28" />
      </div>

      {/* Bottom CTA — 콘텐츠 밖에서 fixed */}
      <div className="fixed bottom-0 left-1/2 -translate-x-1/2 z-40 w-full max-w-[480px] bg-gradient-to-t from-white via-white to-transparent pt-6">
        <div className="bg-white px-4 pb-4">
          <button className="w-full rounded-xl bg-primary h-[52px] flex items-center justify-center text-[16px] font-semibold text-white shadow-lg shadow-primary/20 transition-transform active:scale-[0.98] hover:bg-primary/90 focus-visible:ring-2 focus-visible:ring-primary/50 focus-visible:outline-none">
            연락처 보기 (5,000원)
          </button>
        </div>
        <div
          className="w-full bg-white"
          style={{ height: "env(safe-area-inset-bottom)" }}
        />
      </div>
    </>
  );
}
