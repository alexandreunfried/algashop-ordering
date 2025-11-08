package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;


import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder.aCustomer;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerPersistenceEntityDisassemblerTest {

	private final CustomerPersistenceEntityDisassembler disassembler = new CustomerPersistenceEntityDisassembler();

	@Test
	void shouldConvertFromPersistence() {
		var persistenceEntity = aCustomer().build();
		var domainEntity = disassembler.toDomainEntity(persistenceEntity);

		assertThat(domainEntity).satisfies(entity -> {
			assertThat(entity.id().value()).isEqualTo(persistenceEntity.getId());
			assertThat(entity.fullName().firstName()).isEqualTo(persistenceEntity.getFirstName());
			assertThat(entity.fullName().lastName()).isEqualTo(persistenceEntity.getLastName());
			assertThat(entity.email().value()).isEqualTo(persistenceEntity.getEmail());
			assertThat(entity.birthDate().value()).isEqualTo(persistenceEntity.getBirthDate());
			assertThat(entity.document().value()).isEqualTo(persistenceEntity.getDocument());
			assertThat(entity.registeredAt()).isEqualTo(persistenceEntity.getRegisteredAt());
			assertThat(entity.archivedAt()).isEqualTo(persistenceEntity.getArchivedAt());
			assertThat(entity.isPromotionNotificationsAllowed())
					.isEqualTo(persistenceEntity.getPromotionNotificationsAllowed());
			assertThat(entity.isArchived())
					.isEqualTo(persistenceEntity.getArchived());
			assertThat(entity.address())
					.isEqualTo(AddressEmbeddableDisassembler.toAddress(persistenceEntity.getAddress()));
		});
	}

}
