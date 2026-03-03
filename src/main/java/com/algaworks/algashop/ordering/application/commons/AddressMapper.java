package com.algaworks.algashop.ordering.application.commons;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;

public class AddressMapper {

	private AddressMapper() {
	}

	public static Address toAddress(AddressData address) {
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
