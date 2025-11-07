package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.AddressEmbeddableAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity.CustomerPersistenceEntityBuilder;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.brandNewAddress;

public class CustomerPersistenceEntityTestDataBuilder {

	private CustomerPersistenceEntityTestDataBuilder() {
	}

	public static CustomerPersistenceEntityBuilder aCustomer() {
		return CustomerPersistenceEntity.builder()
				.id(DEFAULT_CUSTOMER_ID.value())
				.firstName("John")
				.lastName("Smith")
				.birthDate(LocalDate.now())
				.email("teste@gmail.com")
				.phone("123456789")
				.document("Document")
				.promotionNotificationsAllowed(true)
				.archived(false)
				.registeredAt(OffsetDateTime.now())
				.archivedAt(OffsetDateTime.now())
				.loyaltyPoints(2)
				.address(AddressEmbeddableAssembler.toAddressEmbeddable(brandNewAddress().build()));
	}


}
