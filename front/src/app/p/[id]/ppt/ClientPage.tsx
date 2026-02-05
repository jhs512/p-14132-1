"use client";

import {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from "react";

import { Button } from "@/components/ui/button";

import { ChevronLeft, ChevronRight, Home, ZoomIn, ZoomOut } from "lucide-react";

interface ClientPageProps {
  postId: number;
  pptTitle: string;
  html: string;
  css: string;
}

export default function ClientPage({
  postId,
  pptTitle,
  html,
  css,
}: ClientPageProps) {
  const getSlideFromHash = useCallback(() => {
    if (typeof window === "undefined") return 0;
    const hash = window.location.hash;
    if (hash) {
      const slideNum = parseInt(hash.substring(1));
      if (!isNaN(slideNum) && slideNum > 0) {
        return slideNum - 1;
      }
    }
    return 0;
  }, []);

  // HTML에서 슬라이드 개수 추출 (section 태그 기준)
  const slideCount = (html.match(/<section/g) || []).length;

  // 서버/클라이언트 일치를 위해 초기값은 항상 0으로 설정, 클라이언트에서 해시 기반으로 초기화
  const [currentSlide, setCurrentSlide] = useState(0);
  const [zoomLevel, setZoomLevel] = useState(1);
  const [isInitialized, setIsInitialized] = useState(false);
  const [showControls, setShowControls] = useState(true);

  const zoomIn = useCallback(
    () => setZoomLevel((prev) => Math.min(prev + 0.1, 2)),
    [],
  );
  const zoomOut = useCallback(
    () => setZoomLevel((prev) => Math.max(prev - 0.1, 0.5)),
    [],
  );
  const resetZoom = useCallback(() => setZoomLevel(1), []);

  const goToSlide = useCallback(
    (index: number) => {
      if (index >= 0 && index < slideCount) {
        setCurrentSlide(index);
      }
    },
    [slideCount],
  );

  const nextSlide = useCallback(() => {
    goToSlide(currentSlide + 1);
  }, [currentSlide, goToSlide]);

  const prevSlide = useCallback(() => {
    goToSlide(currentSlide - 1);
  }, [currentSlide, goToSlide]);

  // 키보드 네비게이션
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Zoom Shortcuts: Cmd/Ctrl + (+/- / 0)
      if (e.metaKey || e.ctrlKey) {
        if (e.key === "=" || e.key === "+") {
          e.preventDefault();
          zoomIn();
          return;
        }
        if (e.key === "-") {
          e.preventDefault();
          zoomOut();
          return;
        }
        if (e.key === "0") {
          e.preventDefault();
          resetZoom();
          return;
        }
      }

      switch (e.key) {
        case "ArrowRight":
        case " ":
        case "PageDown":
          e.preventDefault();
          nextSlide();
          break;
        case "ArrowLeft":
        case "PageUp":
          e.preventDefault();
          prevSlide();
          break;
        case "Home":
          e.preventDefault();
          goToSlide(0);
          break;
        case "End":
          e.preventDefault();
          goToSlide(slideCount - 1);
          break;
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [nextSlide, prevSlide, goToSlide, slideCount, zoomIn, zoomOut, resetZoom]);

  // 초기 로드 시 해시 확인 (클라이언트에서만 실행) - useLayoutEffect로 hydration 전에 동기 실행
  // URL 해시와 동기화하는 것은 외부 시스템과의 정당한 동기화 사례
  const isInitializedRef = useRef(false);
  useLayoutEffect(() => {
    if (isInitializedRef.current) return;
    isInitializedRef.current = true;

    const initialSlide = getSlideFromHash();
    if (initialSlide >= 0 && initialSlide < slideCount) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setCurrentSlide(initialSlide);
    }
    setIsInitialized(true);
  }, [getSlideFromHash, slideCount]);

  // 슬라이드 변경 시 해시 업데이트
  useEffect(() => {
    if (isInitialized) {
      const newHash = `#${currentSlide + 1}`;
      if (window.location.hash !== newHash) {
        window.history.replaceState(null, "", newHash);
      }
    }
  }, [currentSlide, isInitialized]);

  // 해시 변경 감지 (뒤로가기/앞으로가기 지원)
  useEffect(() => {
    const handleHashChange = () => {
      const slideNum = getSlideFromHash();
      if (slideNum !== currentSlide && slideNum >= 0 && slideNum < slideCount) {
        setCurrentSlide(slideNum);
      }
    };

    window.addEventListener("hashchange", handleHashChange);
    return () => window.removeEventListener("hashchange", handleHashChange);
  }, [currentSlide, slideCount, getSlideFromHash]);

  // Mermaid/PlantUML 다이어그램 중앙 정렬 적용
  useEffect(() => {
    const applyDiagramStyles = () => {
      // foreignObject 내부의 section에서 다이어그램 이미지 찾기
      const sections = document.querySelectorAll(
        ".marpit svg foreignObject section",
      );
      sections.forEach((section) => {
        const img = section.querySelector(
          'img[src*="mermaid.ink"], img[src*="plantuml.com"]',
        ) as HTMLImageElement;
        if (img) {
          // section 스타일링 - flexbox로 중앙 정렬
          const sectionEl = section as HTMLElement;
          sectionEl.style.display = "flex";
          sectionEl.style.flexDirection = "column";
          sectionEl.style.justifyContent = "center";
          sectionEl.style.alignItems = "center";

          // 이미지 스타일링
          img.style.maxHeight = "85%";
          img.style.maxWidth = "90%";
          img.style.width = "auto";
          img.style.height = "auto";
          img.style.objectFit = "contain";
          img.style.margin = "auto";

          // 부모 p 태그 스타일링
          const parent = img.parentElement;
          if (parent?.tagName === "P") {
            parent.style.display = "flex";
            parent.style.justifyContent = "center";
            parent.style.alignItems = "center";
            parent.style.flex = "1";
            parent.style.width = "100%";
            parent.style.margin = "0";
          }
        }
      });
    };

    // 초기 적용 및 슬라이드 변경 시 재적용
    applyDiagramStyles();
    // 이미지 로드 완료 후 재적용
    const timer = setTimeout(applyDiagramStyles, 100);
    return () => clearTimeout(timer);
  }, [currentSlide, html]);

  const handleContainerClick = useCallback(
    (e: React.MouseEvent<HTMLDivElement>) => {
      const target = e.target as HTMLElement;
      if (
        target.tagName === "BUTTON" ||
        target.tagName === "A" ||
        target.closest("button") ||
        target.closest("a")
      ) {
        return;
      }

      const { clientX } = e;
      const { innerWidth } = window;
      const xPercentage = (clientX / innerWidth) * 100;

      if (xPercentage < 20) {
        prevSlide();
      } else if (xPercentage > 80) {
        nextSlide();
      } else {
        setShowControls((prev) => !prev);
      }
    },
    [nextSlide, prevSlide],
  );

  return (
    <div
      className="fixed inset-0 bg-black flex flex-col"
      onClick={handleContainerClick}
    >
      {/* 상단 컨트롤 바 (세로 모드/데스크톱용) */}
      <div
        className={`absolute top-0 left-0 right-0 z-10 bg-black/50 text-white p-2 landscape:hidden flex items-center justify-between transition-opacity duration-300 ${showControls ? "opacity-100" : "opacity-0 hover:opacity-100"}`}
      >
        <div className="flex items-center gap-2">
          <Button variant="ghost" size="icon" asChild>
            <a href={`/p/${postId}`}>
              <Home className="w-5 h-5" />
            </a>
          </Button>
          <span className="text-sm truncate max-w-[200px]">{pptTitle}</span>
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="icon"
            onClick={(e) => {
              e.stopPropagation();
              zoomOut();
            }}
          >
            <ZoomOut className="w-5 h-5" />
          </Button>
          <span className="text-sm w-12 text-center">
            {Math.round(zoomLevel * 100)}%
          </span>
          <Button
            variant="ghost"
            size="icon"
            onClick={(e) => {
              e.stopPropagation();
              zoomIn();
            }}
          >
            <ZoomIn className="w-5 h-5" />
          </Button>
          <span className="text-sm ml-4">
            {currentSlide + 1} / {slideCount}
          </span>
        </div>
      </div>

      {/* 우측 사이드바 (가로 모드 전용) */}
      <div
        className={`hidden landscape:flex fixed right-0 top-0 bottom-0 w-12 z-20 bg-black/80 text-white flex-col items-center justify-between py-4 border-l border-white/10 transition-transform duration-300 ${showControls ? "translate-x-0" : "translate-x-full"}`}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex flex-col items-center gap-4">
          <Button variant="ghost" size="icon" asChild className="w-10 h-10">
            <a href={`/p/${postId}`}>
              <Home className="w-5 h-5" />
            </a>
          </Button>
        </div>

        <div className="flex flex-col items-center gap-2">
          <Button
            variant="ghost"
            size="icon"
            onClick={zoomIn}
            className="w-10 h-10"
          >
            <ZoomIn className="w-5 h-5" />
          </Button>
          <span className="text-[10px] font-medium rotate-90 my-2">
            {Math.round(zoomLevel * 100)}%
          </span>
          <Button
            variant="ghost"
            size="icon"
            onClick={zoomOut}
            className="w-10 h-10"
          >
            <ZoomOut className="w-5 h-5" />
          </Button>
        </div>

        <div className="flex flex-col items-center gap-2">
          <Button
            variant="ghost"
            size="icon"
            onClick={nextSlide}
            disabled={currentSlide === slideCount - 1}
            className="w-10 h-10"
          >
            <ChevronRight className="w-5 h-5" />
          </Button>
          <span className="text-xs font-bold my-1">{currentSlide + 1}</span>
          <div className="w-6 h-[1px] bg-white/20" />
          <span className="text-[10px] opacity-60 my-1">{slideCount}</span>
          <Button
            variant="ghost"
            size="icon"
            onClick={prevSlide}
            disabled={currentSlide === 0}
            className="w-10 h-10"
          >
            <ChevronLeft className="w-5 h-5" />
          </Button>
        </div>
      </div>

      {/* 슬라이드 영역 */}
      <div className="flex-1 flex items-center justify-center overflow-hidden">
        <style dangerouslySetInnerHTML={{ __html: css }} />
        <style
          dangerouslySetInnerHTML={{
            __html: `
          .marpit {
            --ppt-font-size: ${24 * zoomLevel}px;
            display: flex;
            width: 100%;
            height: 100%;
            align-items: center;
            justify-content: center;
          }
          .marpit section {
            font-size: var(--ppt-font-size) !important;
          }
          .marpit > svg {
            width: 100%;
            height: 100%;
            box-shadow: 0 5px 15px rgba(0,0,0,0.5);
            display: none;
          }
          .marpit > svg:nth-of-type(${currentSlide + 1}) {
            display: block;
          }

          /* Fix Code Wrapping */
          .marpit pre, .marpit code {
            white-space: pre-wrap !important;
            word-break: break-all !important;
          }

          /* Force Light Theme for Code Blocks in Marp */
          .marpit code[class*="language-"],
          .marpit pre[class*="language-"] {
            color: black !important;
            text-shadow: 0 1px white !important;
            background: #f5f2f0 !important;
          }

          .marpit :not(pre) > code[class*="language-"],
          .marpit pre[class*="language-"] {
            background: #f5f2f0 !important;
          }

          .marpit .token.comment,
          .marpit .token.prolog,
          .marpit .token.doctype,
          .marpit .token.cdata {
            color: slategray !important;
          }

          .marpit .token.punctuation {
            color: #999 !important;
          }

          .marpit .token.namespace {
            opacity: 0.7 !important;
          }

          .marpit .token.property,
          .marpit .token.tag,
          .marpit .token.boolean,
          .marpit .token.number,
          .marpit .token.constant,
          .marpit .token.symbol,
          .marpit .token.deleted {
            color: #905 !important;
          }

          .marpit .token.selector,
          .marpit .token.attr-name,
          .marpit .token.string,
          .marpit .token.char,
          .marpit .token.builtin,
          .marpit .token.inserted {
            color: #690 !important;
          }

          .marpit .token.operator,
          .marpit .token.entity,
          .marpit .token.url,
          .marpit .language-css .token.string,
          .marpit .style .token.string {
            color: #9a6e3a !important;
            background: hsla(0, 0%, 100%, 0.5) !important;
          }

          .marpit .token.atrule,
          .marpit .token.attr-value,
          .marpit .token.keyword {
            color: #07a !important;
          }

          .marpit .token.function,
          .marpit .token.class-name {
            color: #dd4a68 !important;
          }

          .marpit .token.regex,
          .marpit .token.important,
          .marpit .token.variable {
            color: #e90 !important;
          }

        `,
          }}
        />
        <div
          className="marp-slides"
          style={{
            width: "100%",
            height: "100%",
          }}
          dangerouslySetInnerHTML={{ __html: html }}
        />
      </div>

      {/* 네비게이션 버튼 (세로 모드용) */}
      <div
        className={`absolute bottom-4 left-0 right-0 flex landscape:hidden justify-center gap-4 transition-opacity duration-300 ${showControls ? "opacity-100" : "opacity-0 hover:opacity-100"}`}
      >
        <Button
          variant="secondary"
          size="icon"
          onClick={prevSlide}
          disabled={currentSlide === 0}
        >
          <ChevronLeft className="w-5 h-5" />
        </Button>
        <Button
          variant="secondary"
          size="icon"
          onClick={nextSlide}
          disabled={currentSlide === slideCount - 1}
        >
          <ChevronRight className="w-5 h-5" />
        </Button>
      </div>

      {/* 진행률 바 */}
      <div className="absolute bottom-0 left-0 right-0 h-1 bg-gray-800 z-50">
        <div
          className="h-full bg-white/70 transition-all duration-300 ease-out"
          style={{ width: `${((currentSlide + 1) / slideCount) * 100}%` }}
        />
      </div>
    </div>
  );
}
