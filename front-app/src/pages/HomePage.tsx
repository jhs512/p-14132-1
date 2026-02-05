import { useState, useCallback } from "react";
import { useSearchParams } from "react-router-dom";
import { usePosts } from "../api/posts";
import PostCard from "../components/PostCard";
import PostGridSkeleton from "../components/PostGridSkeleton";
import EmptyState from "../components/EmptyState";

export default function HomePage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const page = Number(searchParams.get("page") ?? "1");
  const search = searchParams.get("search") ?? "";
  const [inputValue, setInputValue] = useState(search);
  const [showSearch, setShowSearch] = useState(false);

  const { data, isLoading } = usePosts(page, search || undefined);
  const posts = data?.content ?? [];
  const totalPages = data?.pageable?.totalPages ?? 0;

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

  const handlePageChange = useCallback(
    (newPage: number) => {
      setSearchParams((prev) => {
        prev.set("page", String(newPage));
        return prev;
      });
      window.scrollTo({ top: 0, behavior: "smooth" });
    },
    [setSearchParams],
  );

  return (
    <div className="relative flex min-h-screen w-full flex-col overflow-x-hidden max-w-[480px] mx-auto">
      {/* Top App Bar */}
      <header className="sticky top-0 z-50 flex items-center bg-white/95 backdrop-blur-sm p-4 justify-between">
        <h1 className="text-toss-text-primary text-[17px] font-semibold leading-tight tracking-[-0.015em] flex-1">
          Builders
        </h1>
        <div className="flex w-12 items-center justify-end">
          <button
            className="flex items-center justify-center rounded-full w-10 h-10 hover:bg-gray-100 transition-colors text-toss-text-primary focus-visible:ring-2 focus-visible:ring-primary focus-visible:outline-none"
            aria-label="검색"
            onClick={() => setShowSearch((v) => !v)}
          >
            <span className="material-symbols-outlined" style={{ fontSize: 24 }}>
              search
            </span>
          </button>
        </div>
      </header>

      {/* Search Bar */}
      {showSearch ? (
        <form onSubmit={handleSearch} className="px-4 pb-4">
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
      ) : null}

      {/* Main Content */}
      <main className="flex-1 flex flex-col">
        {/* Headline */}
        <div className="px-5 pt-6 pb-6 bg-white">
          <h2 className="text-toss-text-primary tracking-tight text-2xl font-bold leading-tight text-wrap-balance">
            10x 빌더를
            <br />
            만나보세요
          </h2>
        </div>

        {/* Card Grid */}
        <div className="flex-1 bg-toss-gray px-4 pt-6 pb-16 rounded-t-3xl min-h-[calc(100vh-160px)]">
          {isLoading ? (
            <PostGridSkeleton />
          ) : posts.length === 0 ? (
            <EmptyState />
          ) : (
            <div className="grid grid-cols-2 gap-3">
              {posts.map((post) => (
                <PostCard key={post.id} post={post} />
              ))}
            </div>
          )}

          {/* Pagination */}
          {totalPages > 1 ? (
            <nav
              className="flex items-center justify-center gap-2 mt-8 pb-8"
              aria-label="페이지네이션"
            >
              <button
                disabled={page <= 1}
                onClick={() => handlePageChange(page - 1)}
                className="flex items-center justify-center w-9 h-9 rounded-full text-toss-text-tertiary hover:bg-white disabled:opacity-30 disabled:cursor-not-allowed transition-colors focus-visible:ring-2 focus-visible:ring-primary focus-visible:outline-none"
                aria-label="이전 페이지"
              >
                <span className="material-symbols-outlined" style={{ fontSize: 20 }}>
                  chevron_left
                </span>
              </button>
              <span
                className="text-[14px] text-toss-text-secondary font-medium"
                style={{ fontVariantNumeric: "tabular-nums" }}
              >
                {page} / {totalPages}
              </span>
              <button
                disabled={page >= totalPages}
                onClick={() => handlePageChange(page + 1)}
                className="flex items-center justify-center w-9 h-9 rounded-full text-toss-text-tertiary hover:bg-white disabled:opacity-30 disabled:cursor-not-allowed transition-colors focus-visible:ring-2 focus-visible:ring-primary focus-visible:outline-none"
                aria-label="다음 페이지"
              >
                <span className="material-symbols-outlined" style={{ fontSize: 20 }}>
                  chevron_right
                </span>
              </button>
            </nav>
          ) : null}
        </div>
      </main>
    </div>
  );
}
