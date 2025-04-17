안녕하세요! USER 도메인을 TDD 방식으로 설계해보도록 도와드리겠습니다. 업로드하신 문서들을 보니 DDD 아키텍처와 TDD 원칙을 따라 개발하고 계시군요. 먼저 USER 도메인의 설계를 단계별로 진행해보겠습니다.

## 1. USER 도메인 분석

먼저 USER 도메인의 핵심 개념과, 이 도메인이 가져야 할 책임을 정의해보겠습니다.

### 핵심 도메인 개념
- **User(사용자)**: 시스템의 주 사용자 엔티티
- **Profile(프로필)**: 사용자의 상세 정보
- **Authority(권한)**: 사용자가 가진 권한 정보
- **Authentication(인증)**: 사용자 로그인 관련 정보

### 주요 기능
- 사용자 등록 및 정보 관리
- 사용자 인증 (로그인/로그아웃)
- 프로필 정보 업데이트
- 권한 부여 및 관리
- 사용자 조회 및 검색

## 2. TDD 기반 구현 계획

TDD 방식으로 각 기능을 구현할 때는 테스트 코드를 먼저 작성하고(Red), 이를 통과하는 코드를 작성(Green), 그 후 리팩토링(Refactor)하는 과정을 거치게 됩니다.

## 3. USER 도메인 설계

DDD 아키텍처에 맞게 USER 도메인을 다음과 같이 설계해보겠습니다:

### 1) Domain Layer (도메인 계층)

```
domain
└── user
    ├── entity
    │   ├── User.java                # 사용자 엔티티
    │   └── UserProfile.java         # 사용자 프로필
    ├── vo
    │   ├── Email.java               # 이메일 값 객체
    │   ├── Password.java            # 비밀번호 값 객체
    │   └── Username.java            # 사용자명 값 객체
    ├── repository
    │   └── UserRepository.java      # 사용자 레포지토리 인터페이스
    ├── service
    │   └── UserDomainService.java   # 도메인 서비스 인터페이스
    └── exception
        ├── DuplicateUserException.java
        ├── UserNotFoundException.java
        └── InvalidUserException.java
```

### 2) Application Layer (응용 계층)

```
application
└── user
    ├── service
    │   ├── UserService.java             # 사용자 서비스 인터페이스
    │   └── UserServiceImpl.java         # 사용자 서비스 구현
    ├── dto
    │   ├── UserRegistrationDto.java     # 사용자 등록 DTO
    │   ├── UserProfileDto.java          # 사용자 프로필 DTO
    │   └── UserResponseDto.java         # 사용자 응답 DTO
    ├── validator
    │   └── UserValidator.java           # 사용자 검증 클래스
    └── mapper
        └── UserMapper.java              # 엔티티-DTO 매핑 클래스
```

### 3) Infrastructure Layer (인프라 계층)

```
infrastructure
└── user
    ├── repository
    │   └── JpaUserRepositoryImpl.java   # JPA 사용자 레포지토리 구현
    ├── security
    │   ├── UserDetailsServiceImpl.java  # Spring Security UserDetails 구현
    │   └── PasswordEncoderImpl.java     # 비밀번호 암호화 구현
    └── persistence
        └── UserEntity.java              # JPA Entity 클래스
```

### 4) Web Layer (웹 계층)

```
web
└── user
    ├── controller
    │   └── UserController.java          # 사용자 컨트롤러
    ├── request
    │   ├── UserRegistrationRequest.java # 사용자 등록 요청
    │   └── UserUpdateRequest.java       # 사용자 정보 수정 요청
    ├── response
    │   ├── UserResponse.java            # 사용자 응답
    │   └── UserListResponse.java        # 사용자 목록 응답
    └── advice
        └── UserControllerAdvice.java    # 예외 처리
```

## 4. TDD 기반 구현 예시

업로드하신 CODE_CONVENTION.md에 나온 TDD 원칙과 코딩 규칙을 따라 USER 도메인의 핵심 클래스들을 구현해보겠습니다. 먼저 값 객체부터 시작하여 엔티티, 서비스로 순차적으로 진행합니다.

### 1) Email 값 객체 테스트 (Red 단계)

```java
// domain/user/vo/EmailTest.java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("유효한 이메일 형식이면 Email 객체가 생성된다")
    void createEmailWithValidFormat() {
        // given
        String validEmailStr = "user@example.com";
        
        // when
        Email email = new Email(validEmailStr);
        
        // then
        assertThat(email.getValue()).isEqualTo(validEmailStr);
    }
    
    @Test
    @DisplayName("이메일 형식이 유효하지 않으면 예외가 발생한다")
    void throwExceptionForInvalidEmailFormat() {
        // given
        String invalidEmailStr = "invalid-email";
        
        // when & then
        assertThatThrownBy(() -> new Email(invalidEmailStr))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("잘못된 이메일 형식입니다");
    }
    
    @Test
    @DisplayName("이메일 값이 null이면 예외가 발생한다")
    void throwExceptionForNullEmail() {
        // given
        String nullEmail = null;
        
        // when & then
        assertThatThrownBy(() -> new Email(nullEmail))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이메일은 필수 값입니다");
    }
    
    @Test
    @DisplayName("동일한 값을 가진 Email 객체는 동등하다")
    void emailEquality() {
        // given
        Email email1 = new Email("same@example.com");
        Email email2 = new Email("same@example.com");
        
        // when & then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }
}
```

### 2) Email 값 객체 구현 (Green 단계)

```java
// domain/user/vo/Email.java
import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    
    private final String value;
    
    public Email(String value) {
        validate(value);
        this.value = value;
    }
    
    private void validate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("이메일은 필수 값입니다");
        }
        
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("잘못된 이메일 형식입니다");
        }
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
```

### 3) User 엔티티 테스트 (Red 단계)

```java
// domain/user/entity/UserTest.java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("유효한 정보로 User 객체가 생성된다")
    void createUserWithValidInfo() {
        // given
        Email email = new Email("user@example.com");
        Username username = new Username("testuser");
        Password password = new Password("Password123!");
        
        // when
        User user = new User(email, username, password);
        
        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.isActive()).isTrue(); // 기본값은 활성화 상태
    }
    
    @Test
    @DisplayName("사용자 정보 수정이 정상적으로 동작한다")
    void updateUserProfile() {
        // given
        User user = new User(
            new Email("user@example.com"),
            new Username("oldUsername"),
            new Password("Password123!")
        );
        Username newUsername = new Username("newUsername");
        
        // when
        user.updateUsername(newUsername);
        
        // then
        assertThat(user.getUsername()).isEqualTo(newUsername);
    }
    
    @Test
    @DisplayName("비밀번호 변경이 정상적으로 동작한다")
    void changePassword() {
        // given
        User user = new User(
            new Email("user@example.com"),
            new Username("username"),
            new Password("OldPassword123!")
        );
        Password newPassword = new Password("NewPassword123!");
        
        // when
        user.changePassword(newPassword);
        
        // then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }
    
    @Test
    @DisplayName("사용자 비활성화가 정상적으로 동작한다")
    void deactivateUser() {
        // given
        User user = new User(
            new Email("user@example.com"),
            new Username("username"),
            new Password("Password123!")
        );
        
        // when
        user.deactivate();
        
        // then
        assertThat(user.isActive()).isFalse();
    }
}
```

### 4) User 엔티티 구현 (Green 단계)

```java
// domain/user/entity/User.java
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User {
    private final UUID id;
    private final Email email;
    private Username username;
    private Password password;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public User(Email email, Username username, Password password) {
        this.id = UUID.randomUUID();
        this.email = Objects.requireNonNull(email, "이메일은 필수입니다");
        this.username = Objects.requireNonNull(username, "사용자명은 필수입니다");
        this.password = Objects.requireNonNull(password, "비밀번호는 필수입니다");
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateUsername(Username username) {
        this.username = Objects.requireNonNull(username, "사용자명은 필수입니다");
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changePassword(Password password) {
        this.password = Objects.requireNonNull(password, "비밀번호는 필수입니다");
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public UUID getId() {
        return id;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public Username getUsername() {
        return username;
    }
    
    public Password getPassword() {
        return password;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

### 5) UserRepository 인터페이스 정의

```java
// domain/user/repository/UserRepository.java
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(Email email);
    Optional<User> findByUsername(Username username);
    List<User> findAll();
    void delete(User user);
    boolean existsByEmail(Email email);
    boolean existsByUsername(Username username);
}
```

### 6) UserService 인터페이스와 구현 (Application Layer)

먼저 테스트를 작성하고(Red), 구현을 진행합니다(Green).

```java
// application/user/service/UserServiceTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
    }
    
    @Test
    @DisplayName("유효한 정보로 사용자 등록이 성공한다")
    void registerUserWithValidInfo() {
        // given
        UserRegistrationDto dto = new UserRegistrationDto(
            "user@example.com",
            "username",
            "Password123!"
        );
        
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        
        User mockUser = new User(
            new Email(dto.getEmail()),
            new Username(dto.getUsername()),
            new Password(dto.getPassword())
        );
        
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        
        // when
        UserResponseDto response = userService.registerUser(dto);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(dto.getEmail());
        assertThat(response.getUsername()).isEqualTo(dto.getUsername());
    }
    
    @Test
    @DisplayName("이미 존재하는 이메일로 사용자 등록 시 예외가 발생한다")
    void throwExceptionWhenRegisteringWithExistingEmail() {
        // given
        UserRegistrationDto dto = new UserRegistrationDto(
            "existing@example.com",
            "username",
            "Password123!"
        );
        
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);
        
        // when & then
        assertThatThrownBy(() -> userService.registerUser(dto))
            .isInstanceOf(DuplicateUserException.class)
            .hasMessageContaining("이미 존재하는 이메일입니다");
    }
    
    // 추가 테스트 케이스...
}
```

Service 인터페이스와 구현:

```java
// application/user/service/UserService.java
public interface UserService {
    UserResponseDto registerUser(UserRegistrationDto registrationDto);
    UserResponseDto getUserById(UUID id);
    UserResponseDto getUserByEmail(String email);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(UUID id, UserUpdateDto updateDto);
    void deleteUser(UUID id);
    void changePassword(UUID id, PasswordChangeDto passwordDto);
}

// application/user/service/UserServiceImpl.java
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        Email email = new Email(registrationDto.getEmail());
        Username username = new Username(registrationDto.getUsername());
        
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("이미 존재하는 이메일입니다: " + email.getValue());
        }
        
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException("이미 존재하는 사용자명입니다: " + username.getValue());
        }
        
        Password password = new Password(registrationDto.getPassword());
        User user = new User(email, username, password);
        
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }
    
    @Override
    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("ID에 해당하는 사용자를 찾을 수 없습니다: " + id));
        
        return mapToDto(user);
    }
    
    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(new Email(email))
            .orElseThrow(() -> new UserNotFoundException("이메일에 해당하는 사용자를 찾을 수 없습니다: " + email));
        
        return mapToDto(user);
    }
    
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public UserResponseDto updateUser(UUID id, UserUpdateDto updateDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("ID에 해당하는 사용자를 찾을 수 없습니다: " + id));
        
        if (updateDto.getUsername() != null) {
            Username newUsername = new Username(updateDto.getUsername());
            
            if (!user.getUsername().getValue().equals(newUsername.getValue()) && 
                userRepository.existsByUsername(newUsername)) {
                throw new DuplicateUserException("이미 존재하는 사용자명입니다: " + newUsername.getValue());
            }
            
            user.updateUsername(newUsername);
        }
        
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }
    
    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("ID에 해당하는 사용자를 찾을 수 없습니다: " + id));
        
        user.deactivate();
        userRepository.save(user);
    }
    
    @Override
    public void changePassword(UUID id, PasswordChangeDto passwordDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("ID에 해당하는 사용자를 찾을 수 없습니다: " + id));
        
        Password newPassword = new Password(passwordDto.getNewPassword());
        user.changePassword(newPassword);
        userRepository.save(user);
    }
    
    private UserResponseDto mapToDto(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getEmail().getValue(),
            user.getUsername().getValue(),
            user.isActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
```

### 7) UserController 구현 (Web Layer)

```java
// web/user/controller/UserController.java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRegistrationRequest request) {
        UserRegistrationDto dto = new UserRegistrationDto(
            request.getEmail(),
            request.getUsername(),
            request.getPassword()
        );
        
        UserResponseDto responseDto = userService.registerUser(dto);
        UserResponse response = mapToResponse(responseDto);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponseDto dto = userService.getUserById(id);
        UserResponse response = mapToResponse(dto);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers() {
        List<UserResponseDto> dtos = userService.getAllUsers();
        List<UserResponse> users = dtos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        
        UserListResponse response = new UserListResponse(users);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest request) {
        
        UserUpdateDto dto = new UserUpdateDto(request.getUsername());
        UserResponseDto responseDto = userService.updateUser(id, dto);
        UserResponse response = mapToResponse(responseDto);
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @RequestBody PasswordChangeRequest request) {
        
        PasswordChangeDto dto = new PasswordChangeDto(request.getNewPassword());
        userService.changePassword(id, dto);
        
        return ResponseEntity.noContent().build();
    }
    
    private UserResponse mapToResponse(UserResponseDto dto) {
        return new UserResponse(
            dto.getId(),
            dto.getEmail(),
            dto.getUsername(),
            dto.isActive(),
            dto.getCreatedAt(),
            dto.getUpdatedAt()
        );
    }
}
```

## 5. 코드 분석 및 설명

이 설계는 DDD와 TDD 원칙을 따라 구현되었습니다. 주요 포인트를 설명하겠습니다:

1. **값 객체(Value Object) 활용**:
    - Email, Username, Password는 도메인 개념을 나타내는 값 객체로 구현
    - 각 값 객체는 자체 유효성 검증 로직을 포함
    - 불변(immutable) 특성을 가짐

2. **엔티티(Entity) 설계**:
    - User는 고유 식별자(ID)를 가진 엔티티
    - 값 객체로 구성되어 타입 안전성과 도메인 규칙 보장
    - 불필요한 getter/setter 대신 명확한 행동(behavior)으로 상태 변경

3. **계층 분리**:
    - Domain: 핵심 비즈니스 로직과 규칙
    - Application: 유스케이스 구현 및 도메인 서비스 조직
    - Infrastructure: 외부 시스템과의 통합(데이터베이스 등)
    - Web: 외부와의 통신 인터페이스

4. **인터페이스와 구현 분리**:
    - 각 계층은 인터페이스를 통해 통신
    - 의존성 역전 원칙(DIP) 준수

5. **TDD 접근 방식**:
    - 각 기능에 대한 테스트 코드 먼저 작성(Red)
    - 테스트를 통과하는 최소한의 코드 구현(Green)
    - 코드 리팩토링 및 개선(Refactor)

이 설계는 USER 도메인의 견고한 기반을 제공하며, TDD 접근 방식을 통해 코드의 품질과 테스트 커버리지를 보장합니다. DDD의 원칙을 적용하여 비즈니스 개념과 규칙을 명확하게 표현하고, 도메인 전문가와의 의사소통을 용이하게 해줍니다.

추가적인 기능 또는 더 자세한 설명이 필요하시면 말씀해주세요!