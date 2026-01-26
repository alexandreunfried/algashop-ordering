package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomerPersistenceEntityRepositoryIT {

	private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

	@Test
	void shouldPersist() {
		CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.aCustomer().build();

		customerPersistenceEntityRepository.saveAndFlush(entity);

		assertThat(customerPersistenceEntityRepository.existsById(entity.getId())).isTrue();

		CustomerPersistenceEntity savedEntity = customerPersistenceEntityRepository.findById(entity.getId()).orElseThrow();

		assertThat(savedEntity.getEmail()).isEqualTo(entity.getEmail());
	}

	@Test
	void shouldCount() {
		long count = customerPersistenceEntityRepository.count();

		assertThat(count).isZero();
	}

	@Test
	void shouldSetAuditingValues() {
		CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.aCustomer().build();

		entity = customerPersistenceEntityRepository.saveAndFlush(entity);

		assertThat(entity.getCreatedByUserId()).isNotNull();
		assertThat(entity.getLastModifiedAt()).isNotNull();
		assertThat(entity.getLastModifiedByUserId()).isNotNull();
	}

}
