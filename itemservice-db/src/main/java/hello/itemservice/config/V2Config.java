package hello.itemservice.config;

import javax.persistence.EntityManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepositoryV3;
import hello.itemservice.repository.v2.ItemQueryRepositoryV2;
import hello.itemservice.repository.v2.ItemRepositoryV2;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV2;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class V2Config {
	private final ItemRepositoryV2 itemRepositoryV2; // 스프링 데이터 JPA의 구현체는 스프링이 동적 프록시 기술로 생성해준다.
	private final EntityManager entityManager;

	@Bean
	public ItemService itemService() {
		return new ItemServiceV2(itemRepositoryV2, itemQueryRepositoryV2());
	}

	@Bean
	public ItemQueryRepositoryV2 itemQueryRepositoryV2(){
		return new ItemQueryRepositoryV2(entityManager);
	}

	// 테스트 데이터 초기화 때문에 남겨둠
	@Bean
	public ItemRepository itemRepository() {
		return new JpaItemRepositoryV3(entityManager);
	}
}
