package hello.itemservice.repository.v2;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.itemservice.domain.Item;

public interface ItemRepositoryV2 extends JpaRepository<Item, Long> {
}
