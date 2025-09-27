package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.entity.ProductTestDataBuilder.aProductAltMousePad;

class OrderItemTest {

	@Test
	void shouldGenerateBrandNewOrderItem() {
		Product product = aProductAltMousePad().build();
		Quantity quantity = new Quantity(1);
		OrderId orderId = new OrderId();

		OrderItem orderItem = OrderItem.brandNew()
				.product(product)
				.quantity(quantity)
				.orderId(orderId)
				.build();

		Assertions.assertWith(orderItem,
				i -> Assertions.assertThat(i.id()).isNotNull(),
				i -> Assertions.assertThat(i.productId()).isEqualTo(product.id()),
				i -> Assertions.assertThat(i.productName()).isEqualTo(product.name()),
				i -> Assertions.assertThat(i.price()).isEqualTo(product.price()),
				i -> Assertions.assertThat(i.quantity()).isEqualTo(quantity),
				i -> Assertions.assertThat(i.orderId()).isEqualTo(orderId)
		);
	}

}