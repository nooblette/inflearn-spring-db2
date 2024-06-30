package hello.springtx.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
	// JpaRepository의 구현체를 주입받아 사용
	private final OrderRepository orderRepository;

	// JPA는 트랜잭션 커밋 시점에 Order 데이터를 DB에 반영한다.
	@Transactional
	public void order(Order order) throws NotEnoughMoneyException {
		log.info("call order");

		// 주문 데이터 저장
		orderRepository.save(order);

		log.info("결제 프로세스 진입");
		if(order.getUsername().equals("예외")){
			// 시스템 예외(언체크 예외, 런타임 예외) - 트랜잭션 롤백
			log.info("시스템 예외(언체크 예외, 런타임 예외)");
			throw new RuntimeException("시스템 예외");
		} else if (order.getUsername().equals("잔고부족")) {
			// 비즈니스 예외(체크 예외) - 트랜잭션 커밋
			order.setPayState("대기"); // 결제상태를 "대기"로 업데이트
			throw new NotEnoughMoneyException("잔고가 부족합니다.");
		} else {
			// 정상 승인
			log.info("정상 승인");
			order.setPayState("완료");
		}
		log.info("결제 프로세스 완료");
	}

}
