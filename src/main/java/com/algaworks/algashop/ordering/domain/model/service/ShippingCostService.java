package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import lombok.Builder;

import java.time.LocalDate;

public interface ShippingCostService {

	CalculationResult calculate(CalculationRequest request);

	@Builder
	record CalculationRequest(ZipCode origen, ZipCode destination) {
	}

	@Builder
	record CalculationResult(Money cost, LocalDate expectedDate) {
	}

}
