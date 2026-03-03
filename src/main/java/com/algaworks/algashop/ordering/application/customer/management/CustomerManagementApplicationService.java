package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static com.algaworks.algashop.ordering.application.commons.AddressMapper.toAddress;


@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {

	private final CustomerRegistrationService customerRegistration;
	private final Customers customers;
	private final Mapper mapper;

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

		return mapper.convert(customer, CustomerOutput.class);
	}

}
