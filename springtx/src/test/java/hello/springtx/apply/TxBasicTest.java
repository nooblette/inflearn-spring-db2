package hello.springtx.apply;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest // AOP 등 스프링 트랜잭션 기능이 동작해야하므로 @SpringBootTest 어노테이션을 추가한다.
public class TxBasicTest {
	// BasicService 빈을 DI(의존관게 주입)한다.
	@Autowired BasicService basicService;

	@Test
	void proxyCheck() {
		log.info("aop class={}", basicService.getClass());
		assertThat(AopUtils.isAopProxy(basicService)).isTrue();
	}

	@Test
	void txTest() {
		basicService.tx();
		basicService.nonTx();
	}
	@TestConfiguration
	static class TxBasicTestConfiguration {
		// BasicService 클래스 빈 등록하며 스프링 컨테이너의 관리를 받게 한다.
		@Bean
		BasicService basicService() {
			return new BasicService();
		}
	}

	@Slf4j
	static class BasicService {
		@Transactional
		public void tx() {
			log.info("call tx");

			// txActive : 트랜잭션이 활성화 되었는지 여부를 확인할 수 있다.
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("txActive: {}", txActive);
		}

		public void nonTx() {
			log.info("call nonTx");

			// txActive : 트랜잭션이 활성화 되었는지 여부를 확인할 수 있다.
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("txActive: {}", txActive);
		}

	}

}
