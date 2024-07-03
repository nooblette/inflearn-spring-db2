package hello.springtx.propagation;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {
	// 스프링이 EntityManager 빈을 의존관계 주입해준다.
	private final EntityManager entityManager;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void save(Log logMessage) {
		log.info("로그 저장");
		entityManager.persist(logMessage);

		// 로그 적재 중 예외가 발생한 상황을 가정해본다.
		if(logMessage.getMessage().contains("로그예외")) {
			log.info("Log 저장시 예외 발생");
			throw new RuntimeException("에외 발생");
		}
	}

	public Optional<Log> find(String message) {
		return entityManager.createQuery("select l from Log l where l.message = :message", Log.class)
			.setParameter("message", message)
			.getResultList().stream().findAny();
	}
}
