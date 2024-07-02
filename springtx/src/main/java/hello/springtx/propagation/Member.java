package hello.springtx.propagation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {
	@Id
	@GeneratedValue // DB에서 할당하는 id 값을 사용한다.
	private Long id;
	private String username;

	// JPA 엔티티(Entity)로 매핑하기 위해선 기본 생성자가 필요하다. (JPA 스펙상 기본 생성자가 필요)
	public Member() {
	}

	public Member(String username) {
		this.username = username;
	}
}
