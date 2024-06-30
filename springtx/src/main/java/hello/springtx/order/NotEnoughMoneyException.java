package hello.springtx.order;

// 결제 잔고 부족시 발생하는 예외, 체크 예외이므로 Exception을 상속받는다.
public class NotEnoughMoneyException extends Exception {
	public NotEnoughMoneyException(String message) {
		super(message);
	}
}
