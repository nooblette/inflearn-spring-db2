package hello.springtx.propagation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Log {
	// 데이터베이스에 남길 Log 엔티티
	@Id
	@GeneratedValue // DB에서 할당하는 id 값을 사용한다.
	private Long id;
	private String message;

	// JPA 엔티티는 스펙상 기본 생성자를 필요로 한다.
	public Log() {
	}

	public Log(String message) {
		this.message = message;
	}
}
