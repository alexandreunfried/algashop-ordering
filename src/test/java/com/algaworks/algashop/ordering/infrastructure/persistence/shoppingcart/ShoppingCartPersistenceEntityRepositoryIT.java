package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShoppingCartPersistenceEntityRepositoryIT {

	private final ShoppingCartPersistenceEntityRepository shoppingCartPersistenceEntityRepository;
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
		ShoppingCartPersistenceEntity entity = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart()
				.customer(customerPersistenceEntity)
				.build();

		shoppingCartPersistenceEntityRepository.saveAndFlush(entity);
		Assertions.assertThat(shoppingCartPersistenceEntityRepository.existsById(entity.getId())).isTrue();

		ShoppingCartPersistenceEntity savedEntity = shoppingCartPersistenceEntityRepository.findById(entity.getId()).orElseThrow();

		Assertions.assertThat(savedEntity.getItems()).isNotEmpty();
	}

	@Test
	void shouldCount() {
		long shoppingCartsCount = shoppingCartPersistenceEntityRepository.count();
		Assertions.assertThat(shoppingCartsCount).isZero();
	}

	@Test
	void shouldSetAuditingValues() {
		ShoppingCartPersistenceEntity entity = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart()
				.customer(customerPersistenceEntity)
				.build();
		entity = shoppingCartPersistenceEntityRepository.saveAndFlush(entity);

		Assertions.assertThat(entity.getCreatedByUserId()).isNotNull();

		Assertions.assertThat(entity.getLastModifiedAt()).isNotNull();
		Assertions.assertThat(entity.getLastModifiedByUserId()).isNotNull();
	}

}
