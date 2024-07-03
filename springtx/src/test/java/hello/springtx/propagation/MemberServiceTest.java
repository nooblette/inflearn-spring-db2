package hello.springtx.propagation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}