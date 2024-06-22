package hello.itemservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepositoryV2;
import hello.itemservice.repository.jpa.SpringDataJpaItemRepository;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaConfig {
	private final SpringDataJpaItemRepository springDataJpaItemRepository;

	@Bean
	public ItemService itemService() {
		return new ItemServiceV1(itemRepository());
	}

	@Bean // JpaItemRepositoryV2 리포지토리를 스프링 빈으로 등록한다.
	public ItemRepository itemRepository() {
		return new JpaItemRepositoryV2(springDataJpaItemRepository);
	}
}
