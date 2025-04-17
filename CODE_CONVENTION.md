# Java 코드 컨벤션 가이드

## 기본 원칙
이 코드 컨벤션은 클린 코드와 객체지향 프로그래밍의 원칙에 기반하여 더 유지보수하기 좋고, 이해하기 쉬운 코드를 작성하는 것을 목표로 합니다.

## 참고 자료
* [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
* [자바 컨벤션 참고](https://myeonguni.tistory.com/1596)

## 1. 들여쓰기(Indentation) 규칙
* **한 메서드에 오직 한 단계의 들여쓰기(indent)만 허용합니다.**
    * 메서드가 두 단계 이상의 들여쓰기를 가진다면, 해당 부분을 별도의 메서드로 추출하는 것을 고려합니다.
    * 이는 메서드가 한 가지 일만 하도록 유지하는 데 도움이 됩니다.

```java
// 나쁜 예:
public void process() {
    for (Item item : items) {        // 첫 번째 들여쓰기
        if (item.isValid()) {        // 두 번째 들여쓰기
            item.process();
        }
    }
}

// 좋은 예:
public void process() {
    for (Item item : items) {
        processIfValid(item);
    }
}

private void processIfValid(Item item) {
    if (item.isValid()) {
        item.process();
    }
}
```

## 2. 조건문 규칙
* **else 예약어를 사용하지 않습니다.**
    * 조기 반환(early return)을 사용하여 중첩된 조건문을 피합니다.
    * 각 조건을 독립적으로 처리하면 코드 가독성이 향상됩니다.

```java
// 나쁜 예:
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

// 좋은 예:
public String getGrade(int score) {
    if (score >= 90) {
        return "A";
    }
    if (score >= 80) {
        return "B";
    }
    if (score >= 70) {
        return "C";
    }
    return "D";
}
```

## 3. 원시값과 문자열 포장
* **모든 원시값과 문자열을 적절한 객체로 포장합니다.**
    * 이는 값 객체(Value Object)를 통해 도메인의 의미를 명확히 합니다.
    * 타입 안전성을 높이고 관련 로직을 응집시킵니다.

```java
// 나쁜 예:
public class User {
    private String email;
    private String password;
    
    public boolean isValidEmail() {
        return email.contains("@");
    }
}

// 좋은 예:
public class User {
    private Email email;
    private Password password;
}

public class Email {
    private final String value;
    
    public Email(String value) {
        validate(value);
        this.value = value;
    }
    
    private void validate(String value) {
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    public String getValue() {
        return value;
    }
}
```

## 4. 일급 컬렉션 사용
* **컬렉션을 포함한 클래스는 반드시 다른 멤버 변수가 없어야 합니다.**
    * 컬렉션을 감싸는 전용 클래스를 만들어 관련 동작을 응집시킵니다.
    * 이를 통해 컬렉션에 대한 비즈니스 규칙을 명확히 표현할 수 있습니다.

```java
// 나쁜 예:
public class Lottery {
    private List<LotteryNumber> numbers;
    private String name;
    private LocalDate drawDate;
    
    public boolean contains(LotteryNumber number) {
        return numbers.contains(number);
    }
}

// 좋은 예:
public class Lottery {
    private LotteryNumbers numbers;
    private String name;
    private LocalDate drawDate;
}

public class LotteryNumbers {
    private final List<LotteryNumber> values;
    
    public LotteryNumbers(List<LotteryNumber> values) {
        validateSize(values);
        validateDuplication(values);
        this.values = new ArrayList<>(values);
    }
    
    private void validateSize(List<LotteryNumber> values) {
        if (values.size() != 6) {
            throw new IllegalArgumentException("로또 번호는 6개여야 합니다.");
        }
    }
    
    private void validateDuplication(List<LotteryNumber> values) {
        if (values.size() != new HashSet<>(values).size()) {
            throw new IllegalArgumentException("로또 번호에 중복이 있습니다.");
        }
    }
    
    public boolean contains(LotteryNumber number) {
        return values.contains(number);
    }
}
```

## 5. 인스턴스 변수 제한
* **3개 이상의 인스턴스 변수를 가진 클래스는 구현하지 않습니다.**
    * 클래스의 책임을 명확히 하고 단일 책임 원칙을 유지합니다.
    * 많은 인스턴스 변수는 클래스가 여러 책임을 가질 가능성이 높습니다.

```java
// 나쁜 예:
public class Order {
    private String orderId;
    private Customer customer;
    private List<OrderItem> items;
    private LocalDateTime orderDate;
    private DeliveryAddress address;
    private PaymentInfo paymentInfo;
}

// 좋은 예:
public class Order {
    private OrderId orderId;
    private Customer customer;
    private OrderItems items;
}

public class OrderDetail {
    private OrderId orderId;
    private LocalDateTime orderDate;
    private DeliveryAddress address;
}

public class PaymentDetail {
    private OrderId orderId;
    private PaymentInfo paymentInfo;
}
```

## 6. Getter/Setter 제한
* **핵심 로직을 구현하는 도메인 객체에는 getter/setter를 사용하지 않습니다.**
    * 객체의 상태를 직접 노출하지 않고, 행동을 통해 상호작용합니다.
    * DTO(Data Transfer Object)에는 예외적으로 getter/setter 사용을 허용합니다.

```java
// 나쁜 예 (도메인 객체에 getter/setter 사용):
public class Cart {
    private List<Item> items;
    
    public List<Item> getItems() {
        return items;
    }
    
    public void setItems(List<Item> items) {
        this.items = items;
    }
}

// 클라이언트 코드
cart.getItems().add(new Item());  // 캡슐화 위반

// 좋은 예:
public class Cart {
    private final List<Item> items = new ArrayList<>();
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public boolean hasItem(ItemId itemId) {
        return items.stream()
                .anyMatch(item -> item.isSameId(itemId));
    }
    
    public int totalPrice() {
        return items.stream()
                .mapToInt(Item::getPrice)
                .sum();
    }
}

// DTO는 허용
public class CartDto {
    private List<ItemDto> items;
    
    public List<ItemDto> getItems() {
        return items;
    }
    
    public void setItems(List<ItemDto> items) {
        this.items = items;
    }
}
```

## 7. 메소드 인자 수 제한
* **메소드의 인자는 최대 3개까지만 허용합니다.**
    * 가능하면 2개 이하로 유지하도록 노력합니다.
    * 많은 인자가 필요한 경우, 객체로 묶어서 전달합니다.

```java
// 나쁜 예:
public void createUser(String name, String email, String password, int age, String address) {
    // ...
}

// 좋은 예:
public void createUser(UserCreationRequest request) {
    // ...
}

public class UserCreationRequest {
    private final String name;
    private final String email;
    private final String password;
    private final int age;
    private final String address;
    
    // 생성자, validation 등
}
```

## 8. 디미터의 법칙 준수
* **코드 한 줄에 점(.)을 하나만 허용합니다.**
    * "친구하고만 대화하라"는 원칙을 지킵니다.
    * 객체의 내부 구조에 대한 지식을 최소화합니다.

```java
// 나쁜 예:
String zipcode = user.getAddress().getZipcode().getValue();

// 좋은 예:
Address address = user.getAddress();
Zipcode zipcode = address.getZipcode();
String value = zipcode.getValue();

// 더 좋은 예:
String zipcode = user.getZipcodeValue();
```

## 9. 메소드의 단일 책임
* **메소드가 한가지 일만 담당하도록 구현합니다.**
    * 메소드 이름은 그 기능을 명확히 표현해야 합니다.
    * 하나의 추상화 수준에서 작업하도록 합니다.

```java
// 나쁜 예:
public void processOrder(Order order) {
    // 주문 유효성 검사
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("주문 항목이 없습니다.");
    }
    
    // 재고 확인
    for (OrderItem item : order.getItems()) {
        if (inventoryService.getStock(item.getProductId()) < item.getQuantity()) {
            throw new OutOfStockException(item.getProductId());
        }
    }
    
    // 결제 처리
    paymentService.process(order.getPaymentDetails());
    
    // 주문 저장
    orderRepository.save(order);
    
    // 이메일 발송
    emailService.sendOrderConfirmation(order);
}

// 좋은 예:
public void processOrder(Order order) {
    validateOrder(order);
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

private void checkInventory(Order order) {
    // ...
}

// 나머지 메소드들...
```

## 10. 작은 클래스 유지
* **클래스를 작게 유지하기 위해 노력합니다.**
    * 클래스는 명확한 하나의 책임만 가져야 합니다(SRP).
    * 일반적으로 클래스의 메소드 수가 많다면 책임이 너무 많을 가능성이 높습니다.

```java
// 나쁜 예:
public class UserService {
    // 사용자 관련 메소드
    public User createUser(...) { ... }
    public User findByEmail(...) { ... }
    
    // 인증 관련 메소드
    public Token login(...) { ... }
    public void logout(...) { ... }
    public User getCurrentUser() { ... }
    
    // 권한 관련 메소드
    public boolean hasPermission(...) { ... }
    public List<Role> getUserRoles(...) { ... }
}

// 좋은 예:
public class UserService {
    public User createUser(...) { ... }
    public User findByEmail(...) { ... }
}

public class AuthenticationService {
    public Token login(...) { ... }
    public void logout(...) { ... }
    public User getCurrentUser() { ... }
}

public class AuthorizationService {
    public boolean hasPermission(...) { ... }
    public List<Role> getUserRoles(...) { ... }
}
```

## 11. 포맷팅
* **IntelliJ 또는 Eclipse 통합 개발 도구의 기본 포맷터를 사용합니다.**
    * 팀 내에서 동일한 포맷팅 설정을 공유하는 것이 좋습니다.
    * 프로젝트에 `.editorconfig` 파일을 추가하여 일관된 형식을 유지합니다.

## 12. 예외 처리
* **예외는 명확한 계층 구조로 관리합니다.**
    * 가능한 한 구체적인 예외를 던집니다.
    * 예외 메시지는 문제와 해결 방법을 명확히 설명해야 합니다.

```java
// 나쁜 예:
throw new RuntimeException("Error");

// 좋은 예:
throw new InvalidOrderException("주문의 총액은 0보다 커야 합니다. 현재 값: " + order.getTotalAmount());
```

## 13. 변수 네이밍
* **의미 있는 변수 이름을 사용합니다.**
    * 약어 사용을 최소화하고 목적이 명확한 이름을 사용합니다.
    * 자바 네이밍 컨벤션을 따릅니다: camelCase(변수, 메소드), PascalCase(클래스)

```java
// 나쁜 예:
int d; // 일수
List<User> l; // 사용자 목록
boolean flg; // 처리 완료 여부

// 좋은 예:
int daysElapsed;
List<User> activeUsers;
boolean isCompleted;
```

## 14. 주석 사용
* **코드로 의도를 표현하고, 주석은 최소화합니다.**
    * 좋은 코드는 대부분의 경우 주석이 필요하지 않습니다.
    * 주석이 필요하다고 느낀다면, 코드를 더 명확하게 리팩토링할 수 있는지 고려합니다.

```java
// 나쁜 예:
// 사용자가 프리미엄 사용자인지 확인
if (user.getType() == 1) {
    // ...
}

// 좋은 예:
if (user.isPremium()) {
    // ...
}
```

## 15. TDD(테스트 주도 개발) 적용
* **모든 코드는 반드시 TDD 방식으로 개발합니다.**
* **Red-Green-Refactor 사이클을 준수합니다:**
    1. **Red**: 실패하는 테스트를 먼저 작성합니다.
    2. **Green**: 테스트를 통과하는 가장 간단한 코드를 작성합니다.
    3. **Refactor**: 중복을 제거하고 코드를 개선합니다.

* **테스트를 먼저 작성함으로써:**
    * 설계에 대해 미리 고민하게 됩니다.
    * 코드의 결합도를 낮추고 응집도를 높이는 설계가 자연스럽게 도출됩니다.
    * 모든 코드가 테스트로 검증됩니다.

* **테스트는 다음 원칙을 따라 작성합니다:**
    * 테스트 이름은 한글로 작성하여 도메인 용어를 명확히 표현합니다.
    * Given-When-Then 패턴을 사용해 테스트 구조를 명확히 합니다.
    * 각 테스트는 하나의 개념만 검증합니다.
    * 테스트 간에 의존성이 없어야 합니다.
    * 테스트 코드도 프로덕션 코드와 동일한 품질 기준을 적용합니다.

```java
// 좋은 TDD 테스트 예:
@Test
void 로또_번호는_6개여야_한다() {
    // given
    List<LotteryNumber> fiveNumbers = List.of(
        new LotteryNumber(1),
        new LotteryNumber(2),
        new LotteryNumber(3),
        new LotteryNumber(4),
        new LotteryNumber(5)
    );
    
    // when & then
    assertThatThrownBy(() -> new LotteryNumbers(fiveNumbers))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("로또 번호는 6개여야 합니다");
}

@Test
void 로또_번호_범위를_벗어나면_예외가_발생한다() {
    // given
    int invalidNumber = 46;
    
    // when & then
    assertThatThrownBy(() -> new LotteryNumber(invalidNumber))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("로또 번호는 1부터 45 사이여야 합니다");
}
```

* **TDD 실천 방법:**
    * 가장 단순한 테스트부터 시작합니다.
    * 테스트가 실패하는 것을 반드시 확인한 후에 구현을 시작합니다.
    * 테스트를 통과하는 가장 단순한 코드를 먼저 작성합니다.
    * 리팩토링 단계에서 설계를 개선합니다.
    * 한 번에 한 가지 기능만 개발합니다.
