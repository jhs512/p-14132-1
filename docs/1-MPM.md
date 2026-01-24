# MPM (Mono Project MSA)

## 개요

MPM은 **하나의 프로젝트(모노리스)에서 MSA의 장점을 취하는 아키텍처**다.

물리적으로는 단일 프로젝트지만, 논리적으로는 Bounded Context 단위로 철저히 분리하여 나중에 필요할 때 MSA로 쉽게 전환할 수 있는 구조를 유지한다.

## 핵심 철학

### 1. Bounded Context 분리

```
boundedContexts/
├── member/           # 회원 컨텍스트
├── post/             # 게시물 컨텍스트
├── home/             # 홈 컨텍스트
└── sharedContexts/   # 공유 컨텍스트 (여러 BC에서 참조하는 도메인)
```

- 각 Bounded Context는 독립적인 도메인 영역
- Context 간 직접 참조 금지 (sharedContexts를 통해서만 공유)

### 2. 레이어 구조 (in → app → domain)

각 Bounded Context는 다음 레이어로 구성된다:

```
member/
├── in/       # 인바운드 (Controller, EventListener, InitData)
├── app/      # 애플리케이션 (Facade, Service)
├── config/   # 설정 (Spring Config, Security Config)
├── domain/   # 도메인 (Entity, Value Object)
├── dto/      # DTO (Request/Response)
├── event/    # 도메인 이벤트
└── out/      # 아웃바운드 (Repository)
```

#### in (인바운드)

- 외부 요청의 진입점 (REST Controller, Event Listener, InitData 등)
- **오직 Facade만 사용 가능**
- Service, Repository, Domain 직접 접근 금지

#### app (애플리케이션)

- Facade: in 레이어에 노출되는 유일한 인터페이스
- Service: 내부 비즈니스 로직 (Facade에서만 호출)

#### config (설정)

- Spring Configuration 클래스
- SecurityConfig (해당 BC의 URL 권한 설정)
- AppConfig (해당 BC 전용 Bean 설정)

#### domain (도메인)

- Entity, Value Object, Domain Event
- 비즈니스 규칙 캡슐화

#### dto (데이터 전송 객체)

- Request/Response DTO
- 해당 BC 전용 DTO

#### event (도메인 이벤트)

- 다른 BC에 발행하는 도메인 이벤트
- ApplicationEventPublisher를 통해 발행

#### out (아웃바운드)

- Repository, 외부 시스템 연동
- app 레이어에서만 접근

### 3. sharedContexts 모듈

여러 Bounded Context에서 공통으로 참조해야 하는 도메인을 담는 특수한 컨텍스트:

```
boundedContexts/sharedContexts/
└── member/
    ├── app/      # ActorFacade, AuthTokenService
    ├── domain/   # Member, MemberAttr, MemberProxy
    ├── dto/      # AccessTokenPayload
    └── out/      # MemberRepository, MemberAttrRepository
```

#### sharedContexts 사용 원칙

- **읽기 전용 참조**: 다른 BC에서는 sharedContexts의 도메인을 읽기만 함
- **수정은 소유 BC에서**: Member 수정은 member BC의 MemberFacade를 통해서만
- **최소한의 노출**: 정말 공유가 필요한 것만 sharedContexts에 배치

### 4. Context 간 통신

#### 동기 통신

- RestAPIClient를 통한 호출

#### 비동기 통신

- 각 BC의 event 패키지를 통한 이벤트 발행/구독
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
- 모든 레이어 → `sharedContexts`
- 모든 레이어 → `global`
- 모든 레이어 → `standard`

### 금지

- `in` → `domain` (직접 접근)
- `in` → `out` (직접 접근)
- `in` → `app`의 Service (Facade만 허용)
- Context A → Context B (sharedContexts 제외)

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
├── config/
├── domain/
├── dto/
├── out/
└── subContexts/
    └── memberLog/
        ├── in/
        ├── app/
        ├── domain/
        └── out/
```

subContext도 동일한 레이어 규칙을 따른다.

## global vs standard

### global

- 프로젝트 전반에 걸친 인프라/설정
- Spring Security, Exception Handler, Rq, AppConfig 등
- 프레임워크/인프라 의존적

### standard

- 순수 유틸리티, 확장 함수, 공용 DTO
- 프레임워크 비의존적
- 어디서든 사용 가능
