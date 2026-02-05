import { useState, useCallback } from "react";
import { useSearchParams } from "react-router-dom";
import { useLatestPosts, usePopularByLikes, usePopularByHits } from "../api/posts";
import HeroCarousel from "../components/HeroCarousel";
import HorizontalProjectCard from "../components/HorizontalProjectCard";
import GridProjectCard from "../components/GridProjectCard";

export default function HomePage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const search = searchParams.get("search") ?? "";
  const [inputValue, setInputValue] = useState(search);
  const [showSearch, setShowSearch] = useState(false);

  const { data: latestData } = useLatestPosts(4);
  const { data: likesData } = usePopularByLikes(6);
  const { data: hitsData } = usePopularByHits(6);

  const latestPosts = latestData?.content ?? [];
  const likedPosts = likesData?.content ?? [];
  const hitPosts = hitsData?.content ?? [];

  const handleSearch = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      setSearchParams((prev) => {
        prev.set("search", inputValue);
        prev.set("page", "1");
        return prev;
      });
    },
    [inputValue, setSearchParams],
  );

  return (
    <>
      <div className="relative flex w-full flex-col max-w-[480px] mx-auto">
        {/* Header */}
        <header className="sticky top-0 z-50 flex items-center bg-white/95 backdrop-blur-sm px-4 py-3 justify-between border-b border-gray-100">
          <h1 className="flex items-center gap-2 flex-1">
            <img src="/icon-192.webp" alt="Builders" className="w-7 h-7 rounded-lg" />
            <span className="text-toss-text-primary text-[19px] font-extrabold tracking-tight">Builders</span>
          </h1>
          <div className="flex items-center gap-2">
            <button
              className="flex items-center justify-center rounded-full w-10 h-10 hover:bg-gray-100 transition-colors text-toss-text-primary"
              aria-label="검색"
              onClick={() => setShowSearch((v) => !v)}
            >
              <span className="material-symbols-outlined" style={{ fontSize: 24 }}>
                search
              </span>
            </button>
            <button
              className="flex items-center justify-center rounded-full w-10 h-10 hover:bg-gray-100 transition-colors text-toss-text-primary"
              aria-label="프로필"
            >
              <span className="material-symbols-outlined" style={{ fontSize: 24 }}>
                account_circle
              </span>
            </button>
          </div>
        </header>

        {/* Search Bar */}
        {showSearch && (
          <form onSubmit={handleSearch} className="px-4 py-3 bg-white border-b border-gray-100">
            <input
              type="search"
              name="search"
              autoComplete="off"
              spellCheck={false}
              placeholder="프로젝트 검색…"
              aria-label="프로젝트 검색"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              className="w-full h-11 px-4 rounded-xl bg-toss-gray text-[15px] text-toss-text-primary placeholder:text-toss-text-tertiary focus-visible:ring-2 focus-visible:ring-primary focus-visible:outline-none"
            />
          </form>
        )}

        {/* Hero Carousel */}
        <HeroCarousel posts={latestPosts} />

        {/* 주목할 만한 프로젝트 (좋아요순) */}
        <section className="px-5 pt-8 pb-4">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-toss-text-primary text-[19px] font-bold leading-tight">
              주목할 만한 프로젝트
            </h2>
          </div>
          <div className="flex overflow-x-auto hide-scrollbar gap-3 -mx-5 px-5 pb-2">
            {likedPosts.map((post) => (
              <HorizontalProjectCard key={post.id} post={post} />
            ))}
          </div>
        </section>

        {/* Divider */}
        <div className="h-2 bg-toss-gray w-full" />

        {/* 실시간 인기 프로젝트 (조회순) */}
        <section className="px-4 py-6">
          <div className="flex items-center justify-between mb-4 px-1">
            <h2 className="text-toss-text-primary text-[19px] font-bold leading-tight">
              실시간 인기 프로젝트
            </h2>
          </div>
          <div className="grid grid-cols-2 gap-x-3 gap-y-6">
            {hitPosts.map((post) => (
              <GridProjectCard key={post.id} post={post} />
            ))}
          </div>
        </section>
      </div>

    </>
  );
}
