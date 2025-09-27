package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderCannotBePlacedException;
import com.algaworks.algashop.ordering.domain.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderItemId;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Order {

	private OrderId id;
	private CustomerId customerId;

	private Money totalAmount;
	private Quantity totalItems;

	private OffsetDateTime placedAt;
	private OffsetDateTime paidAt;
	private OffsetDateTime canceledAt;
	private OffsetDateTime readyAt;

	private BillingInfo billing;
	private ShippingInfo shipping;

	private OrderStatus status;
	private PaymentMethod paymentMethod;

	private Money shippingCost;
	private LocalDate expectedDeliveryDate;

	private Set<OrderItem> items;

	@Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
	public Order(OrderId id, CustomerId customerId, Money totalAmount,
				 Quantity totalItems, OffsetDateTime placedAt,
				 OffsetDateTime paidAt, OffsetDateTime canceledAt,
				 OffsetDateTime readyAt, BillingInfo billing,
				 ShippingInfo shipping, OrderStatus status, PaymentMethod paymentMethod,
				 Money shippingCost, LocalDate expectedDeliveryDate, Set<OrderItem> items) {
		setId(id);
		setCustomerId(customerId);
		setTotalAmount(totalAmount);
		setTotalItems(totalItems);
		setPlacedAt(placedAt);
		setPaidAt(paidAt);
		setCanceledAt(canceledAt);
		setReadyAt(readyAt);
		setBilling(billing);
		setShipping(shipping);
		setStatus(status);
		setPaymentMethod(paymentMethod);
		setShippingCost(shippingCost);
		setExpectedDeliveryDate(expectedDeliveryDate);
		setItems(items);
	}

	public static Order draft(CustomerId customerId) {
		return new Order(
				new OrderId(),
				customerId,
				Money.ZERO,
				Quantity.ZERO,
				null,
				null,
				null,
				null,
				null,
				null,
				OrderStatus.DRAFT,
				null,
				null,
				null,
				new HashSet<>()
		);
	}

	public void addItem(Product product, Quantity quantity) {
		Objects.requireNonNull(product);
		Objects.requireNonNull(quantity);

		product.checkOutOfStock();

		OrderItem orderItem = OrderItem.brandNew()
				.orderId(id())
				.product(product)
				.quantity(quantity)
				.build();

		if (items == null) {
			this.items = new HashSet<>();
		}

		items.add(orderItem);

		recalculateTotals();
	}

	public void place() {
		verifyIfCanChangeToPlaced();
		changeStatus(OrderStatus.PLACED);
		setPlacedAt(OffsetDateTime.now());
	}

	public void markAsPaid() {
		changeStatus(OrderStatus.PAID);
		setPaidAt(OffsetDateTime.now());
	}

	public void changePaymentMethod(PaymentMethod paymentMethod) {
		Objects.requireNonNull(paymentMethod);
		setPaymentMethod(paymentMethod);
	}

	public void changeShipping(ShippingInfo shipping, Money shippingCost, LocalDate expectedDeliveryDate) {
		Objects.requireNonNull(shipping);
		Objects.requireNonNull(shippingCost);
		Objects.requireNonNull(expectedDeliveryDate);

		if (expectedDeliveryDate.isBefore(LocalDate.now())) {
			throw new OrderInvalidShippingDeliveryDateException(id());
		}

		setShipping(shipping);
		setShippingCost(shippingCost);
		setExpectedDeliveryDate(expectedDeliveryDate);
	}

	public void changeItemQuantity(OrderItemId orderItemId, Quantity quantity) {
		Objects.requireNonNull(orderItemId);
		Objects.requireNonNull(quantity);

		OrderItem orderItem = findOrderItem(orderItemId);
		orderItem.changeQuantity(quantity);

		recalculateTotals();
	}

	public void changeBilling(BillingInfo billing) {
		Objects.requireNonNull(billing);
		setBilling(billing);
	}

	public boolean isDraft() {
		return OrderStatus.DRAFT.equals(status());
	}

	public boolean isPlaced() {
		return OrderStatus.PLACED.equals(status());
	}

	public boolean isPaid() {
		return OrderStatus.PAID.equals(status());
	}

	public OrderId id() {
		return id;
	}

	public CustomerId customerId() {
		return customerId;
	}

	public Money totalAmount() {
		return totalAmount;
	}

	public Quantity totalItems() {
		return totalItems;
	}

	public OffsetDateTime placedAt() {
		return placedAt;
	}

	public OffsetDateTime paidAt() {
		return paidAt;
	}

	public OffsetDateTime canceledAt() {
		return canceledAt;
	}

	public OffsetDateTime readyAt() {
		return readyAt;
	}

	public BillingInfo billing() {
		return billing;
	}

	public ShippingInfo shipping() {
		return shipping;
	}

	public OrderStatus status() {
		return status;
	}

	public PaymentMethod paymentMethod() {
		return paymentMethod;
	}

	public Money shippingCost() {
		return shippingCost;
	}

	public LocalDate expectedDeliveryDate() {
		return expectedDeliveryDate;
	}

	public Set<OrderItem> items() {
		return Collections.unmodifiableSet(items);
	}

	private void recalculateTotals() {
		BigDecimal totalItemsAmount = items().stream()
				.map(i -> i.totalAmount().value())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Integer totalItemsQuantity = items.stream()
				.map(i -> i.quantity().value())
				.reduce(0, Integer::sum);

		BigDecimal shippingCost;
		if (shippingCost() == null) {
			shippingCost = BigDecimal.ZERO;
		} else {
			shippingCost = this.shippingCost.value();
		}

		BigDecimal totalAmount = totalItemsAmount.add(shippingCost);

		setTotalAmount(new Money(totalAmount));
		setTotalItems(new Quantity(totalItemsQuantity));
	}

	private void changeStatus(OrderStatus newStatus) {
		Objects.requireNonNull(newStatus);
		if (status().canNotChangeTo(newStatus)) {
			throw new OrderStatusCannotBeChangedException(id(), status(), newStatus);
		}

		setStatus(newStatus);
	}

	private void verifyIfCanChangeToPlaced() {
		if (shipping() == null) {
			throw OrderCannotBePlacedException.noShippingInfo(id());
		}
		if (billing() == null) {
			throw OrderCannotBePlacedException.noBillingInfo(id());
		}
		if (paymentMethod() == null) {
			throw OrderCannotBePlacedException.noPaymentMethod(id());
		}
		if (shippingCost() == null) {
			throw OrderCannotBePlacedException.invalidShippingCost(id());
		}
		if (expectedDeliveryDate() == null) {
			throw OrderCannotBePlacedException.invalidExpectedDeliveryDate(id());
		}
		if (items() == null || items().isEmpty()) {
			throw OrderCannotBePlacedException.noItems(id());
		}
	}

	private OrderItem findOrderItem(OrderItemId orderItemId) {
		Objects.requireNonNull(orderItemId);
		return items().stream()
				.filter(i -> i.id().equals(orderItemId))
				.findFirst()
				.orElseThrow(() -> new OrderDoesNotContainOrderItemException(id(), orderItemId));
	}

	private void setId(OrderId id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	private void setCustomerId(CustomerId customerId) {
		Objects.requireNonNull(customerId);
		this.customerId = customerId;
	}

	private void setTotalAmount(Money totalAmount) {
		Objects.requireNonNull(totalAmount);
		this.totalAmount = totalAmount;
	}

	private void setTotalItems(Quantity totalItems) {
		Objects.requireNonNull(totalItems);
		this.totalItems = totalItems;
	}

	private void setPlacedAt(OffsetDateTime placedAt) {
		this.placedAt = placedAt;
	}

	private void setPaidAt(OffsetDateTime paidAt) {
		this.paidAt = paidAt;
	}

	private void setCanceledAt(OffsetDateTime canceledAt) {
		this.canceledAt = canceledAt;
	}

	private void setReadyAt(OffsetDateTime readyAt) {
		this.readyAt = readyAt;
	}

	private void setBilling(BillingInfo billing) {
		this.billing = billing;
	}

	private void setShipping(ShippingInfo shipping) {
		this.shipping = shipping;
	}

	private void setStatus(OrderStatus status) {
		Objects.requireNonNull(status);
		this.status = status;
	}

	private void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	private void setShippingCost(Money shippingCost) {
		this.shippingCost = shippingCost;
	}

	private void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	private void setItems(Set<OrderItem> items) {
		Objects.requireNonNull(items);
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;

		Order order = (Order) o;
		return Objects.equals(id, order.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
