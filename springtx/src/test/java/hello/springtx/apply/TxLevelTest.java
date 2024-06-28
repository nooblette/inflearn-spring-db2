package hello.springtx.apply;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
public class TxLevelTest {
	@Autowired
	LevelService service;

	@Test
	void orderTest() {
		service.write();
		service.read();
	}

	@TestConfiguration
	static class TxLevelConfig {
		@Bean
		LevelService levelService() {
			return new LevelService();
		}
	}

	@Slf4j
	@Transactional(readOnly = true) // 읽기 전용 트랜잭션 생성
	static class LevelService {

		@Transactional(readOnly = false) // 읽기와 수정까지 가능한 트랜잭션 생성(생략 가능, default : readOnly = false)
		public void write() {
			log.info("call write");
			printTxInfo();
		}

		// 클래스 레벨의 트랜잭션 레벨을 그대로 사용
		public void read() {
			log.info("call read");
			printTxInfo();
		}

		private void printTxInfo() {
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("tx active: {}", txActive);

			boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
			log.info("tx readonly: {}", readOnly);
		}

	}
}
