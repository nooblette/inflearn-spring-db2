package hello.springtx.apply;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {
	@Autowired // InternalCallTestConfiguration에서 CallService 객체를 빈으로 등록하였으므로, 의존관계 주입이 가능해진다.
	CallService callService;

	@Test
	void printProxy(){
		log.info("callService={}", callService.getClass());
	}

	@Test
	void internalCall() {
		// internal 메서드에 트랜잭션이 적용된다.
		callService.internal();
	}

	@Test
	void externalCall() {
		// internal 메서드에 트랜잭션이 적용된다.
		callService.external();
	}

	@TestConfiguration
	static class InternalCallTestConfiguration {
		@Bean // CallService 객체를 스프링 빈으로 등록
		public CallService callService() {
			return new CallService();
		}
	}

	@Slf4j
	static class CallService {
		public void external(){
			// 트랜잭션이 필요 없는 영역
			log.info("call external");
			printTxInfo();
			internal();
		}

		@Transactional
		public void internal(){
			// 트랜잭션이 필요한 영역
			log.info("call internal");
			printTxInfo();
		}

		private void printTxInfo() {
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("tx active: {}", txActive);
		}
	}
}
