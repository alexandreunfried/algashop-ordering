package com.algaworks.algashop.ordering.domain.model.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderStatusTest {

	@Test
	void canChangeTo() {
		Assertions.assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.PLACED)).isTrue();
		Assertions.assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.PAID)).isFalse();
		Assertions.assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.READY)).isFalse();
		Assertions.assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.CANCELED)).isTrue();

		Assertions.assertThat(OrderStatus.PLACED.canChangeTo(OrderStatus.DRAFT)).isFalse();
		Assertions.assertThat(OrderStatus.PLACED.canChangeTo(OrderStatus.PAID)).isTrue();
		Assertions.assertThat(OrderStatus.PLACED.canChangeTo(OrderStatus.READY)).isFalse();
		Assertions.assertThat(OrderStatus.PLACED.canChangeTo(OrderStatus.CANCELED)).isTrue();

		Assertions.assertThat(OrderStatus.PAID.canChangeTo(OrderStatus.DRAFT)).isFalse();
		Assertions.assertThat(OrderStatus.PAID.canChangeTo(OrderStatus.PLACED)).isFalse();
		Assertions.assertThat(OrderStatus.PAID.canChangeTo(OrderStatus.READY)).isTrue();
		Assertions.assertThat(OrderStatus.PAID.canChangeTo(OrderStatus.CANCELED)).isTrue();

		Assertions.assertThat(OrderStatus.READY.canChangeTo(OrderStatus.DRAFT)).isFalse();
		Assertions.assertThat(OrderStatus.READY.canChangeTo(OrderStatus.PLACED)).isFalse();
		Assertions.assertThat(OrderStatus.READY.canChangeTo(OrderStatus.PAID)).isFalse();
		Assertions.assertThat(OrderStatus.READY.canChangeTo(OrderStatus.CANCELED)).isTrue();

		Assertions.assertThat(OrderStatus.CANCELED.canChangeTo(OrderStatus.DRAFT)).isFalse();
		Assertions.assertThat(OrderStatus.CANCELED.canChangeTo(OrderStatus.PLACED)).isFalse();
		Assertions.assertThat(OrderStatus.CANCELED.canChangeTo(OrderStatus.PAID)).isFalse();
		Assertions.assertThat(OrderStatus.CANCELED.canChangeTo(OrderStatus.READY)).isFalse();
	}

	@Test
	void canNotChangeTo() {
		Assertions.assertThat(OrderStatus.DRAFT.canNotChangeTo(OrderStatus.PLACED)).isFalse();
		Assertions.assertThat(OrderStatus.DRAFT.canNotChangeTo(OrderStatus.PAID)).isTrue();
		Assertions.assertThat(OrderStatus.DRAFT.canNotChangeTo(OrderStatus.READY)).isTrue();
		Assertions.assertThat(OrderStatus.DRAFT.canNotChangeTo(OrderStatus.CANCELED)).isFalse();

		Assertions.assertThat(OrderStatus.PLACED.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
		Assertions.assertThat(OrderStatus.PLACED.canNotChangeTo(OrderStatus.PAID)).isFalse();
		Assertions.assertThat(OrderStatus.PLACED.canNotChangeTo(OrderStatus.READY)).isTrue();
		Assertions.assertThat(OrderStatus.PLACED.canNotChangeTo(OrderStatus.CANCELED)).isFalse();

		Assertions.assertThat(OrderStatus.PAID.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
		Assertions.assertThat(OrderStatus.PAID.canNotChangeTo(OrderStatus.PLACED)).isTrue();
		Assertions.assertThat(OrderStatus.PAID.canNotChangeTo(OrderStatus.READY)).isFalse();
		Assertions.assertThat(OrderStatus.PAID.canNotChangeTo(OrderStatus.CANCELED)).isFalse();

		Assertions.assertThat(OrderStatus.READY.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
		Assertions.assertThat(OrderStatus.READY.canNotChangeTo(OrderStatus.PLACED)).isTrue();
		Assertions.assertThat(OrderStatus.READY.canNotChangeTo(OrderStatus.PAID)).isTrue();
		Assertions.assertThat(OrderStatus.READY.canNotChangeTo(OrderStatus.CANCELED)).isFalse();

		Assertions.assertThat(OrderStatus.CANCELED.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
		Assertions.assertThat(OrderStatus.CANCELED.canNotChangeTo(OrderStatus.PLACED)).isTrue();
		Assertions.assertThat(OrderStatus.CANCELED.canNotChangeTo(OrderStatus.PAID)).isTrue();
		Assertions.assertThat(OrderStatus.CANCELED.canNotChangeTo(OrderStatus.READY)).isTrue();
	}

}