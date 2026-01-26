package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.model.FieldValidations;

public record ProductName(String value) {

	public ProductName {
		FieldValidations.requiresNonBlank(value);
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public String toString() {
		return value;
	}

}
