package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;

public class AddressEmbeddableDisassembler {

	private AddressEmbeddableDisassembler() {
	}

	public static Address toAddress(AddressEmbeddable address) {

		if (address == null) {
			return null;
		}

		return Address.builder()
				.street(address.getStreet())
				.number(address.getNumber())
				.complement(address.getComplement())
				.neighborhood(address.getNeighborhood())
				.city(address.getCity())
				.state(address.getState())
				.zipCode(new ZipCode(address.getZipCode()))
				.build();
	}

}
