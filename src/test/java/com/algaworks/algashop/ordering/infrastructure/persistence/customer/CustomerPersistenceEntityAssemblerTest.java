package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddableAssembler;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerPersistenceEntityAssemblerTest {

	private final CustomerPersistenceEntityAssembler assembler = new CustomerPersistenceEntityAssembler();

	@Test
	void shouldConvertToDomainCustomer() {
		var customer = CustomerTestDataBuilder.existingCustomer().build();
		var customerPersistenceEntity = assembler.fromDomain(customer);

		assertThat(customerPersistenceEntity).satisfies(
				c -> assertThat(c.getId()).isEqualTo(customer.id().value()),
				c -> assertThat(c.getFirstName()).isEqualTo(customer.fullName().firstName()),
				c -> assertThat(c.getLastName()).isEqualTo(customer.fullName().lastName()),
				c -> assertThat(c.getEmail()).isEqualTo(customer.email().value()),
				c -> assertThat(c.getPhone()).isEqualTo(customer.phone().value()),
				c -> assertThat(c.getDocument()).isEqualTo(customer.document().value()),
				c -> assertThat(c.getPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed()),
				c -> assertThat(c.getArchived()).isEqualTo(customer.isArchived()),
				c -> assertThat(c.getAddress()).isEqualTo(AddressEmbeddableAssembler.toAddressEmbeddable(customer.address()))
		);
	}

}
