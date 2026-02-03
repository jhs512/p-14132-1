# CLAUDE.md

이 파일은 Claude Code가 이 저장소에서 작업할 때 참고하는 컨텍스트 문서입니다.

## 프로젝트 개요

SLOG - 개인 블로그 플랫폼 (Spring Boot + Next.js)

## 기술 스택

### 백엔드 (back/)
- **언어**: Kotlin 2.2, Java 24
- **프레임워크**: Spring Boot 4.0
- **데이터베이스**: H2 (개발), JPA + QueryDSL
- **인증**: Spring Security, OAuth2, JWT
- **API 문서**: SpringDoc OpenAPI (Swagger)

### 프론트엔드 (front/)
- **프레임워크**: Next.js 16 (App Router)
- **언어**: TypeScript
- **스타일링**: Tailwind CSS 4
- **상태관리**: React Hook Form, Zod
- **UI 컴포넌트**: Radix UI, shadcn/ui
- **에디터**: Toast UI Editor
- **패키지 매니저**: pnpm

## 프로젝트 구조

```
p-14132-1/
├── back/                          # Spring Boot 백엔드
│   └── src/main/kotlin/com/back/
│       ├── boundedContexts/       # 도메인별 바운디드 컨텍스트
│       │   ├── home/              # 홈 컨텍스트
│       │   ├── member/            # 회원 컨텍스트
│       │   └── post/              # 게시물 컨텍스트
│       │       ├── app/           # 애플리케이션 서비스 (Facade)
│       │       ├── domain/        # 도메인 엔티티
│       │       ├── dto/           # DTO
│       │       ├── in/            # 인바운드 어댑터 (Controller)
│       │       └── out/           # 아웃바운드 어댑터 (Repository)
│       ├── global/                # 전역 설정
│       │   ├── dto/               # 공통 DTO (RsData 등)
│       │   ├── exception/         # 예외 처리
│       │   ├── jpa/               # JPA 공통 (BaseTime 등)
│       │   ├── security/          # Spring Security 설정
│       │   └── web/               # Web 설정 (Rq 등)
│       └── standard/              # 표준 유틸리티
├── front/                         # Next.js 프론트엔드
│   └── src/
│       ├── app/                   # App Router 페이지
│       ├── components/            # 공통 컴포넌트 (shadcn/ui)
│       ├── domain/                # 도메인별 컴포넌트/훅
│       ├── global/                # 전역 설정 (auth, backend)
│       └── lib/                   # 유틸리티
└── docs/                          # 문서
    └── plans/                     # 설계 문서
```

## 주요 빌드/실행 명령어

### 백엔드
```bash
cd back
./gradlew bootRun              # 개발 서버 실행
./gradlew compileKotlin        # 컴파일 확인
./gradlew test                 # 테스트 실행
```

### 프론트엔드
```bash
cd front
pnpm install                   # 의존성 설치
pnpm dev                       # 개발 서버 실행 (localhost:3000)
pnpm build                     # 프로덕션 빌드
pnpm check                     # format + tsc + lint
```

## 코딩 컨벤션

### 백엔드 (Kotlin)
- **아키텍처**: 헥사고날 아키텍처 기반 바운디드 컨텍스트
- **서비스 네이밍**: `XxxFacade` (애플리케이션 서비스)
- **컨트롤러 네이밍**: `ApiV1XxxController`
- **응답 형식**: `RsData<T>` (resultCode, msg, data)
- **권한 체크**:
  - `getCheckActorCanXxxRs(actor)` - RsData 반환 (권한 조회용)
  - `checkActorCanXxx(actor)` - 예외 던지기 (권한 검증용)
- **DTO 권한 필드**: `actorCanModify`, `actorCanDelete`

### 프론트엔드 (TypeScript)
- **컴포넌트**: 함수형 컴포넌트 + React Compiler
- **스타일**: Tailwind CSS + cn() 유틸리티
- **API 호출**: openapi-fetch + 자동 생성 타입 (schema.d.ts)

## 원본 저장소 참조

- 원본 경로: `/Users/jangka512/IdeaProjects/slog_2025_04`
- 패턴 참조 시 원본 코드를 확인하여 일관성 유지

## API 엔드포인트 패턴

- 글: `/post/api/v1/posts`
- 댓글: `/post/api/v1/posts/{postId}/comments`
- 파일: `/post/api/v1/posts/{postId}/genFiles`
- 회원: `/member/api/v1/members`
