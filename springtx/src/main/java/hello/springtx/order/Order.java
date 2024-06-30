package hello.springtx.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity // Order 클래스를 엔티티로 선언한다. (JPA를 사용)
@Table(name = "orders") // Order 클래스는 orders라는 데이터베이스 테이블을 매핑하는 엔티티
@Getter
@Setter
public class Order {

	@Id
	@GeneratedValue
	private Long id;

	private String username; // 정상, 예외, 잔고부족
	private String payState; // 대기, 완료
}
