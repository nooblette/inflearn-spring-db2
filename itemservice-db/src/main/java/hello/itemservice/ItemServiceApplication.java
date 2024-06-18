package hello.itemservice;

import javax.sql.DataSource;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Slf4j
@Import(JpaConfig.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

	// @Bean
	// @Profile("test")
	// public DataSource dataSource(){
	// 	// DataSource를 직접 등록
	// 	log.info("메모리 데이터베이스 초기화");
	// 	DriverManagerDataSource dataSource = new DriverManagerDataSource();
	// 	dataSource.setDriverClassName("org.h2.Driver"); // H2 데이터베이스의 JDBC 구현체(Driver) 사용
	// 	dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"); // 메모리 모드로 사용, JVM 내에 데이터베이스를 만들고 데이터를 쌓는다.
	// 	dataSource.setUsername("sa");
	// 	dataSource.setPassword("");
	// 	return dataSource;
	// }
}
