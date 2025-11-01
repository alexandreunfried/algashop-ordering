package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({CustomersPersistenceProvider.class, CustomerPersistenceEntityAssembler.class, CustomerPersistenceEntityDisassembler.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CustomersIT {

	private final Customers customers;

	@Test
	void shouldPersistAndFind() {
		Customer originalCustomer = CustomerTestDataBuilder.existingCustomer().build();
		CustomerId customerId = originalCustomer.id();
		customers.add(originalCustomer);

		Optional<Customer> possibleCustomer = customers.ofId(customerId);

		assertThat(possibleCustomer).isPresent();

		Customer savedCustomer = possibleCustomer.get();

		assertThat(savedCustomer).satisfies(
				s -> assertThat(s.id()).isEqualTo(customerId),
				s -> assertThat(s.fullName()).isEqualTo(originalCustomer.fullName()),
				s -> assertThat(s.birthDate()).isEqualTo(originalCustomer.birthDate()),
				s -> assertThat(s.email()).isEqualTo(originalCustomer.email()),
				s -> assertThat(s.phone()).isEqualTo(originalCustomer.phone()),
				s -> assertThat(s.document()).isEqualTo(originalCustomer.document()),
				s -> assertThat(s.isPromotionNotificationsAllowed()).isEqualTo(originalCustomer.isPromotionNotificationsAllowed()),
				s -> assertThat(s.archivedAt()).isEqualTo(originalCustomer.archivedAt()),
				s -> assertThat(s.loyaltyPoints()).isEqualTo(originalCustomer.loyaltyPoints()),
				s -> assertThat(s.address()).isEqualTo(originalCustomer.address())
		);
	}

	@Test
	void shouldUpdateExistingCustomer() {
		Customer customer = CustomerTestDataBuilder.existingCustomer().build();
		customers.add(customer);

		customer = customers.ofId(customer.id()).orElseThrow();
		var newName = new FullName("Robert", "Martin");
		customer.changeName(newName);

		customers.add(customer);

		customer = customers.ofId(customer.id()).orElseThrow();

		assertThat(customer.fullName()).isEqualTo(newName);
	}

	@Test
	void shouldNotAllowStaleUpdates() {
		Customer customer = CustomerTestDataBuilder.existingCustomer().build();
		customers.add(customer);

		Customer customerT1 = customers.ofId(customer.id()).orElseThrow();
		Customer customerT2 = customers.ofId(customer.id()).orElseThrow();

		var newNameT1 = new FullName("Robert", "Martin");
		customerT1.changeName(newNameT1);
		customers.add(customerT1);

		var newNameT2 = new FullName("Eric", "Evans");
		customerT2.changeName(newNameT2);

		Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
				.isThrownBy(() -> customers.add(customerT2));

		Customer savedCustomer = customers.ofId(customer.id()).orElseThrow();

		assertThat(savedCustomer.fullName()).isEqualTo(newNameT1);
	}

	@Test
	void shouldCountExistingCustomers() {
		Assertions.assertThat(customers.count()).isZero();

		Customer customer1 = CustomerTestDataBuilder.brandNewCustomer().build();
		Customer customer2 = CustomerTestDataBuilder.brandNewCustomer().build();

		customers.add(customer1);
		customers.add(customer2);

		Assertions.assertThat(customers.count()).isEqualTo(2L);
	}

	@Test
	void shouldReturnIfCustomerExists() {
		Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
		customers.add(customer);

		Assertions.assertThat(customers.exists(customer.id())).isTrue();
		Assertions.assertThat(customers.exists(new CustomerId())).isFalse();
	}

}
