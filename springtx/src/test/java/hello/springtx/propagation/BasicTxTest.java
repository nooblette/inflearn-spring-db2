package hello.springtx.propagation;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class BasicTxTest {
	@Autowired
	// 스프링부트는 트랜잭션 매니저(PlatformTransactionManager) 빈을 스프링 컨테이너에 자동으로 등록해준다.
	// 개발자가 직접 트랜잭션 매니저 빈을 등록하는 경우, 개발자가 등록한 빈이 높은 우선순위를 갖는다. (해당 트랜잭션 매니저가  사용된다.)
	PlatformTransactionManager txManager;

	@TestConfiguration
	static class BasicTxTestConfiguration {
		@Bean
		public PlatformTransactionManager transactionManager(DataSource dataSource) {
			// 개발자가 직접 트랜잭션 매니저(PlatformTransactionManager) 빈을 등록하는 경우, 개발자가 등록한 트랜잭션 매니저 빈이 우선권을 갖는다.(해당 트랜잭션 매니저가 사용된다.)
			return new DataSourceTransactionManager(dataSource);
		}
	}

	@Test
	void commit() {
		log.info("트랜잭션 시작");
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute()); // 트랜잭션을 연다. (트랜잭션을 시작한다.)

		log.info("트랜잭션 커밋 시작");
		txManager.commit(status);
		log.info("트랜잭션 커밋 완료");
	}

	@Test
	void rollback() {
		log.info("트랜잭션 시작");
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute()); // 트랜잭션을 연다. (트랜잭션을 시작한다.)

		log.info("트랜잭션 롤백 시작");
		txManager.rollback(status);
		log.info("트랜잭션 롤백 완료");

	}
}
