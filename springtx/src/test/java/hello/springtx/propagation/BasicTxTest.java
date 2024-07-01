package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
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

	@Test
	void double_commit() {
		log.info("트랜잭션1 시작");
		TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션1 커밋");
		txManager.commit(tx1);

		log.info("트랜잭션2 시작");
		TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션2 커밋");
		txManager.commit(tx2);
	}

	@Test
	void double_commit_rollback() {
		log.info("트랜잭션1 시작");
		TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션1 커밋");
		txManager.commit(tx1);

		log.info("트랜잭션2 시작");
		TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션2 롤백");
		txManager.rollback(tx2);
	}

	@Test
	void inner_commit() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

		// isNewTransaction : 처음 수행된 트랜잭션(외부 트랜잭션) 여부를 나타낸다.
		log.info("outer.isNewTransaction={}", outer.isNewTransaction());

		inner();
		log.info("외부 트랜잭션 커밋");
		txManager.commit(outer);
	}

	private void inner() {
		log.info("내부 트랜잭션 시작");
		// 이미 트랜잭션(outer, 외부 트랜잭션)이 생성된 후에 또 트랜잭션(inner, 내부 트랜잭션)이 생성된다.
		TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("inner.isNewTransaction={}", inner.isNewTransaction());

		// 내부 트랜잭션을 먼저 커멋한다.
		log.info("내부 트랜잭션 커밋");
		txManager.commit(inner);
	}

	@Test
	void outer_rollback() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

		log.info("내부 트랜잭션 시작");
		TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());

		// 내부 트랜잭션을 먼저 커멋한다.
		log.info("내부 트랜잭션 커밋");
		txManager.commit(inner);

		// 외부 트랜잭션은 롤백된다.
		log.info("외부 트랜잭션 롤백");
		txManager.rollback(outer);
	}

	@Test
	void inner_rollback() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

		log.info("내부 트랜잭션 시작");
		TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());

		// 내부 트랜잭션은 롤백된다.
		log.info("내부 트랜잭션 롤백");
		txManager.rollback(inner);

		// 외부 트랜잭션은 커밋된다.
		log.info("외부 트랜잭션 커밋");
		assertThatThrownBy(() -> txManager.commit(outer))
			.isInstanceOf(UnexpectedRollbackException.class);
	}

	@Test
	void inner_rollback_requires_new() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("outer.isNewTransaction()={}", outer.isNewTransaction()); // true

		log.info("내부 트랜잭션 시작");
		DefaultTransactionAttribute definition = new DefaultTransactionAttribute();

		// 트랜잭션 전파 옵션을 설정(default : PROPAGATION_REQUIRED, 기존 트랜잭션이 존재할 경우 기존 트랜잭션에 참여)
		// PROPAGATION_REQUIRES_NEW : 기존 트랜잭션이 있어도 무시하고 신규 물리 트랜잭션을 생성
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus inner = txManager.getTransaction(definition);
		log.info("inner.isNewTransaction()={}", inner.isNewTransaction()); // true

		// 내부 트랜잭션은 롤백된다.
		log.info("내부 트랜잭션 롤백");
		txManager.rollback(inner);

		// 외부 트랜잭션은 커밋된다.
		log.info("외부 트랜잭션 커밋");
		txManager.commit(outer);
	}
}
