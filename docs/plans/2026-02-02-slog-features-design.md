# SLOG 기능 구현 설계 문서

## 1. 개요

### 1.1 목적
- 참조 프로젝트 [SLOG](https://github.com/jhs512/slog_2025_04)의 기능을 현재 프로젝트(MPM 아키텍처)에 구현한다.
- 참조 프로젝트 경로 : /Users/jangka512/IdeaProjects/slog_2025_04
  - 여기를 자주 참고해서 만들어줘.
- 이 프로젝트는 학생들에게 제공되는 샘플이라서 아름답고 쉬워야 해요.

### 1.2 참조 프로젝트 기술 스택
| 구분 | SLOG |
|------|------|
| Backend | Kotlin 1.9.25, Spring Boot 3.4.4 |
| Frontend | Next.js 16.0.10, React 19.2.3 |
| Editor | TOAST UI Editor 3.2.3 |
| Presentation | Marp 4.2.0 |

### 1.3 현재 프로젝트 기술 스택
| 구분 | 현재 |
|------|------|
| Backend | Kotlin 2.2.21, Spring Boot 4.0.2 |
| Frontend | Next.js 16.1.2, React 19.2.3 |
| Architecture | MPM (Mono Project MSA) |

---

## 2. 구현할 기능 목록

### 2.1 핵심 기능

| # | 기능 | 설명 | 우선순위 |
|---|------|------|----------|
| 1 | 파일 관리 시스템 | 첨부파일/썸네일 업로드, 다운로드, 관리 | 높음 |
| 2 | 게시물 상태 관리 | published/listed 플래그, 임시저장 | 높음 |
| 3 | 프레젠테이션 모드 | Marp 기반 PPT 변환 | 낮음 |
| 4 | Raw 콘텐츠 뷰 | 소스 코드 미니멀 뷰 | 낮음 |

---

## 3. 아키텍처 설계

### 3.1 MPM 아키텍처 적용

현재 아키텍처 원칙을 유지하면서 새 기능 추가:

```
boundedContexts/
├── member/              # 기존 유지
├── post/                # 확장 (파일, 상태 관리)
│   ├── in/
│   ├── app/
│   ├── config/
│   ├── domain/
│   │   ├── shared/      # Post, PostComment (기존)
│   │   └── genFile/     # PostGenFile (신규)
│   ├── dto/
│   ├── event/
│   ├── out/
│   └── subContexts/
│       └── genFile/     # 파일 관리 서브컨텍스트
├── home/                # 기존 유지
└── sharedContexts/      # 기존 유지
```

### 3.2 새로운 도메인 모델

#### 3.2.1 PostGenFile (게시물 첨부파일)

```kotlin
@Entity
class PostGenFile(
    @field:ManyToOne(fetch = LAZY)
    val post: Post,

    @field:Enumerated(STRING)
    val typeCode: TypeCode,      // ATTACHMENT, THUMBNAIL

    val fileNo: Int,              // 파일 번호 (순서)
    val originalFileName: String, // 원본 파일명
    val fileName: String,         // 저장 파일명 (UUID)
    val fileExt: String,          // 확장자
    val fileExtTypeCode: String,  // 파일 타입 (img, video, etc.)
    val fileExtType2Code: String, // 세부 타입 (jpg, png, etc.)
    val fileDateDir: String,      // 저장 디렉토리 (yyyy_MM_dd)
    val fileSize: Int,            // 파일 크기
    val metadata: String = "",    // 메타데이터 (JSON)
) : BaseTime() {  // BaseTime 상속 (createdAt, modifiedAt: Instant)

    enum class TypeCode {
        ATTACHMENT,  // 일반 첨부파일
        THUMBNAIL    // 썸네일 이미지
    }

    // 계산 속성
    val filePath: String get() = "$fileDateDir/$fileName"
    val publicUrl: String get() = "/gen/$filePath"
    val downloadUrl: String get() = "/post/${post.id}/genFile/download/$id/$originalFileName"
}
```

#### 3.2.2 Post 확장 (상태 관리)

```kotlin
@Entity
class Post(
    @field:ManyToOne(fetch = LAZY)
    val author: Member,
    var title: String,
    content: String,

    // 신규 필드
    var published: Boolean = false,  // 공개 여부
    var listed: Boolean = false,     // 목록 노출 여부
) : BaseTime() {
    // 기존 필드 유지
    @OneToOne(fetch = LAZY, cascade = [PERSIST, REMOVE])
    var body: PostBody = PostBody(content)

    @OneToMany(mappedBy = "post", cascade = [PERSIST, REMOVE], orphanRemoval = true)
    val comments: MutableList<PostComment> = mutableListOf()

    // 신규: 파일 관계
    @OneToMany(mappedBy = "post", cascade = [PERSIST, REMOVE], orphanRemoval = true)
    val genFiles: MutableList<PostGenFile> = mutableListOf()

    @field:ManyToOne(fetch = LAZY)
    var thumbnailGenFile: PostGenFile? = null

    // 상태 확인 속성
    val isPublished: Boolean get() = published
    val isListed: Boolean get() = listed
    val isTemp: Boolean get() = !published
    val isPrivate: Boolean get() = published && !listed

    // 파일 관리 메서드
    fun addGenFile(genFile: PostGenFile) { genFiles.add(genFile) }
    fun findGenFile(typeCode: TypeCode, fileNo: Int): PostGenFile? =
        genFiles.find { it.typeCode == typeCode && it.fileNo == fileNo }
    fun deleteGenFile(genFile: PostGenFile): Boolean = genFiles.remove(genFile)

    // 권한 확인 메서드 확장
    fun canRead(actor: Member?): Boolean {
        if (!published) return actor?.id == author.id || actor?.isAdmin == true
        return true
    }

    fun checkActorCanRead(actor: Member?) {
        if (!canRead(actor)) throw BusinessException("403-3", "${id}번 글 조회권한이 없습니다.")
    }
}
```

---

## 4. API 설계

### 4.1 파일 관리 API

```
# 파일 목록 조회
GET /post/api/v1/posts/{postId}/genFiles

# 파일 상세 조회
GET /post/api/v1/posts/{postId}/genFiles/{id}

# 파일 업로드
POST /post/api/v1/posts/{postId}/genFiles/{typeCode}
Content-Type: multipart/form-data

# 파일 수정 (교체)
PUT /post/api/v1/posts/{postId}/genFiles/{typeCode}/{fileNo}
Content-Type: multipart/form-data

# 파일 삭제
DELETE /post/api/v1/posts/{postId}/genFiles/{typeCode}/{fileNo}

# 파일 다운로드 (공개)
GET /post/{postId}/genFile/download/{id}/{fileName}

# 정적 파일 서빙
GET /gen/{path}
```

### 4.2 게시물 상태 관리 API

```
# 임시저장 생성/조회
POST /post/api/v1/posts/temp

# 게시물 생성 (published/listed 포함)
POST /post/api/v1/posts
{
  "title": "...",
  "content": "...",
  "published": true,
  "listed": true
}

# 게시물 수정 (상태 포함)
PUT /post/api/v1/posts/{id}
{
  "title": "...",
  "content": "...",
  "published": true,
  "listed": false
}

# 내 게시물 목록 (임시저장 포함)
GET /post/api/v1/posts/mine?page=1&pageSize=10
```

### 4.3 프레젠테이션/Raw API

```
# PPT 데이터 조회 (프론트엔드에서 Marp 렌더링)
GET /post/api/v1/posts/{id}/ppt

# Raw 콘텐츠 조회
GET /post/api/v1/posts/{id}/raw
```

---

## 5. 패키지 구조 설계

### 5.1 Backend 구조

```
boundedContexts/post/
├── in/
│   ├── web/
│   │   ├── PostApiController.kt          # 기존 확장
│   │   └── PostGenFileApiController.kt   # 신규
│   └── initData/
│       └── PostInitData.kt               # 초기 데이터
├── app/
│   ├── PostFacade.kt                     # 기존 확장
│   └── PostGenFileFacade.kt              # 신규
├── config/
│   └── PostSecurityConfig.kt             # 권한 설정 확장
├── domain/
│   └── shared/
│       ├── Post.kt                       # 확장
│       ├── PostComment.kt                # 기존 유지
│       └── PostGenFile.kt                # 신규
├── dto/
│   ├── PostDto.kt                        # 확장
│   ├── PostWithContentDto.kt             # 확장
│   └── PostGenFileDto.kt                 # 신규
├── event/
│   └── PostGenFileEvent.kt               # 신규
└── out/
    ├── PostRepository.kt                 # 확장
    └── PostGenFileRepository.kt          # 신규
```

### 5.2 Frontend 구조

```
src/
├── app/
│   ├── posts/
│   │   ├── [id]/
│   │   │   ├── page.tsx                  # 상세 (확장)
│   │   │   ├── edit/
│   │   │   │   └── page.tsx              # 수정 (확장)
│   │   │   ├── ppt/
│   │   │   │   └── page.tsx              # PPT 모드 (신규)
│   │   │   └── raw/
│   │   │       └── page.tsx              # Raw 모드 (신규)
│   │   ├── mine/
│   │   │   └── page.tsx                  # 내 게시물 (신규)
│   │   └── write/
│   │       └── page.tsx                  # 작성 (확장)
│   └── adm/
│       └── posts/                        # 관리자 게시물 관리
├── domain/
│   └── post/
│       ├── hooks/
│       │   ├── usePost.ts                # 기존 확장
│       │   └── usePostGenFile.ts         # 신규
│       └── components/
│           ├── PostEditor.tsx            # 확장 (파일 업로드)
│           ├── PostGenFileList.tsx       # 신규
│           └── MarpPresenter.tsx         # 신규
└── components/
    └── ui/                               # shadcn/ui 컴포넌트
```

---

## 6. 기술 선택

### 6.1 파일 저장

| 옵션 | 장점 | 단점 | 선택 |
|------|------|------|------|
| 로컬 파일시스템 | 단순, 빠름 | 확장성 제한 | **개발/초기** |
| S3 | 확장성, 안정성 | 복잡도 증가 | 프로덕션 |

**결정**: 로컬 파일시스템으로 시작, 인터페이스 추상화로 S3 전환 용이하게

### 6.2 이미지 처리

```kotlin
// build.gradle.kts 의존성
implementation("org.apache.tika:tika-core:3.1.0")          // 파일 타입 감지
implementation("com.twelvemonkeys.imageio:imageio-webp:3.12.0") // WebP 지원
```

### 6.3 프레젠테이션

- Backend: 마크다운 콘텐츠 제공
- Frontend: Marp (npm: `@marp-team/marp-core`) 렌더링

---

## 7. 데이터베이스 스키마

### 7.1 post_gen_file 테이블

```sql
CREATE TABLE post_gen_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    type_code VARCHAR(20) NOT NULL,      -- ATTACHMENT, THUMBNAIL
    file_no INT NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_name VARCHAR(100) NOT NULL,      -- UUID
    file_ext VARCHAR(20) NOT NULL,
    file_ext_type_code VARCHAR(20),
    file_ext_type2_code VARCHAR(20),
    file_date_dir VARCHAR(20) NOT NULL,   -- yyyy_MM_dd
    file_size INT NOT NULL,
    metadata TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,  -- Instant
    modified_at TIMESTAMP WITH TIME ZONE NOT NULL, -- Instant

    CONSTRAINT fk_post_gen_file_post FOREIGN KEY (post_id) REFERENCES post(id),
    UNIQUE KEY uk_post_gen_file (post_id, type_code, file_no)
);
```

### 7.2 post 테이블 확장

```sql
ALTER TABLE post ADD COLUMN published BOOLEAN DEFAULT FALSE;
ALTER TABLE post ADD COLUMN listed BOOLEAN DEFAULT FALSE;
ALTER TABLE post ADD COLUMN thumbnail_gen_file_id BIGINT;

ALTER TABLE post ADD CONSTRAINT fk_post_thumbnail
    FOREIGN KEY (thumbnail_gen_file_id) REFERENCES post_gen_file(id);
```

---

## 8. 보안 고려사항

### 8.1 파일 업로드

- 파일 크기 제한: 10MB (설정 가능)
- 허용 확장자: 화이트리스트 방식
- 파일명 UUID 변환으로 경로 조작 방지
- MIME 타입 검증 (Apache Tika)

### 8.2 게시물 접근 제어

```kotlin
// 접근 권한 매트릭스
| 상태              | 작성자 | 관리자 | 일반 사용자 |
|-------------------|--------|--------|-------------|
| temp (미공개)     | O      | O      | X           |
| published=true    | O      | O      | O           |
| listed=false      | O      | O      | X (링크로만)|
```

---

## 9. 구현 단계

### Phase 0: 기존 코드 리팩토링 ✅ 완료
1. ~~PostBody 엔티티 제거~~ → **사용자 요청으로 유지**
2. ~~DTO 상속 구조 도입~~ → **data class 제약으로 스킵**
3. Facade 권한 검증 통합 (부분 적용)
4. @Transactional 명시화

### Phase 1: 게시물 상태 관리 ✅ 완료
1. ✅ Post 엔티티에 published/listed 필드 추가
2. ✅ 임시저장 API 구현 (POST /post/api/v1/posts/temp)
3. ✅ 내 게시물 목록 API 구현 (GET /post/api/v1/posts/mine)
4. ✅ 읽기 권한 확인 로직 추가 (checkActorCanRead)
5. ✅ 테스트 케이스 추가

### Phase 2: 파일 관리 시스템 ✅ 완료
1. ✅ PostGenFile 엔티티 생성
2. ✅ 파일 업로드/다운로드 API 구현 (ApiV1PostGenFileController)
3. ✅ 정적 파일 서빙 설정 (WebConfig, /gen/** 경로)
4. ⏳ 프론트엔드 파일 관리 UI (추후 구현)

### Phase 3: 프레젠테이션/Raw 모드 ✅ 완료
1. ✅ PPT 데이터 API 구현 (GET /post/api/v1/posts/{id}/ppt)
2. ✅ Raw 콘텐츠 API 구현 (GET /post/api/v1/posts/{id}/raw)
3. ⏳ Marp 프론트엔드 통합 (추후 구현)

---

## 10. 기존 코드 리팩토링

새 기능 구현과 함께 기존 코드의 문제점을 개선한다.

### 10.1 개선할 문제점

| 우선순위 | 문제점 | 개선 방향 |
|---------|--------|----------|
| 🔴 High | PostBody 불필요한 엔티티화 | Post에 content를 `@Lob`으로 직접 포함 |
| 🔴 High | 권한 검증 위치 비일관성 | Facade에서 통합 처리 |
| 🟠 Medium | DTO 코드 중복 | 상속 구조 도입 |
| 🟠 Medium | 트랜잭션 경계 불명확 | Facade에 `@Transactional` 명시 |

### 10.2 PostBody 제거

**현재 (문제):**
```kotlin
@Entity
class Post(...) {
    @OneToOne(fetch = LAZY, cascade = [PERSIST, REMOVE])
    var body: PostBody = PostBody(content)  // 불필요한 JOIN 발생
}
```

**개선 후:**
```kotlin
@Entity
class Post(
    @field:ManyToOne(fetch = LAZY)
    val author: Member,
    var title: String,
    @field:Lob
    var content: String,
    var published: Boolean = false,
    var listed: Boolean = false,
) : BaseTime()
```

### 10.3 DTO 상속 구조

**현재 (중복):**
```kotlin
data class PostDto(id, createdAt, modifiedAt, authorId, authorName, title)
data class PostWithContentDto(id, createdAt, modifiedAt, authorId, authorName, title, content)  // 중복
```

**개선 후:**
```kotlin
open class PostDto(
    val id: Int,
    val createdAt: Instant,
    val modifiedAt: Instant,
    val authorId: Int,
    val authorName: String,
    val authorProfileImgUrl: String,
    val title: String,
    val published: Boolean,
    val listed: Boolean,
)

class PostWithContentDto(
    id: Int, createdAt: Instant, modifiedAt: Instant,
    authorId: Int, authorName: String, authorProfileImgUrl: String,
    title: String, published: Boolean, listed: Boolean,
    val content: String,
) : PostDto(id, createdAt, modifiedAt, authorId, authorName, authorProfileImgUrl, title, published, listed)
```

### 10.4 권한 검증 통합

**현재 (분산):**
```kotlin
// Controller에서
post.checkActorCanDelete(actor)
postFacade.delete(post)
```

**개선 후:**
```kotlin
// Facade에서 통합
@Transactional
fun delete(actor: Member, post: Post) {
    post.checkActorCanDelete(actor)
    post.author.decrementPostsCount()
    postRepository.delete(post)
}

// Controller는 단순 호출
postFacade.delete(actor, post)
```

---

## 11. 테스트 계획

### 11.1 단위 테스트
- PostGenFile 도메인 로직
- 파일 타입 감지
- 접근 권한 확인

### 11.2 통합 테스트
- 파일 업로드/다운로드 플로우
- 게시물 상태 변경 플로우
- 권한 기반 접근 제어

### 11.3 E2E 테스트
- 게시물 작성 → 파일 첨부 → 발행 플로우
- PPT 모드 렌더링

---

## 12. 참고 자료

- [SLOG GitHub Repository](https://github.com/jhs512/slog_2025_04)
- [현재 프로젝트 MPM 아키텍처](./1-MPM.md)
- [Spring Boot 4.0 Documentation](https://docs.spring.io/spring-boot/)
- [Marp Documentation](https://marp.app/)
