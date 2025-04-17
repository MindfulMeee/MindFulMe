# 코드 컴벤션 가이드

## 해당 컴벤션은 해당 자료들을 참고해서 적용했습니다.
### 모두 지키지는 못하더라도, 최대한 지향하는 방식으로 적용합시다.
[우아한테크코스 클린코드](https://github.com/woowacourse/woowacourse-docs/blob/main/cleancode/pr_checklist.md)
[TDD원칙](https://github.com/2jigoo/BookStudy-StartTdd/blob/main/docs/02-TDD-%EC%8B%9C%EC%9E%91.md)

## 1. 단일 책임 원칙 (Single Responsibility Principle)
### 작은 메서드와 클래스
* **한 메서드는 한 가지 일만 수행해야 합니다**
  * 메서드가 여러 단계의 들여쓰기를 가진다면, 새로운 메서드로 추출하세요.
  * 메서드 이름은 그 기능을 명확히 표현해야 합니다.

```java
// 개선 전:
public void processOrder(Order order) {
    // 유효성 검사, 재고 확인, 결제 처리, 저장, 이메일 발송을 모두 한 메서드에서 처리
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("주문 항목이 없습니다.");
    }
    
    for (OrderItem item : order.getItems()) {
        if (inventoryService.getStock(item.getProductId()) < item.getQuantity()) {
            throw new OutOfStockException(item.getProductId());
        }
    }
    
    paymentService.process(order.getPaymentDetails());
    orderRepository.save(order);
    emailService.sendOrderConfirmation(order);
}

// 개선 후:
public void processOrder(Order order) {
    validateOrder(order);         // 각 책임을 별도 메서드로 분리
    checkInventory(order);
    processPayment(order);
    saveOrder(order);
    sendConfirmationEmail(order);
}

private void validateOrder(Order order) {
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("주문 항목이 없습니다.");
    }
}
```

* **클래스는 하나의 책임만 가져야 합니다**
  * 클래스의 인스턴스 변수는 3개 이하로 유지하세요(큰 클래스는 여러 책임을 가질 가능성이 높습니다).
  * 클래스가 여러 역할을 한다면, 분리를 고려하세요.

```java
// 개선 전:
public class UserService {
    // 사용자, 인증, 권한 관련 모든 책임을 한 클래스에 배치
    public User createUser() { /*...*/ }
    public Token login() { /*...*/ }
    public boolean hasPermission() { /*...*/ }
}

// 개선 후:
public class UserService {
    public User createUser() { /*...*/ }
}

public class AuthenticationService {
    public Token login() { /*...*/ }
}

public class AuthorizationService {
    public boolean hasPermission() { /*...*/ }
}
```

## 2. 개방-폐쇄 원칙 (Open-Closed Principle)
### 적절한 추상화와 캡슐화

* **객체의 상태를 노출하지 말고, 행동을 통해 상호작용하세요**
  * 핵심 도메인 객체에서는 getter/setter 사용을 지양합니다.
  * DTO(Data Transfer Object)에만 제한적으로 getter/setter를 허용합니다.

```java
// 개선 전:
public class Cart {
    private List<Item> items;
    
    public List<Item> getItems() {
        return items;  // 내부 컬렉션이 그대로 노출됨
    }
    
    public void setItems(List<Item> items) {
        this.items = items;
    }
}

// 클라이언트 코드에서:
cart.getItems().add(new Item());  // 캡슐화 위반!

// 개선 후:
public class Cart {
    private final List<Item> items = new ArrayList<>();
    
    public void addItem(Item item) {
        items.add(item);  // 행동을 통한 상호작용
    }
    
    public boolean contains(ItemId id) {
        return items.stream()
                .anyMatch(item -> item.isSameId(id));
    }
    
    public int totalPrice() {
        return items.stream()
                .mapToInt(Item::getPrice)
                .sum();
    }
}
```

## 3. 리스코프 치환 원칙 (Liskov Substitution Principle)
### 상속과 다형성

* **상속은 IS-A 관계가 명확할 때만 사용하세요**
  * 부모 클래스의 유효한 동작이 자식 클래스에서도 유효해야 합니다.
  * 상속보다 컴포지션을 우선적으로 고려하세요.

```java
// 개선 전:
public class Rectangle {
    protected int width;
    protected int height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // 정사각형의 특성 때문에 height도 변경 - LSP 위반
    }
    
    @Override
    public void setHeight(int height) {
        this.height = height;
        this.width = height;
    }
}

// 개선 후:
public interface Shape {
    int area();
}

public class Rectangle implements Shape {
    private int width;
    private int height;
    
    // 생성자로 초기화
    
    @Override
    public int area() {
        return width * height;
    }
}

public class Square implements Shape {
    private int side;
    
    // 생성자로 초기화
    
    @Override
    public int area() {
        return side * side;
    }
}
```

## 4. 인터페이스 분리 원칙 (Interface Segregation Principle)
### 작고 명확한 인터페이스

* **인터페이스는 클라이언트가 필요로 하는 메서드만 포함해야 합니다**
  * 범용 인터페이스보다 목적에 특화된 여러 인터페이스로 분리하세요.

```java
// 개선 전:
public interface Worker {
    void work();
    void eat();
    void sleep();
}

// 개선 후:
public interface Workable {
    void work();
}

public interface Eatable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public class HumanWorker implements Workable, Eatable, Sleepable {
    @Override public void work() { /*...*/ }
    @Override public void eat() { /*...*/ }
    @Override public void sleep() { /*...*/ }
}

public class RobotWorker implements Workable {
    @Override public void work() { /*...*/ }
    // 로봇은 eat()과 sleep()이 필요 없음
}
```

## 5. 의존성 역전 원칙 (Dependency Inversion Principle)
### 추상화에 의존하기

* **구체적인 구현이 아닌 추상화에 의존하세요**
  * 고수준 모듈과 저수준 모듈 모두 추상화에 의존해야 합니다.
  * 의존성 주입을 활용하여 결합도를 낮추세요.

```java
// 개선 전:
public class OrderService {
    private MySqlOrderRepository orderRepository = new MySqlOrderRepository();
    
    public void createOrder(Order order) {
        orderRepository.save(order);  // 구체적인 구현에 직접 의존
    }
}

// 개선 후:
public class OrderService {
    private final OrderRepository orderRepository;  // 인터페이스에 의존
    
    public OrderService(OrderRepository orderRepository) {  // 의존성 주입
        this.orderRepository = orderRepository;
    }
    
    public void createOrder(Order order) {
        orderRepository.save(order);
    }
}

public interface OrderRepository {
    void save(Order order);
}

public class MySqlOrderRepository implements OrderRepository {
    @Override
    public void save(Order order) {
        // MySQL 데이터베이스에 저장하는 구현
    }
}
```

## 6. 객체 설계 원칙
### 원시 타입 포장 및 값 객체 사용

* **원시값과 문자열을 적절한 객체로 포장하세요**
  * 값 객체(Value Object)는 도메인 의미를 명확히 합니다.
  * 유효성 검사와 관련 로직을 응집시킵니다.

```java
// 개선 전:
public class User {
    private String email;  // 그냥 문자열
    
    public boolean isValidEmail() {
        return email.contains("@");  // 유효성 검사 로직이 User 클래스에 있음
    }
}

// 개선 후:
public class User {
    private final Email email;  // 값 객체
}

public class Email {
    private final String value;
    
    public Email(String value) {
        validate(value);
        this.value = value;
    }
    
    private void validate(String value) {
        if (!value.contains("@")) {
            throw new IllegalArgumentException("잘못된 이메일 형식입니다");
        }
    }
}
```

### 일급 컬렉션 사용

* **컬렉션을 전용 클래스로 포장하세요**
  * 컬렉션과 관련된 동작과 검증을 응집시킵니다.

```java
// 개선 전:
public class Lottery {
    private List<LotteryNumber> numbers;
    
    public boolean isValid() {
        return numbers.size() == 6 && new HashSet<>(numbers).size() == 6;
    }
}

// 개선 후:
public class Lottery {
    private final LotteryNumbers numbers;
}

public class LotteryNumbers {
    private final List<LotteryNumber> values;
    
    public LotteryNumbers(List<LotteryNumber> values) {
        validateSize(values);
        validateDuplication(values);
        this.values = new ArrayList<>(values);  // 방어적 복사
    }
    
    private void validateSize(List<LotteryNumber> values) {
        if (values.size() != 6) {
            throw new IllegalArgumentException("로또 번호는 6개여야 합니다");
        }
    }
    
    private void validateDuplication(List<LotteryNumber> values) {
        if (values.size() != new HashSet<>(values).size()) {
            throw new IllegalArgumentException("로또 번호에 중복이 있습니다");
        }
    }
}
```

### 디미터의 법칙 준수

* **객체의 내부 구조에 대한 지식을 최소화하세요**
  * 메서드 체이닝을 최소화하고, "친구하고만 대화하라"는 원칙을 지키세요.

```java
// 개선 전:
String zipcode = user.getAddress().getZipcode().getValue();  // 객체 내부 구조에 지나치게 의존

// 개선 후 (중간 객체 사용):
Address address = user.getAddress();
Zipcode zipcode = address.getZipcode();
String value = zipcode.getValue();

// 또는 더 좋은 방법 (책임 위임):
String zipcode = user.getZipcodeValue();  // User 객체에 책임 위임
```

## 7. 테스트 주도 개발 (TDD)
### Red-Green-Refactor 사이클

* **모든 코드는 TDD 방식으로 개발하세요**
  1. **Red**: 실패하는 테스트를 먼저 작성합니다.
  2. **Green**: 테스트를 통과하는 가장 간단한 코드를 작성합니다.
  3. **Refactor**: 중복을 제거하고 코드를 개선합니다.

```java
// TDD 예시 (로또 번호 유효성 검사)
@Test
void 로또_번호는_1부터_45_사이여야_한다() {
    // given
    int invalidNumber = 46;
    
    // when & then
    assertThatThrownBy(() -> new LotteryNumber(invalidNumber))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("로또 번호는 1부터 45 사이여야 합니다");
}

// 위 테스트가 실패한 후, 구현:
public class LotteryNumber {
    private final int value;
    
    public LotteryNumber(int value) {
        validate(value);
        this.value = value;
    }
    
    private void validate(int value) {
        if (value < 1 || value > 45) {
            throw new IllegalArgumentException("로또 번호는 1부터 45 사이여야 합니다");
        }
    }
}
```

### 테스트 작성 원칙

* **테스트는 다음 원칙을 따라 작성하세요**
  * Given-When-Then 패턴으로 테스트 구조를 명확히 합니다.
  * 각 테스트는 하나의 개념만 검증합니다.
  * 테스트 이름은 한글로 작성하여 도메인 용어를 명확히 표현합니다.

```java
@Test
void 로또_번호에_중복이_있으면_예외가_발생한다() {
    // Given: 테스트 준비
    List<LotteryNumber> numbersWithDuplication = Arrays.asList(
        new LotteryNumber(1),
        new LotteryNumber(2),
        new LotteryNumber(3),
        new LotteryNumber(4),
        new LotteryNumber(5),
        new LotteryNumber(5)  // 중복
    );
    
    // When & Then: 실행 및 검증
    assertThatThrownBy(() -> new LotteryNumbers(numbersWithDuplication))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("로또 번호에 중복이 있습니다");
}
```

## 8. 코드 스타일과 가독성

### 조건문 규칙
* **else 예약어의 사용을 최소화하세요**
  * 조기 반환(early return)을 활용하여 중첩을 줄이세요.

```java
// 개선 전:
public String getGrade(int score) {
    if (score >= 90) {
        return "A";
    } else if (score >= 80) {
        return "B";
    } else if (score >= 70) {
        return "C";
    } else {
        return "D";
    }
}

// 개선 후:
public String getGrade(int score) {
    if (score >= 90) return "A";
    if (score >= 80) return "B";
    if (score >= 70) return "C";
    return "D";
}
```

### 메소드 인자 제한
* **메소드 인자는 최대 3개까지만 사용하세요**
  * 많은 인자가 필요하면, 전용 객체로 묶어 전달하세요.

```java
// 개선 전:
public void createUser(String name, String email, String password, int age, String address) {
    // 너무 많은 인자
}

// 개선 후:
public void createUser(UserCreationRequest request) {
    // 전용 객체를 통해 전달
}

public class UserCreationRequest {
    private final String name;
    private final String email;
    private final String password;
    private final int age;
    private final String address;
    
    // 생성자, 검증 등
}
```

### 의미 있는 이름 사용
* **변수와 메서드 이름은 목적을 명확히 표현해야 합니다**

```java
// 개선 전:
int d;  // 일수
List<User> l;  // 사용자 목록
boolean flg;  // 처리 완료 여부

// 개선 후:
int daysElapsed;
List<User> activeUsers;
boolean isCompleted;
```

### 주석 최소화
* **코드로 의도를 표현하고, 주석은 최소화하세요**
  * 주석이 필요하다고 느낀다면, 메서드나 변수명을 개선하세요.

```java
// 개선 전:
// 사용자가 프리미엄 사용자인지 확인
if (user.getType() == 1) {
    // ...
}

// 개선 후:
if (user.isPremium()) {
    // ...
}
```

## 9. 예외 처리
* **예외는 명확한 계층 구조로 관리하세요**
  * 구체적인 예외 클래스를 사용하여 문제를 명확히 하세요.
  * 예외 메시지는 문제와 해결 방법을 설명해야 합니다.

```java
// 개선 전:
throw new RuntimeException("Error");  // 모호한 예외와 메시지

// 개선 후:
throw new InvalidOrderException(
    "주문의 총액은 0보다 커야 합니다. 현재 값: " + order.getTotalAmount()
);  // 구체적인 예외와 자세한 메시지
```
