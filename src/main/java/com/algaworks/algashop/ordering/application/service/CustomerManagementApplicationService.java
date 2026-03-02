package com.algaworks.algashop.ordering.application.service;

import com.algaworks.algashop.ordering.application.model.AddressData;
import com.algaworks.algashop.ordering.application.model.CustomerInput;
import com.algaworks.algashop.ordering.application.model.CustomerOutput;
import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {

	private final CustomerRegistrationService customerRegistration;
	private final Customers customers;

	@Transactional
	public UUID create(CustomerInput input) {
		Objects.requireNonNull(input);
		AddressData addressData = input.getAddress();

		Address address = toAddress(addressData);

		Customer customer = customerRegistration.register(
				new FullName(input.getFirstName(), input.getLastName()),
				new BirthDate(input.getBirthDate()),
				new Email(input.getEmail()),
				new Phone(input.getPhone()),
				new Document(input.getDocument()),
				input.getPromotionNotificationsAllowed(),
				address
		);

		customers.add(customer);

		return customer.id().value();
	}

	@Transactional(readOnly = true)
	public CustomerOutput findById(UUID customerId) {
		Objects.requireNonNull(customerId);
		Customer customer = customers.ofId(new CustomerId(customerId))
				.orElseThrow(CustomerNotFoundException::new);

		return CustomerOutput.builder()
				.id(customer.id().value())
				.firstName(customer.fullName().firstName())
				.lastName(customer.fullName().lastName())
				.email(customer.email().value())
				.document(customer.document().value())
				.phone(customer.phone().value())
				.promotionNotificationsAllowed(customer.isPromotionNotificationsAllowed())
				.loyaltyPoints(customer.loyaltyPoints().value())
				.registeredAt(customer.registeredAt())
				.archived(customer.isArchived())
				.archivedAt(customer.archivedAt())
				.birthDate(customer.birthDate() != null ? customer.birthDate().value() : null)
				.address(toAddressData(customer.address()))
				.build();
	}

	private static Address toAddress(AddressData address) {
		if (address == null) {
			return null;
		}
		return Address.builder()
				.zipCode(new ZipCode(address.getZipCode()))
				.state(address.getState())
				.city(address.getCity())
				.neighborhood(address.getNeighborhood())
				.street(address.getStreet())
				.number(address.getNumber())
				.complement(address.getComplement())
				.build();
	}

	private static AddressData toAddressData(Address address) {
		if (address == null) {
			return null;
		}
		return AddressData.builder()
				.street(address.street())
				.number(address.number())
				.complement(address.complement())
				.neighborhood(address.neighborhood())
				.city(address.city())
				.state(address.state())
				.zipCode(address.zipCode().value())
				.build();
	}

}
