package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

	private static final CustomerId ID = new CustomerId();
	private static final FullName FULL_NAME = new FullName("John", "Doe");
	private static final BirthDate BIRTH_DATE = new BirthDate(LocalDate.of(1991, 7, 5));
	private static final Email EMAIL = new Email("john.doe@gmail.com");
	private static final Phone PHONE = new Phone("478-256-2504");
	private static final Document DOCUMENT = new Document("255-08-0578");
	private static final Address ADDRESS = Address.builder()
			.street("Bourbon Street")
			.number("1134")
			.neighborhood("North Ville")
			.city("York")
			.state("South California")
			.zipCode(new ZipCode("12345"))
			.complement("Apt. 114")
			.build();
	private static final OffsetDateTime REGISTERED_AT = OffsetDateTime.now();

	@Test
	void given_invalidEmail_whenTryCreateEmail_shouldGenerateException() {
		Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Email("invalid"));
	}

	@Test
	void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
		Customer customer = new Customer(
				ID,
				FULL_NAME,
				BIRTH_DATE,
				EMAIL,
				PHONE,
				DOCUMENT,
				false,
				REGISTERED_AT,
				ADDRESS
		);

		customer.archive();

		Assertions.assertWith(customer,
				c -> assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
				c -> assertThat(c.email()).isNotEqualTo(new Email("john.doe@gmail.com")),
				c -> assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
				c -> assertThat(c.document()).isEqualTo(new Document("000-00-0000")),
				c -> assertThat(c.birthDate()).isNull(),
				c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse(),
				c -> assertThat(c.address()).isEqualTo(ADDRESS.toBuilder().number("Anonymized").complement(null).build())
		);

	}

	@Test
	void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
		Customer customer = new Customer(
				ID,
				new FullName("Anonymous", "Anonymous"),
				null,
				new Email("anonymous@anonymous.com"),
				new Phone("000-000-0000"),
				new Document("000-00-0000"),
				false,
				true,
				OffsetDateTime.now(),
				OffsetDateTime.now(),
				new LoyaltyPoints(10),
				ADDRESS.toBuilder().number("Anonymized").complement(null).build()
		);

		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(customer::archive);

		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(() -> customer.changeEmail(EMAIL));

		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(() -> customer.changePhone(PHONE));

		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(customer::enablePromotionNotifications);

		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(customer::disablePromotionNotifications);
	}

	@Test
	void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
		Customer customer = new Customer(
				ID,
				FULL_NAME,
				BIRTH_DATE,
				EMAIL,
				PHONE,
				DOCUMENT,
				false,
				REGISTERED_AT,
				ADDRESS
		);

		customer.addLoyaltyPoints(new LoyaltyPoints(10));
		customer.addLoyaltyPoints(new LoyaltyPoints(20));

		Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
	}

	@Test
	void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
		Customer customer = new Customer(
				ID,
				FULL_NAME,
				BIRTH_DATE,
				EMAIL,
				PHONE,
				DOCUMENT,
				false,
				REGISTERED_AT,
				ADDRESS
		);

		Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> customer.addLoyaltyPoints(LoyaltyPoints.ZERO));

		Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new LoyaltyPoints(-10));
	}
}