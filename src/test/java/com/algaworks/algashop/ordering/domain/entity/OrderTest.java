package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static com.algaworks.algashop.ordering.domain.entity.ProductTestDataBuilder.*;

class OrderTest {

	@Test
	void shouldGenerate() {
		Order order = Order.draft(new CustomerId());
		Assertions.assertThat(order).isNotNull();
	}

	@Test
	void shouldAddItem() {
		Order order = Order.draft(new CustomerId());

		Product product = aProductAltMousePad().build();
		ProductId productId = product.id();
		order.addItem(
				product,
				new Quantity(1)
		);

		Assertions.assertThat(order.items()).hasSize(1);

		OrderItem orderItem = order.items().iterator().next();
		Assertions.assertWith(orderItem,
				i -> Assertions.assertThat(i.id()).isNotNull(),
				i -> Assertions.assertThat(i.productName()).isEqualTo(new ProductName("Mouse Pad")),
				i -> Assertions.assertThat(i.productId()).isEqualTo(productId),
				i -> Assertions.assertThat(i.price()).isEqualTo(new Money("100")),
				i -> Assertions.assertThat(i.quantity()).isEqualTo(new Quantity(1))
		);

	}

	@Test
	void shouldGenerateExceptionWhenTryToChangeItemSet() {
		Order order = Order.draft(new CustomerId());
		Product product = aProductAltMousePad().build();

		order.addItem(
				product,
				new Quantity(1)
		);

		Set<OrderItem> items = order.items();
		Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
				.isThrownBy(items::clear);
	}

	@Test
	void shouldCalculateTotals() {
		Order order = Order.draft(new CustomerId());

		order.addItem(
				aProductAltMousePad().build(),
				new Quantity(2)
		);

		order.addItem(
				aProductAltRamMemory().build(),
				new Quantity(1)
		);

		Set<OrderItem> items = order.items();
		Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("400"));
		Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(3));

		Assertions.assertThat(items)
				.anyMatch(i -> i.totalAmount().equals(new Money("200"))
						&& i.productName().equals(new ProductName("4GB RAM")))
				.anyMatch(i -> i.totalAmount().equals(new Money("200"))
						&& i.productName().equals(new ProductName("Mouse Pad")));

	}

	@Test
	void givenDraftOrder_whenPlace_shouldChangeToPlaced() {
		Order order = OrderTestDataBuilder.anOrder().build();
		order.place();
		Assertions.assertThat(order.isPlaced()).isTrue();
	}

	@Test
	void givenPlacedOrder_whenMarkAsPaid_shouldChangeToPaid() {
		Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
		order.markAsPaid();
		Assertions.assertThat(order.isPaid()).isTrue();
		Assertions.assertThat(order.paidAt()).isNotNull();
	}

	@Test
	void givenPlacedOrder_whenTryToPlace_shouldGenerateException() {
		Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

		Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
				.isThrownBy(order::place);
	}

	@Test
	void givenDraftOrder_whenChangePaymentMethod_shouldAllowChange() {
		Order order = Order.draft(new CustomerId());
		order.changePaymentMethod(PaymentMethod.CREDIT_CARD);
		Assertions.assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
	}

	@Test
	void giverDraftOrder_whenChangeBillingInfo_shouldAllowChange() {
		Address address = Address.builder()
				.street("Bourbon Street")
				.number("1234")
				.neighborhood("North Ville")
				.complement("apt. 11")
				.city("Montfort")
				.state("South Carolina")
				.zipCode(new ZipCode("79911"))
				.build();

		BillingInfo billingInfo = BillingInfo.builder()
				.address(address)
				.document(new Document("225-09-1992"))
				.phone(new Phone("123-111-9911"))
				.fullName(new FullName("John", "Doe"))
				.build();

		Order order = Order.draft(new CustomerId());
		order.changeBilling(billingInfo);

		Assertions.assertThat(order.billing()).isEqualTo(billingInfo);
	}

	@Test
	void givenDraftOrder_whenChangeShippingInfo_shouldAllowChange() {
		Address address = Address.builder()
				.street("Bourbon Street")
				.number("1234")
				.neighborhood("North Ville")
				.complement("apt. 11")
				.city("Montfort")
				.state("South Carolina")
				.zipCode(new ZipCode("79911"))
				.build();

		ShippingInfo shippingInfo = ShippingInfo.builder()
				.address(address)
				.fullName(new FullName("John", "Doe"))
				.document(new Document("112-33-2321"))
				.phone(new Phone("111-441-1244"))
				.build();

		Order order = Order.draft(new CustomerId());
		Money shippingCost = Money.ZERO;
		LocalDate expectedDeliveryDate = LocalDate.now().plusDays(1);

		order.changeShipping(shippingInfo, shippingCost, expectedDeliveryDate);

		Assertions.assertWith(order,
				o -> Assertions.assertThat(o.shipping()).isEqualTo(shippingInfo),
				o -> Assertions.assertThat(o.shippingCost()).isEqualTo(shippingCost),
				o -> Assertions.assertThat(o.expectedDeliveryDate()).isEqualTo(expectedDeliveryDate)
		);
	}

	@Test
	void givenDraftOrderAndDeliveryDateInThePast_whenChangeShippingInfo_shouldNotAllowChange() {
		Address address = Address.builder()
				.street("Bourbon Street")
				.number("1234")
				.neighborhood("North Ville")
				.complement("apt. 11")
				.city("Montfort")
				.state("South Carolina")
				.zipCode(new ZipCode("79911"))
				.build();

		ShippingInfo shippingInfo = ShippingInfo.builder()
				.address(address)
				.fullName(new FullName("John", "Doe"))
				.document(new Document("112-33-2321"))
				.phone(new Phone("111-441-1244"))
				.build();

		Order order = Order.draft(new CustomerId());
		Money shippingCost = Money.ZERO;
		LocalDate expectedDeliveryDate = LocalDate.now().minusDays(2);

		Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
				.isThrownBy(() -> order.changeShipping(shippingInfo, shippingCost, expectedDeliveryDate));

	}

	@Test
	void givenDraftOrder_whenChangeItem_shouldRecalculate() {
		Order order = Order.draft(new CustomerId());

		order.addItem(
				aProductAltMousePad().build(),
				new Quantity(3)
		);

		OrderItem orderItem = order.items().iterator().next();

		order.changeItemQuantity(orderItem.id(), new Quantity(5));

		Assertions.assertWith(order,
				o -> Assertions.assertThat(o.totalAmount()).isEqualTo(new Money("500")),
				o -> Assertions.assertThat(o.totalItems()).isEqualTo(new Quantity(5))
		);
	}

	@Test
	void givenOutOfStockProduct_whenTryToAddToAnOrder_shouldNotAllow() {
		Order order = Order.draft(new CustomerId());

		ThrowableAssert.ThrowingCallable addItemTask = () -> order.addItem(
				aProductUnavailable().build(),
				new Quantity(1)
		);

		Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
				.isThrownBy(addItemTask);
	}

}