package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
class OrderPersistenceEntityRepositoryIT {

	private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;

	@Autowired
	public OrderPersistenceEntityRepositoryIT(OrderPersistenceEntityRepository orderPersistenceEntityRepository) {
		this.orderPersistenceEntityRepository = orderPersistenceEntityRepository;
	}

	@Test
	void shouldPersist() {
		OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

		orderPersistenceEntityRepository.saveAndFlush(entity);

		assertThat(orderPersistenceEntityRepository.existsById(entity.getId())).isTrue();
	}

	@Test
	void shouldCount() {
		long count = orderPersistenceEntityRepository.count();

		assertThat(count).isZero();
	}

	@Test
	void shouldSetAuditingValues() {
		OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

		entity = orderPersistenceEntityRepository.saveAndFlush(entity);

		assertThat(entity.getCreatedByUserId()).isNotNull();
		assertThat(entity.getLastModifiedAt()).isNotNull();
		assertThat(entity.getLastModifiedByUserId()).isNotNull();
	}

}