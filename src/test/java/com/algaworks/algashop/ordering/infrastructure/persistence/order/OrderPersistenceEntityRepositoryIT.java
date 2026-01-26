package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrderPersistenceEntityRepositoryIT {

	private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;
	private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

	private CustomerPersistenceEntity customerPersistenceEntity;

	@BeforeEach
	void setup() {
		UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();
		if (!customerPersistenceEntityRepository.existsById(customerId)) {
			customerPersistenceEntity = customerPersistenceEntityRepository.saveAndFlush(
					CustomerPersistenceEntityTestDataBuilder.aCustomer().build()
			);
		}
	}

	@Test
	void shouldPersist() {
		OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
				.customer(customerPersistenceEntity)
				.build();

		orderPersistenceEntityRepository.saveAndFlush(entity);

		assertThat(orderPersistenceEntityRepository.existsById(entity.getId())).isTrue();

		OrderPersistenceEntity savedEntity = orderPersistenceEntityRepository.findById(entity.getId()).orElseThrow();

		assertThat(savedEntity.getItems()).isNotEmpty();
	}

	@Test
	void shouldCount() {
		long count = orderPersistenceEntityRepository.count();

		assertThat(count).isZero();
	}

	@Test
	void shouldSetAuditingValues() {
		OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder.existingOrder()
				.customer(customerPersistenceEntity)
				.build();

		entity = orderPersistenceEntityRepository.saveAndFlush(entity);

		assertThat(entity.getCreatedByUserId()).isNotNull();
		assertThat(entity.getLastModifiedAt()).isNotNull();
		assertThat(entity.getLastModifiedByUserId()).isNotNull();
	}

}
