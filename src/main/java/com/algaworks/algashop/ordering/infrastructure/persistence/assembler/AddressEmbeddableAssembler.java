package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;

public class AddressEmbeddableAssembler {

	private AddressEmbeddableAssembler() {
	}

	public static AddressEmbeddable toAddressEmbeddable(Address address) {

		if (address == null) {
			return null;
		}

		return AddressEmbeddable.builder()
				.city(address.city())
				.state(address.state())
				.number(address.number())
				.street(address.street())
				.complement(address.complement())
				.neighborhood(address.neighborhood())
				.zipCode(address.zipCode().value())
				.build();
	}

}
