package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
class CustomerManagementApplicationServiceIT {

	@Autowired
	private CustomerManagementApplicationService customerManagementApplicationService;

	@Test
	void shouldRegister() {
		CustomerInput input = CustomerInput.builder()
				.firstName("John")
				.lastName("Doe")
				.birthDate(LocalDate.of(1991, 7, 5))
				.document("255-08-0578")
				.phone("478-256-2604")
				.email("johndoe@email.com")
				.promotionNotificationsAllowed(false)
				.address(AddressData.builder()
						.street("Bourbon Street")
						.number("1200")
						.complement("Apt. 901")
						.neighborhood("North Ville")
						.city("Yostfort")
						.state("South Carolina")
						.zipCode("70283")
						.build())
				.build();

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

}