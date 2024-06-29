package hello.springtx.apply;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class InitTxTest {
	@Autowired
	Hello hello;

	@TestConfiguration
	static class InitTxTestConfiguration {
		@Bean
		Hello hello() {
			return new Hello();
		}
	}

	@Test
	void go() {
		// 초기화 코드(@PostConstruct)는 스프링 초기화 시점에 호출한다.
		// hello.initV1(); // 직접 코드로 호출하면 트랜잭션이 잘 적용된다.
	}

	static class Hello {
		@PostConstruct
		@Transactional
		public void initV1() {
			boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("hello initV1 @PostConstruct tx active={}", isActive);
		}

		@EventListener(ApplicationReadyEvent.class)
		@Transactional
		public void initV2() {
			boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("hello initV2 @EventListener(ApplicationReadyEvent.class) tx active={}", isActive);
		}
	}
}
