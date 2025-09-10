package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.entity.CustomerTestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

	@Test
	void given_invalidEmail_whenTryCreateEmail_shouldGenerateException() {
		Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new Email("invalid"));
	}

	@Test
	void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
		Customer customer = existingCustomer().build();

		customer.archive();

		Assertions.assertWith(customer,
				c -> assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
				c -> assertThat(c.email()).isNotEqualTo(new Email("john.doe@gmail.com")),
				c -> assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
				c -> assertThat(c.document()).isEqualTo(new Document("000-00-0000")),
				c -> assertThat(c.birthDate()).isNull(),
				c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse(),
				c -> assertThat(c.address()).isEqualTo(brandNewAddress().number("Anonymized").complement(null).build())
		);

	}

	@Test
	void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
		Customer customer = existingArchivedCustomer().build();

		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(customer::archive);

		Email email = new Email("john.doe@gmail.com");
		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(() -> customer.changeEmail(email));

		Phone phone = new Phone("478-256-2504");
		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(() -> customer.changePhone(phone));

		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(customer::enablePromotionNotifications);

		Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
				.isThrownBy(customer::disablePromotionNotifications);
	}

	@Test
	void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
		Customer customer = brandNewCustomer().build();

		customer.addLoyaltyPoints(new LoyaltyPoints(10));
		customer.addLoyaltyPoints(new LoyaltyPoints(20));

		Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
	}

	@Test
	void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
		Customer customer = brandNewCustomer().build();

		Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> customer.addLoyaltyPoints(LoyaltyPoints.ZERO));

		Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new LoyaltyPoints(-10));
	}

}