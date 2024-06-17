package hello.itemservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.mybatis.ItemMapper;
import hello.itemservice.repository.mybatis.MyBatisItemRepository;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;

@Configuration // MyBatisConfig에서 등록한 스프링 빈들은 컨테이너의 관리를 받도록 한다.
@RequiredArgsConstructor
public class MyBatisConfig {
	// private final DataSource dataSource;
	private final ItemMapper itemMapper; // Mybatis 모듈이 DataSource, TransactionManager 등을 읽어서 ItemMapper와 연결해준다.

	@Bean // MyBatisItemRepository 리포지토리를 의존관계 주입받는 ItemService를 스프링 빈으로 등록한다.
	public ItemService itemService() {
		return new ItemServiceV1(itemRepository());
	}

	@Bean // MyBatisItemRepository 리포지토리를 스프링 빈으로 등록한다.
	public ItemRepository itemRepository() {
		return new MyBatisItemRepository(itemMapper);
	}
}
