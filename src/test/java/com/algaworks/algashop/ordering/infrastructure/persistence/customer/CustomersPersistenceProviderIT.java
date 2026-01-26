package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
		CustomersPersistenceProvider.class,
		CustomerPersistenceEntityAssembler.class,
		CustomerPersistenceEntityDisassembler.class,
		SpringDataAuditingConfig.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomersPersistenceProviderIT {

	private final CustomersPersistenceProvider persistenceProvider;
	private final CustomerPersistenceEntityRepository entityRepository;

	@Test
	void givenANewCustomer_shouldPersistEntity() {
		Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

		persistenceProvider.add(customer);

		var persistenceEntity = persistenceProvider.ofId(customer.id()).orElseThrow();

		assertThat(persistenceEntity).satisfies(entity -> {
			assertThat(entity.id()).isNotNull();
			assertThat(entity.fullName()).isEqualTo(customer.fullName());
			assertThat(entity.birthDate()).isEqualTo(customer.birthDate());
			assertThat(entity.email()).isEqualTo(customer.email());
			assertThat(entity.phone()).isEqualTo(customer.phone());
			assertThat(entity.document()).isEqualTo(customer.document());
			assertThat(entity.isPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed());
			assertThat(entity.isArchived()).isNotNull();
			assertThat(entity.registeredAt()).isNotNull();
			assertThat(entity.address()).isEqualTo(customer.address());
		});
	}

	@Test
	void givenANewCustomer_whenChangeName_shouldPersistUpdatedNameAndVersion() {
		var customer = CustomerTestDataBuilder.brandNewCustomer().build();
		persistenceProvider.add(customer);

		var persisted = persistenceProvider.ofId(customer.id()).orElseThrow();
		var previousVersion = persisted.version();

		var newName = new FullName("Robert", "Martin");
		persisted.changeName(newName);
		persistenceProvider.add(persisted);

		var reloaded = persistenceProvider.ofId(customer.id()).orElseThrow();

		assertThat(reloaded).satisfies(entity -> {
			assertThat(entity.id()).isNotNull();
			assertThat(entity.fullName()).isEqualTo(newName);
			assertThat(entity.birthDate()).isEqualTo(customer.birthDate());
			assertThat(entity.email()).isEqualTo(customer.email());
			assertThat(entity.phone()).isEqualTo(customer.phone());
			assertThat(entity.document()).isEqualTo(customer.document());
			assertThat(entity.isPromotionNotificationsAllowed())
					.isEqualTo(customer.isPromotionNotificationsAllowed());
			assertThat(entity.isArchived()).isNotNull();
			assertThat(entity.registeredAt()).isNotNull();
			assertThat(entity.address()).isEqualTo(customer.address());
			assertThat(entity.version()).isGreaterThan(previousVersion);
		});
	}

	@Test
	void shouldUpdateAndKeepPersistenceEntityState() {
		Customer customer = CustomerTestDataBuilder.existingCustomer().build();
		UUID customerId = customer.id().value();
		persistenceProvider.add(customer);

		var persistenceEntity = entityRepository.findById(customerId).orElseThrow();

		Assertions.assertThat(persistenceEntity.getFirstName()).isEqualTo(customer.fullName().firstName());
		Assertions.assertThat(persistenceEntity.getLastName()).isEqualTo(customer.fullName().lastName());

		Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
		Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
		Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();

		customer = persistenceProvider.ofId(customer.id()).orElseThrow();
		var newName = new FullName("Robert", "Martin");
		customer.changeName(newName);

		persistenceProvider.add(customer);

		persistenceEntity = entityRepository.findById(customerId).orElseThrow();

		Assertions.assertThat(persistenceEntity.getFirstName()).isEqualTo(newName.firstName());
		Assertions.assertThat(persistenceEntity.getLastName()).isEqualTo(newName.lastName());

		Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
		Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
		Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	void shouldAddFindAndNotFailWhenNoTransaction() {
		Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
		persistenceProvider.add(customer);

		Assertions.assertThatNoException().isThrownBy(
				() -> persistenceProvider.ofId(customer.id()).orElseThrow()
		);
	}

}
