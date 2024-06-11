package hello.itemservice.repository.jdbctemplate;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;

/***
 * ItemRepository 인터페이스를 JdbcTemplate로 구현
 */
@Slf4j
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {
	private final JdbcTemplate template;

	public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public Item save(Item item) {
		String sql = "insert into item(item_name, price, quantity) values(?, ?, ?)";

		// KeyHolder : 데이터베이스에서 생성해준 PK id 값을 자동으로 생성한다(자동 키 증가).
		KeyHolder keyHolder = new GeneratedKeyHolder();

		// DB insert
		template.update(connection -> {
			// 키 값 자동 증가
			PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
			ps.setString(1, item.getItemName());
			ps.setInt(2, item.getPrice());
			ps.setInt(3, item.getQuantity());

			return ps;
		}, keyHolder);

		long key = keyHolder.getKey().longValue();
		item.setId(key);
		return item;
	}

	@Override
	public void update(Long itemId, ItemUpdateDto updateParam) {
		String sql = "update item set item_name = ?, price = ?, quantity = ? where id = ?";
		template.update(sql,
			updateParam.getItemName(),
			updateParam.getPrice(),
			updateParam.getQuantity(),
			itemId);
	}

	@Override
	public Optional<Item> findById(Long id) {
		String sql = "select id, item_name, price, quantity from item where id = ?";

		try {
			// itemRowMapper : sql 쿼리 결과인 ResultSet을 java 객체로 매핑(mapping)
			// queryForObject :
			// 	- 쿼리 결과를 단건으로 조회한다.
			// 	- 조회 결과가 없으면(null)인 경우 EmptyResultDataAccessException 예외가 발생한다.
			//  - 결과가 둘 이상이면 IncorrectResultSizeDataAccessException 예외가 발생한다.
			Item item = template.queryForObject(sql, itemRowMapper(), id);

			// of : 타깃 객체가 반드시 값이 있어야한다. / ofNullable : 타깃 객체가 null 일 수 있다.
			return Optional.of(item);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Override
	public List<Item> findAll(ItemSearchCond cond) {
		String itemName = cond.getItemName();
		Integer maxPrice = cond.getMaxPrice();

		String sql = "select id, item_name, price, quantity from item where 1=1";

		//동적 쿼리
		if (StringUtils.hasText(itemName) || maxPrice != null) {
			sql += " where";
		}
		boolean andFlag = false;
		List<Object> param = new ArrayList<>();
		if (StringUtils.hasText(itemName)) {
			sql += " item_name like concat('%',?,'%')";
			param.add(itemName);
			andFlag = true;
		}
		if (maxPrice != null) {
			if (andFlag) {
				sql += " and";
			}
			sql += " price <= ?";
			param.add(maxPrice);
		}

		log.info("sql={}", sql);

		// query : 쿼리 결과를 리스트로 조회한다.
		return template.query(sql, itemRowMapper(), param.toArray());
	}

	private RowMapper<Item> itemRowMapper() {
		return (rs, rowNum) -> {
			Item item = new Item();
			item.setId(rs.getLong("id"));
			item.setItemName(rs.getString("item_name"));
			item.setPrice(rs.getInt("price"));
			item.setQuantity(rs.getInt("quantity"));

			return item;
		};
	}
}
