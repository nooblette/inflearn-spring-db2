package hello.springtx.propagation;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberRepository {
	// 스프링 데이터 JPA 기술이 아직 능숙하지 않고, 눈으로 직접 데이터베이스 접근을 확인하기 위해 스프링 데이터 JPA가 아니라 JPA를 직접 사용한다.
	private final EntityManager entityManager; // 스프링이 EntityManager 빈을 의존관계 주입해준다.

	// JPA에서 모든 데이터 변경은 트랜잭션 안에서 이뤄져야하므로 반드시 @Transactional 어노테이션을 메서드 혹은 클래스 레벨에 둬야한다.
	// 보통 @Transactional 어노테이션은 서비스 레이어에 두지만 예제는 설명상 편의를 위해 리포지토리 계층에 두었다.
	// @Transactional
	public void save(Member member) {
		log.info("member call");
		entityManager.persist(member);
	}

	public Optional<Member> find(String username) {
		// username은 Member의 PK가 아니므로 JPQL을 작성해주어야 한다. (findById() 사용 불가)
		return entityManager.createQuery("select m from Member m where m.username = :username", Member.class)
			.setParameter("username", username)
			.getResultList().stream().findAny();
	}
}
