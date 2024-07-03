package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class MemberServiceTest {
	@Autowired
	MemberService memberService;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	LogRepository logRepository;

	/**
	 * memberService	@Transactional:OFF
	 * memberRepository @Transactional:ON
	 * logRepository	@Transactional:ON
	 */
	@Test
	void outerTxOff_success() {
		// given
		String username = "outerTxOff_success";

		// when
		memberService.joinV1(username);

		// then - 회원 가입과 로그 모두 정상 저장 된다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * memberService	@Transactional:OFF
	 * memberRepository @Transactional:ON
	 * logRepository	@Transactional:ON, throw RuntimeException
	 */
	@Test
	void outerTxOff_fail() {
		// given
		String username = "로그예외_outerTxOff_fail";

		// when
		assertThatThrownBy(() -> memberService.joinV1(username))
			.isInstanceOf(RuntimeException.class);

		// then - 회원 가입은 정상 저장되지만, 로그는 롤백되고 저장되지 않는다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isEmpty());
	}

	/**
	 * memberService	@Transactional:ON
	 * memberRepository @Transactional:OFF
	 * logRepository	@Transactional:OFF
	 */
	@Test
	void singleTx() {
		// given
		String username = "singleTx";

		// when
		memberService.joinV1(username);

		// then - 회원 가입과 로그 모두 정상 저장 된다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * memberService	@Transactional:ON
	 * memberRepository @Transactional:ON
	 * logRepository	@Transactional:ON
	 */
	@Test
	void outerTxOn_success() {
		// given
		String username = "outerTxOn_success";

		// when
		memberService.joinV1(username);

		// then - 회원 가입과 로그 모두 정상 저장 된다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isPresent());
	}

	/**
	 * memberService	@Transactional:ON
	 * memberRepository @Transactional:ON
	 * logRepository	@Transactional:ON, throw RuntimeException
	 */
	@Test
	void outerTxOn_fail() {
		// given
		String username = "로그예외_outerTxOn_fail";

		// when
		assertThatThrownBy(() -> memberService.joinV1(username))
			.isInstanceOf(RuntimeException.class);

		// then - 로그 리포지토리(논리 트랜잭션)에서 트랜잭션이 롤백되므로 물리 트랜잭션이 롤백된다. 회원 가입과 로그 모두 저장되지 않는다.
		assertTrue(memberRepository.find(username).isEmpty());
		assertTrue(logRepository.find(username).isEmpty());
	}

	/**
	 * memberService	@Transactional:ON
	 * memberRepository @Transactional:ON
	 * logRepository	@Transactional:ON, throw RuntimeException
	 */
	@Test
	void recoverException_fail() {
		// given
		String username = "로그예외_recoverException_fail";

		// when
		assertThatThrownBy(() -> memberService.joinV2(username))
			.isInstanceOf(UnexpectedRollbackException.class);

		// then - 로그 리포지토리(내부 트랜잭션, 신규 트랜잭션 X)에서 런타임 예외가 발생하여 트랜잭션이 롤백된다.
		// 외부 트랜잭션(신규 트랜잭션)이 런타임 예외를 정상흐름으로 변환해도 물리 트랜잭션이 롤백된다. (UnexpectedRollbackException 발생)
		// 회원 가입과 로그 모두 저장되지 않는다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isEmpty());
	}

	/**
	 * memberService	@Transactional:ON
	 * memberRepository @Transactional:ON
	 * logRepository	@Transactional:ON(REQUIRES_NEW), throw RuntimeException
	 */
	@Test
	void recoverException_success() {
		// given
		String username = "로그예외_recoverException_success";

		// when
		memberService.joinV2(username);

		// then - 로그 리포지토리(내부 트랜잭션, 신규 트랜잭션)에서 런타임 예외가 발생하여 트랜잭션이 롤백된다.
		// 외부 트랜잭션(신규 트랜잭션)이 런타임 예외를 정상흐름으로 변환하면 물리 트랜잭션에 커밋을 수행한다. (UnexpectedRollbackException 발생 X)
		// 로그 저장이 실패하더라도 회원가입은 성공한다.
		assertTrue(memberRepository.find(username).isPresent());
		assertTrue(logRepository.find(username).isEmpty());
	}
}