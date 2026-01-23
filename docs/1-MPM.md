# MPM (Mono Project MSA)

## 개요

MPM은 **하나의 프로젝트(모노리스)에서 MSA의 장점을 취하는 아키텍처**다.

물리적으로는 단일 프로젝트지만, 논리적으로는 Bounded Context 단위로 철저히 분리하여 나중에 필요할 때 MSA로 쉽게 전환할 수 있는 구조를 유지한다.

## 핵심 철학

### 1. Bounded Context 분리

```
boundedContexts/
├── member/          # 회원 컨텍스트
├── post/            # 게시물 컨텍스트
└── home/            # 홈 컨텍스트
```

- 각 Bounded Context는 독립적인 도메인 영역
- Context 간 직접 참조 금지

### 2. 레이어 구조 (in → app → domain)

각 Bounded Context는 다음 레이어로 구성된다:

```
member/
├── in/      # 인바운드 (Controller, EventListener)
├── app/     # 애플리케이션 (Facade, Service)
├── domain/  # 도메인 (Entity, Value Object)
└── out/     # 아웃바운드 (Repository)
```

#### in (인바운드)

- 외부 요청의 진입점 (REST Controller, Event Listener 등)
- **오직 Facade만 사용 가능**
- Service, Repository, Domain 직접 접근 금지

#### app (애플리케이션)

- Facade: in 레이어에 노출되는 유일한 인터페이스
- Service: 내부 비즈니스 로직 (Facade에서만 호출)

#### domain (도메인)

- Entity, Value Object, Domain Event
- 비즈니스 규칙 캡슐화

#### out (아웃바운드)

- Repository, 외부 시스템 연동
- app 레이어에서만 접근

### 3. shared 모듈

```
shared/
├── member/
│   ├── domain/   # 공유 도메인 (BaseMember)
│   └── dto/      # 공유 DTO
└── post/
    ├── dto/      # 공유 DTO
    └── event/    # 도메인 이벤트
```

### 4. Context 간 통신

#### 동기 통신

- RestAPIClient를 통한 호출

#### 비동기 통신

- shared의 Event를 통한 이벤트 발행/구독
- `ApplicationEventPublisher` 활용

```kotlin
// 이벤트 발행 (post 컨텍스트)
publisher.publishEvent(
    PostCommentWrittenEvent(postCommentDto, postDto, actorDto)
)

// 이벤트 구독 (member 컨텍스트)
@TransactionalEventListener
fun handle(event: PostCommentWrittenEvent) {
    memberLogFacade.save(event)
}
```

## 의존성 규칙

```
in → app → domain
       ↓
      out
```

### 허용

- `in` → `app` (Facade만)
- `app` → `domain`
- `app` → `out`
- 모든 레이어 → `shared`
- 모든 레이어 → `global`
- 모든 레이어 → `standard`

### 금지

- `in` → `domain` (직접 접근)
- `in` → `out` (직접 접근)
- `in` → `app`의 Service (Facade만 허용)
- Context A → Context B

## 장점

1. **점진적 MSA 전환**: 필요시 Bounded Context 단위로 분리 가능
2. **명확한 경계**: 레이어 규칙으로 의존성 관리 용이
3. **단순한 배포**: 모노리스의 배포 편의성 유지
4. **트랜잭션 관리**: 단일 DB로 트랜잭션 처리 간편
5. **빠른 개발**: 네트워크 호출 없이 빠른 개발/테스트

## subContexts

큰 Bounded Context 내에 작은 하위 컨텍스트를 둘 수 있다:

```
member/
├── in/
├── app/
├── domain/
├── out/
└── subContexts/
    └── memberLog/
        ├── in/
        ├── app/
        ├── domain/
        └── out/
```

subContext도 동일한 레이어 규칙을 따른다.
