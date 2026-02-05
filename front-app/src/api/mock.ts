import type {
  PostDto,
  PostWithContentDto,
  PostPageResponse,
} from "../types/post";

const MOCK_POSTS: (PostDto & { content: string })[] = [
  {
    id: 1,
    createdAt: "2025-12-01T09:00:00Z",
    modifiedAt: "2025-12-01T09:00:00Z",
    authorId: 1,
    authorName: "김바이브",
    authorProfileImgUrl: "",
    title: "AI 기반 실시간 번역 채팅앱",
    content:
      "GPT-4o와 WebSocket을 활용한 실시간 다국어 채팅 앱입니다.\n\n한국어로 입력하면 상대방에게는 자동으로 영어/일본어/중국어로 번역되어 전달됩니다. 반대도 마찬가지입니다.\n\n기술 스택: React Native, Node.js, Socket.io, OpenAI API\n\n현재 MVP 단계이며, 베타 테스터 200명이 사용 중입니다. 월 활성 사용자 수가 꾸준히 증가하고 있습니다.",
    thumbnailImgUrl: "https://picsum.photos/seed/chat-app/600/450",
    published: true,
    listed: true,
    hitCount: 1842,
    likesCount: 127,
    commentsCount: 23,
    actorHasLiked: false,
  },
  {
    id: 2,
    createdAt: "2025-11-28T14:30:00Z",
    modifiedAt: "2025-11-28T14:30:00Z",
    authorId: 2,
    authorName: "이풀스택",
    authorProfileImgUrl: "",
    title: "소상공인 매출 분석 대시보드",
    content:
      "POS 데이터를 연동하여 소상공인이 쉽게 매출 트렌드를 파악할 수 있는 대시보드입니다.\n\n일별/주별/월별 매출 추이, 인기 메뉴 분석, 피크 타임 예측 기능을 제공합니다.\n\n기술 스택: Next.js, Supabase, Chart.js, Vercel\n\n카페 5곳에서 파일럿 운영 중이며, 사장님들의 반응이 매우 좋습니다.",
    thumbnailImgUrl: "https://picsum.photos/seed/dashboard/600/450",
    published: true,
    listed: true,
    hitCount: 956,
    likesCount: 89,
    commentsCount: 15,
    actorHasLiked: false,
  },
  {
    id: 3,
    createdAt: "2025-11-25T11:00:00Z",
    modifiedAt: "2025-11-25T11:00:00Z",
    authorId: 3,
    authorName: "박코더",
    authorProfileImgUrl: "",
    title: "반려동물 건강 관리 플랫폼",
    content:
      "반려동물의 식단, 운동, 병원 기록을 한 곳에서 관리할 수 있는 플랫폼입니다.\n\nAI가 반려동물의 건강 상태를 분석하고, 이상 징후가 감지되면 근처 동물병원을 추천해줍니다.\n\n기술 스택: Flutter, Firebase, TensorFlow Lite\n\n앱스토어 출시 완료, 현재 DAU 500명 돌파했습니다.",
    thumbnailImgUrl: "https://picsum.photos/seed/pet-health/600/450",
    published: true,
    listed: true,
    hitCount: 2310,
    likesCount: 203,
    commentsCount: 41,
    actorHasLiked: false,
  },
  {
    id: 4,
    createdAt: "2025-11-20T16:45:00Z",
    modifiedAt: "2025-11-20T16:45:00Z",
    authorId: 4,
    authorName: "최빌더",
    authorProfileImgUrl: "",
    title: "프리랜서 계약서 자동 생성기",
    content:
      "프리랜서와 클라이언트 간의 계약서를 AI로 자동 생성하는 서비스입니다.\n\n프로젝트 유형, 기간, 금액 등을 입력하면 법적으로 유효한 계약서 초안을 만들어줍니다.\n\n기술 스택: Next.js, Anthropic Claude API, PDF-lib\n\n변호사 감수를 받은 템플릿 기반으로 작동하며, 월 300건 이상의 계약서가 생성되고 있습니다.",
    thumbnailImgUrl: "https://picsum.photos/seed/contract/600/450",
    published: true,
    listed: true,
    hitCount: 3150,
    likesCount: 287,
    commentsCount: 52,
    actorHasLiked: false,
  },
  {
    id: 5,
    createdAt: "2025-11-18T08:20:00Z",
    modifiedAt: "2025-11-18T08:20:00Z",
    authorId: 5,
    authorName: "정메이커",
    authorProfileImgUrl: "",
    title: "동네 러닝 크루 매칭 앱",
    content:
      "같은 동네에서 비슷한 페이스로 달리는 러닝 크루를 매칭해주는 앱입니다.\n\nGPS 기반으로 주변 러닝 크루를 찾고, 실시간으로 함께 달리는 기능을 제공합니다.\n\n기술 스택: React Native, Mapbox, Supabase Realtime\n\n서울 강남/서초 지역에서 시범 운영 중이며, 주간 활성 크루 30개를 넘었습니다.",
    thumbnailImgUrl: "https://picsum.photos/seed/running/600/450",
    published: true,
    listed: true,
    hitCount: 1205,
    likesCount: 156,
    commentsCount: 28,
    actorHasLiked: false,
  },
  {
    id: 6,
    createdAt: "2025-11-15T13:10:00Z",
    modifiedAt: "2025-11-15T13:10:00Z",
    authorId: 6,
    authorName: "한개발",
    authorProfileImgUrl: "",
    title: "AI 면접 코칭 서비스",
    content:
      "취업 준비생을 위한 AI 모의 면접 서비스입니다.\n\n직무별 맞춤 질문을 생성하고, 답변에 대한 피드백을 실시간으로 제공합니다. 표정과 목소리 톤도 분석합니다.\n\n기술 스택: Next.js, WebRTC, OpenAI Whisper, Claude\n\n대학교 3곳 취업센터와 MOU를 체결하고 서비스 중입니다.",
    thumbnailImgUrl: "https://picsum.photos/seed/interview/600/450",
    published: true,
    listed: true,
    hitCount: 4521,
    likesCount: 412,
    commentsCount: 67,
    actorHasLiked: false,
  },
  {
    id: 7,
    createdAt: "2025-11-10T10:30:00Z",
    modifiedAt: "2025-11-10T10:30:00Z",
    authorId: 7,
    authorName: "윤스택",
    authorProfileImgUrl: "",
    title: "중고 명품 AI 감정 서비스",
    content:
      "사진만 찍으면 AI가 명품의 진위 여부를 판별하는 서비스입니다.\n\n가방, 시계, 신발 등 카테고리별 전문 모델이 학습되어 있으며, 정확도 94%를 달성했습니다.\n\n기술 스택: Python, FastAPI, PyTorch, React\n\n중고거래 플랫폼 2곳과 API 연동을 협의 중입니다.",
    thumbnailImgUrl: "https://picsum.photos/seed/luxury/600/450",
    published: true,
    listed: true,
    hitCount: 5823,
    likesCount: 531,
    commentsCount: 89,
    actorHasLiked: false,
  },
  {
    id: 8,
    createdAt: "2025-11-05T15:00:00Z",
    modifiedAt: "2025-11-05T15:00:00Z",
    authorId: 8,
    authorName: "송크래프트",
    authorProfileImgUrl: "",
    title: "시니어를 위한 키오스크 연습 앱",
    content:
      "디지털 소외 계층인 시니어분들이 키오스크 사용법을 연습할 수 있는 앱입니다.\n\n실제 카페, 병원, 관공서 키오스크를 시뮬레이션하여 안전하게 연습할 수 있습니다.\n\n기술 스택: React, TypeScript, Framer Motion\n\n복지관 10곳에 도입 완료, 월 사용자 2,000명을 돌파했습니다.",
    thumbnailImgUrl: "https://picsum.photos/seed/kiosk/600/450",
    published: true,
    listed: true,
    hitCount: 7102,
    likesCount: 892,
    commentsCount: 134,
    actorHasLiked: false,
  },
];

type SortField = "ID" | "LIKES" | "HITS";

export function getMockPostList(
  page: number,
  pageSize: number,
  kw?: string,
  sort: SortField = "ID",
): PostPageResponse {
  let filtered = [...MOCK_POSTS];
  if (kw) {
    const q = kw.toLowerCase();
    filtered = filtered.filter(
      (p) =>
        p.title.toLowerCase().includes(q) ||
        p.authorName.toLowerCase().includes(q),
    );
  }

  if (sort === "LIKES") {
    filtered.sort((a, b) => b.likesCount - a.likesCount);
  } else if (sort === "HITS") {
    filtered.sort((a, b) => b.hitCount - a.hitCount);
  }

  const start = (page - 1) * pageSize;
  const content = filtered.slice(start, start + pageSize);
  const totalPages = Math.ceil(filtered.length / pageSize);

  return {
    content,
    pageable: {
      pageNumber: page,
      pageSize,
      offset: start,
      totalElements: filtered.length,
      totalPages,
      numberOfElements: content.length,
      sorted: true,
    },
  };
}

export function getMockPost(id: string): PostWithContentDto {
  const post = MOCK_POSTS.find((p) => p.id === Number(id));
  if (!post) throw new Error("Post not found");
  return {
    id: post.id,
    createdAt: post.createdAt,
    modifiedAt: post.modifiedAt,
    authorId: post.authorId,
    authorName: post.authorName,
    authorProfileImgUrl: post.authorProfileImgUrl,
    title: post.title,
    content: post.content,
    published: post.published,
    listed: post.listed,
    thumbnailImgUrl: post.thumbnailImgUrl,
    likesCount: post.likesCount,
    commentsCount: post.commentsCount,
    hitCount: post.hitCount,
    actorHasLiked: false,
    actorCanModify: false,
    actorCanDelete: false,
  };
}
