package hello.springtx.order;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class OrderServiceTest {
	@Autowired
	OrderService orderService;

	@Autowired
	OrderRepository orderRepository;

	@Test
	void complete() throws NotEnoughMoneyException {
		// given
		Order order = new Order();
		order.setUsername("정상");

		// when
		orderService.order(order);

		// then
		Order findOrder = orderRepository.findById(order.getId()).get();
		assertThat(findOrder.getPayState()).isEqualTo("완료");
	}

	@Test
	void runtimeException() throws NotEnoughMoneyException {
		// given
		Order order = new Order();
		order.setUsername("예외");

		// when
		assertThatThrownBy(() -> orderService.order(order))
			.isInstanceOf(RuntimeException.class);

		// then
		Optional<Order> orderOptional = orderRepository.findById(order.getId());
		// RuntimeException이 발생하면 트랜잭션이 롤백되므로 DB에 해당 order 데이터가 없어야한다.
		assertThat(orderOptional.isPresent()).isFalse();
	}

	@Test
	void bizException() {
		// given
		Order order = new Order();
		order.setUsername("잔고부족");

		// when
		try {
			orderService.order(order);
			fail("잔고 부족 예외가 발생해야 합니다.");
		} catch (NotEnoughMoneyException e) {
			log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
		}

		// then
		Order findOrder = orderRepository.findById(order.getId()).get();
		// 비즈니스 예외(체크 예외)가 발생하면 트랜잭션이 커밋되므로 DB에 해당 order 데이터 "대기" 상태로 있어야한다.
		assertThat(findOrder.getPayState()).isEqualTo("대기");
	}
}