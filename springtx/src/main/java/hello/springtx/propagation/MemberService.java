package hello.springtx.propagation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final LogRepository logRepository;

	@Transactional
	public void joinV1(String username) {
		// 트랜잭션을 각각 사용한다.
		Member member = new Member(username);
		Log logMessage = new Log(username);

		log.info("memberRepository 호출 시작");
		memberRepository.save(member);
		log.info("memberRepository 호출 종료");

		log.info("logRepository 호출 시작");
		// 로그를 저장하다 실패하면 RuntimeException(언체크 예외)이 발생하고 트랜잭션이 롤백된다.
		// 즉, 로그를 저장하다 예외가 발생하면 회원가입도 실패한다.
		logRepository.save(logMessage);

		log.info("logRepository 호출 종료");
	}

	public void joinV2(String username) {
		// 트랜잭션을 각각 사용한다.
		Member member = new Member(username);
		Log logMessage = new Log(username);

		log.info("memberRepository 호출 시작");
		memberRepository.save(member);
		log.info("memberRepository 호출 종료");

		log.info("logRepository 호출 시작");

		try {
			logRepository.save(logMessage);
		} catch (RuntimeException e) {
			// 로그를 저장하다 실패해도 회원가입은 성공적으로 진행된다. (로그 적재가 실패하더라도 고객은 이상없이 사용할 수 있게 하자)
			log.info("log 저장에 실패했습니다. logMessage={}", logMessage);
			log.info("예외 처리 및 정상 흐름 반환");
		}

		log.info("logRepository 호출 종료");
	}
}
