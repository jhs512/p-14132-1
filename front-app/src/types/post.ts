/** 목록 조회용 DTO (PostDto) */
export interface PostDto {
  id: number;
  createdAt: string;
  modifiedAt: string;
  authorId: number;
  authorName: string;
  authorProfileImgUrl: string;
  title: string;
  published: boolean;
  listed: boolean;
  thumbnailImgUrl: string;
  likesCount: number;
  commentsCount: number;
  hitCount: number;
  actorHasLiked: boolean;
}

/** 상세 조회용 DTO (PostWithContentDto) */
export interface PostWithContentDto {
  id: number;
  createdAt: string;
  modifiedAt: string;
  authorId: number;
  authorName: string;
  authorProfileImgUrl: string;
  title: string;
  content: string;
  published: boolean;
  listed: boolean;
  thumbnailImgUrl: string;
  likesCount: number;
  commentsCount: number;
  hitCount: number;
  actorHasLiked: boolean;
  actorCanModify: boolean;
  actorCanDelete: boolean;
}

/** 페이지네이션 정보 */
export interface PageableDto {
  pageNumber: number;
  pageSize: number;
  offset: number;
  totalElements: number;
  totalPages: number;
  numberOfElements: number;
  sorted: boolean;
}

/** 목록 응답: PageDto<PostDto> */
export interface PostPageResponse {
  content: PostDto[];
  pageable: PageableDto;
}

/** 조회수 응답 */
export interface PostHitResBody {
  resultCode: string;
  msg: string;
  data: {
    hitCount: number;
  };
}

/** 검색 키워드 타입 */
export type PostSearchKeywordType = "ALL" | "TITLE" | "CONTENT" | "AUTHOR_NAME";

/** 정렬 타입 */
export type PostSearchSortType = "ID" | "ID_ASC" | "AUTHOR_NAME" | "AUTHOR_NAME_ASC";
