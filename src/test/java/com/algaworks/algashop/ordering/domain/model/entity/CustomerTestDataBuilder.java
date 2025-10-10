package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CustomerTestDataBuilder {

	private CustomerTestDataBuilder() {

	}

	private static final FullName FULL_NAME = new FullName("John", "Doe");
	private static final BirthDate BIRTH_DATE = new BirthDate(LocalDate.of(1991, 7, 5));
	private static final Email EMAIL = new Email("john.doe@gmail.com");
	private static final Phone PHONE = new Phone("478-256-2504");
	private static final Document DOCUMENT = new Document("255-08-0578");

	public static Customer.BrandNewCustomerBuild brandNewCustomer() {
		return Customer.brandNew()
				.fullName(FULL_NAME)
				.birthDate(BIRTH_DATE)
				.email(EMAIL)
				.phone(PHONE)
				.document(DOCUMENT)
				.promotionNotificationsAllowed(false)
				.address(brandNewAddress().build());
	}

	public static Customer.ExistingCustomerBuild existingCustomer() {
		return Customer.existing()
				.id(new CustomerId())
				.fullName(FULL_NAME)
				.birthDate(BIRTH_DATE)
				.email(EMAIL)
				.phone(PHONE)
				.document(DOCUMENT)
				.promotionNotificationsAllowed(true)
				.archived(false)
				.registeredAt(OffsetDateTime.now())
				.archivedAt(null)
				.loyaltyPoints(LoyaltyPoints.ZERO)
				.address(brandNewAddress().build());
	}

	public static Customer.ExistingCustomerBuild existingArchivedCustomer() {
		return Customer.existing()
				.id(new CustomerId())
				.fullName(new FullName("Anonymous", "Anonymous"))
				.birthDate(null)
				.email(new Email("anonymous@anonymous.com"))
				.phone(new Phone("000-000-0000"))
				.document(new Document("000-00-0000"))
				.promotionNotificationsAllowed(false)
				.archived(true)
				.registeredAt(OffsetDateTime.now())
				.archivedAt(OffsetDateTime.now())
				.loyaltyPoints(new LoyaltyPoints(10))
				.address(brandNewAddress().number("Anonymized").complement(null).build());
	}

	public static Address.AddressBuilder brandNewAddress() {
		return Address.builder()
				.street("Bourbon Street")
				.number("1134")
				.neighborhood("North Ville")
				.city("York")
				.state("South California")
				.zipCode(new ZipCode("12345"))
				.complement("Apt. 114");
	}

}
