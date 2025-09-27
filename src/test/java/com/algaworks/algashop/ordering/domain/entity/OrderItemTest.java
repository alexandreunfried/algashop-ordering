package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.entity.ProductTestDataBuilder.aProductAltMousePad;

class OrderItemTest {

	@Test
	void shouldGenerate() {
		OrderItem orderItem = OrderItem.brandNew()
				.product(aProductAltMousePad().build())
				.quantity(new Quantity(1))
				.orderId(new OrderId())
				.build();

		Assertions.assertThat(orderItem).isNotNull();
	}

}