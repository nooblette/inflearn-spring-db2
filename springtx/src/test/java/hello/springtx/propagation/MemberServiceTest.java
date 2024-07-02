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
}