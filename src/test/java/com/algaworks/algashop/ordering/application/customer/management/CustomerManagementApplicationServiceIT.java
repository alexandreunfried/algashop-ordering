package com.algaworks.algashop.ordering.application.customer.management;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

	@Autowired
	private CustomerManagementApplicationService customerManagementApplicationService;

	@Test
	void shouldRegister() {
		CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();

		UUID customerId = customerManagementApplicationService.create(input);
		Assertions.assertThat(customerId).isNotNull();

		CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

		Assertions.assertThat(customerOutput).isNotNull();
		Assertions.assertThat(customerOutput.getId()).isEqualTo(customerId);
		Assertions.assertThat(customerOutput.getFirstName()).isEqualTo("John");
		Assertions.assertThat(customerOutput.getLastName()).isEqualTo("Doe");
		Assertions.assertThat(customerOutput.getBirthDate()).isEqualTo(LocalDate.of(1991, 7, 5));
		Assertions.assertThat(customerOutput.getPhone()).isEqualTo("478-256-2604");
		Assertions.assertThat(customerOutput.getEmail()).isEqualTo("johndoe@email.com");
		Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
		Assertions.assertThat(customerOutput.getPromotionNotificationsAllowed()).isFalse();
		Assertions.assertThat(customerOutput.getAddress()).isNotNull();
		Assertions.assertThat(customerOutput.getAddress().getStreet()).isEqualTo("Bourbon Street");
		Assertions.assertThat(customerOutput.getAddress().getNumber()).isEqualTo("1200");
		Assertions.assertThat(customerOutput.getAddress().getComplement()).isEqualTo("Apt. 901");
		Assertions.assertThat(customerOutput.getAddress().getNeighborhood()).isEqualTo("North Ville");
		Assertions.assertThat(customerOutput.getAddress().getCity()).isEqualTo("Yostfort");
		Assertions.assertThat(customerOutput.getAddress().getState()).isEqualTo("South Carolina");
		Assertions.assertThat(customerOutput.getAddress().getZipCode()).isEqualTo("70283");
	}

	@Test
	void shouldUpdate() {
		CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
		CustomerUpdateInput updateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate().build();

		UUID customerId = customerManagementApplicationService.create(input);
		Assertions.assertThat(customerId).isNotNull();

		customerManagementApplicationService.update(customerId, updateInput);

		CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

		Assertions.assertThat(customerOutput).isNotNull();
		Assertions.assertThat(customerOutput.getId()).isEqualTo(customerId);
		Assertions.assertThat(customerOutput.getFirstName()).isEqualTo("Matt");
		Assertions.assertThat(customerOutput.getLastName()).isEqualTo("Damon");
		Assertions.assertThat(customerOutput.getBirthDate()).isEqualTo(LocalDate.of(1991, 7, 5));
		Assertions.assertThat(customerOutput.getPhone()).isEqualTo("123-321-1112");
		Assertions.assertThat(customerOutput.getEmail()).isEqualTo("johndoe@email.com");
		Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
		Assertions.assertThat(customerOutput.getPromotionNotificationsAllowed()).isTrue();
		Assertions.assertThat(customerOutput.getAddress()).isNotNull();
		Assertions.assertThat(customerOutput.getAddress().getStreet()).isEqualTo("Amphitheatre Parkway");
		Assertions.assertThat(customerOutput.getAddress().getNumber()).isEqualTo("1600");
		Assertions.assertThat(customerOutput.getAddress().getComplement()).isEmpty();
		Assertions.assertThat(customerOutput.getAddress().getNeighborhood()).isEqualTo("Mountain View");
		Assertions.assertThat(customerOutput.getAddress().getCity()).isEqualTo("Mountain View");
		Assertions.assertThat(customerOutput.getAddress().getState()).isEqualTo("California");
		Assertions.assertThat(customerOutput.getAddress().getZipCode()).isEqualTo("94043");
	}

}