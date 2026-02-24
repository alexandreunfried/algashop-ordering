package com.algaworks.algashop.ordering.application.service;

import com.algaworks.algashop.ordering.application.model.AddressData;
import com.algaworks.algashop.ordering.application.model.CustomerInput;
import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegistrationService;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
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

		Address address = toDomain(addressData);

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

	private static Address toDomain(AddressData address) {
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

}
