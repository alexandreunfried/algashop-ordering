package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.utility.IdGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class CustomerTest {

	private static final UUID ID = IdGenerator.generateTimeBasedUUID();
	private static final LocalDate BIRTH_DATE = LocalDate.of(1991, 7, 5);
	private static final OffsetDateTime REGISTERED_AT = OffsetDateTime.now();

	@Test
	void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
						new Customer(
								ID,
								"John Doe",
								BIRTH_DATE,
								"invalid",
								"478-256-2504",
								"255-08-0578",
								false,
								REGISTERED_AT
						)
				);
	}

	@Test
	void given_invalidEmail_whenTryUpdatedCustomerEmail_shouldGenerateException() {
		Customer customer = new Customer(
				ID,
				"John Doe",
				BIRTH_DATE,
				"john.doe@gmail.com",
				"478-256-2504",
				"255-08-0578",
				false,
				REGISTERED_AT
		);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> customer.changeEmail("invalid"));
	}

	@Test
	void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
		Customer customer = new Customer(
				ID,
				"John Doe",
				BIRTH_DATE,
				"john.doe@gmail.com",
				"478-256-2504",
				"255-08-0578",
				true,
				REGISTERED_AT
		);

		customer.archive();

		assertWith(customer,
				c -> assertThat(c.fullName()).isEqualTo("Anonymous"),
				c -> assertThat(c.email()).isNotEqualTo("john.doe@gmail.com"),
				c -> assertThat(c.phone()).isEqualTo("000-000-0000"),
				c -> assertThat(c.document()).isEqualTo("000-00-0000"),
				c -> assertThat(c.birthDate()).isNull(),
				c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse()
		);
	}

	@Test
	void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
		Customer customer = new Customer(
				ID,
				"Anonymous",
				null,
				"anonymous@anonymous.com",
				"000-000-0000",
				"000-00-0000",
				false,
				true,
				REGISTERED_AT,
				REGISTERED_AT,
				10
		);

		assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(customer::archive);

		assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(() -> customer.changeEmail("email@gmail.com"));

		assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(() -> customer.changePhone("123-123-1111"));

		assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(customer::enablePromotionNotifications);

		assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(customer::disablePromotionNotifications);

		assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(() -> customer.addLoyaltyPoints(10));
	}

	@Test
	void given_brandNewCustomer_whenAddLoyaltyPoints_shoulSumPoints() {
		Customer customer = new Customer(
				ID,
				"John Doe",
				BIRTH_DATE,
				"john.doe@gmail.com",
				"478-256-2504",
				"255-08-0578",
				true,
				REGISTERED_AT
		);

		customer.addLoyaltyPoints(10);
		customer.addLoyaltyPoints(20);

		assertThat(customer.loyaltyPoints()).isEqualTo(30);
	}

	@Test
	void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shoulGenerateException() {
		Customer customer = new Customer(
				ID,
				"John Doe",
				BIRTH_DATE,
				"john.doe@gmail.com",
				"478-256-2504",
				"255-08-0578",
				true,
				REGISTERED_AT
		);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> customer.addLoyaltyPoints(0));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> customer.addLoyaltyPoints(-10));

	}

}