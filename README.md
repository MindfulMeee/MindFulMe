# TDD 기반 사용자 관리 시스템

## 프로젝트 소개
이 프로젝트는 테스트 주도 개발(TDD) 방식을 적용한 사용자 관리 시스템입니다. TDD의 원칙을 철저히 따르며, 각 기능을 구현하기 전에 테스트 코드를 먼저 작성하는 방식으로 개발되었습니다.

## TDD 개발 과정
이 프로젝트는 다음과 같은 TDD 사이클을 따라 개발되었습니다:

1. **Red**: 실패하는 테스트 작성
2. **Green**: 테스트를 통과하는 최소한의 코드 구현
3. **Refactor**: 코드 개선 및 중복 제거

## 주요 기능
- 사용자 등록 및 관리
- 사용자 인증 및 권한 관리
- 프로필 업데이트
- 사용자 조회 및 검색

## 기술 스택
- Backend: Java 17, Spring Boot 
- Database: MySQL
- Testing: JUnit 5, Mockito
- Build Tool: Gradle

## 테스트 설계 및 구현

### 테스트 계층
- **단위 테스트**: 개별 클래스/메소드 테스트
- **통합 테스트**: 컴포넌트 간 상호작용 테스트
- **E2E 테스트**: 사용자 시나리오 기반 테스트

### 테스트 시나리오 예시

| 테스트 ID | 목적 | 선행 조건 | 테스트 데이터 | 테스트 단계 | 예상 결과 |
|-----------|------|-----------|--------------|------------|-----------|
| TS-001 | 유효한 정보로 사용자 등록 검증 | DB 접근 가능 | 사용자명="testuser"<br>비밀번호="password123"<br>이메일="test@example.com" | **준비**: UserService 인스턴스 생성, 테스트용 User 객체 생성<br>**실행**: userService.register(user) 호출<br>**검증**: ID 할당 확인, 입력값 일치 확인 | 사용자 등록 성공, ID 할당됨 |
| TS-002 | 중복 사용자명 등록 실패 | 기존 사용자 존재 | 기존 사용자명="testuser"<br>신규 사용자="testuser" | **준비**: 기존 사용자 등록<br>**실행**: 동일 사용자명으로 등록 시도<br>**검증**: 예외 발생 확인 | DuplicateUserException 발생 |

## 프로젝트 구조
```
src/
├── main/java/
│   └── com/myproject/usermanagement/
│       ├── domain/       # 도메인 모델
│       ├── repository/   # 데이터 접근 계층
│       ├── service/      # 비즈니스 로직
│       ├── controller/   # API 엔드포인트
│       ├── dto/          # 데이터 전송 객체
│       └── exception/    # 예외 처리
├── test/java/
│   └── com/myproject/usermanagement/
│       ├── unit/         # 단위 테스트
│       ├── integration/  # 통합 테스트
│       └── e2e/          # E2E 테스트
└── resources/
```

## 테스트 커버리지
- 라인 커버리지: 85% 이상 유지
- 브랜치 커버리지: 80% 이상 유지
- 메소드 커버리지: 90% 이상 유지

## 개발 과정 기록

### 1주차: 초기 설정 및 사용자 등록 기능
- 프로젝트 셋업 및 기본 구조 설계
- 사용자 등록 테스트 케이스 작성
- 사용자 모델 및 레포지토리 구현
- 사용자 서비스 레이어 구현

### 2주차: 사용자 인증 및 권한 관리
- 인증 관련 테스트 케이스 작성
- 로그인 기능 구현
- 권한 관리 시스템 테스트 및 구현

## TDD 적용 사례

### 예시: 사용자 등록 기능

#### 1. 먼저 테스트 작성
```java
@Test
void 사용자_등록_성공() {
    // Given
    UserService userService = new UserService(userRepository);
    User user = new User("testuser", "password123", "test@example.com");
    
    // When
    User savedUser = userService.register(user);
    
    // Then
    assertNotNull(savedUser.getId());
    assertEquals("testuser", savedUser.getUsername());
}
```

#### 2. 실패하는 테스트 확인
- 테스트 실행 시 UserService 클래스가 없어서 실패

#### 3. 최소한의 구현
```java
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User register(User user) {
        // ID 생성 로직
        user.setId(UUID.randomUUID().toString());
        return user;
    }
}
```

#### 4. 테스트 통과 확인
- 테스트 실행 후 통과 확인

#### 5. 리팩토링
```java
public User register(User user) {
    validateUser(user);
    user.setId(UUID.randomUUID().toString());
    return userRepository.save(user);
}

private void validateUser(User user) {
    // 유효성 검증 로직
}
```

## 실행 방법
```bash
# 저장소 클론
git clone https://github.com/your-username/tdd-user-management.git

# 디렉토리 이동
cd tdd-user-management

# 테스트 실행
./gradlew test

# 애플리케이션 실행
./gradlew bootRun
```

## 배운 점 및 개선 사항
- TDD를 통해 설계의 명확성과 코드 품질 향상
- 테스트 작성이 설계 사고를 돕는 과정
- 향후 모킹(Mocking) 기법 개선 필요
- 테스트 실행 속도 최적화 필요

## 라이센스
MIT License
