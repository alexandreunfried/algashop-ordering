package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
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

	private Billing billing;
	private Shipping shipping;

	private OrderStatus status;
	private PaymentMethod paymentMethod;

	private Set<OrderItem> items;

	@Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
	public Order(OrderId id, CustomerId customerId, Money totalAmount,
				 Quantity totalItems, OffsetDateTime placedAt,
				 OffsetDateTime paidAt, OffsetDateTime canceledAt,
				 OffsetDateTime readyAt, Billing billing,
				 Shipping shipping, OrderStatus status, PaymentMethod paymentMethod, Set<OrderItem> items) {
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
				new HashSet<>()
		);
	}

	public void addItem(Product product, Quantity quantity) {
		Objects.requireNonNull(product);
		Objects.requireNonNull(quantity);

		verifyIfChangeable();

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

	public void markAsReady() {
		changeStatus(OrderStatus.READY);
		setReadyAt(OffsetDateTime.now());
	}

	public void changePaymentMethod(PaymentMethod paymentMethod) {
		Objects.requireNonNull(paymentMethod);
		verifyIfChangeable();
		setPaymentMethod(paymentMethod);
	}

	public void changeBilling(Billing billing) {
		Objects.requireNonNull(billing);
		verifyIfChangeable();
		setBilling(billing);
	}

	public void changeShipping(Shipping newShipping) {
		Objects.requireNonNull(newShipping);

		verifyIfChangeable();

		if (newShipping.expectedDate().isBefore(LocalDate.now())) {
			throw new OrderInvalidShippingDeliveryDateException(id());
		}

		setShipping(newShipping);
		recalculateTotals();
	}

	public void changeItemQuantity(OrderItemId orderItemId, Quantity quantity) {
		Objects.requireNonNull(orderItemId);
		Objects.requireNonNull(quantity);

		verifyIfChangeable();

		OrderItem orderItem = findOrderItem(orderItemId);
		orderItem.changeQuantity(quantity);

		recalculateTotals();
	}

	public void removeItem(OrderItemId orderItemId) {
		Objects.requireNonNull(orderItemId);
		verifyIfChangeable();

		OrderItem orderItem = findOrderItem(orderItemId);
		items.remove(orderItem);

		recalculateTotals();
	}

	public void cancel() {
		changeStatus(OrderStatus.CANCELED);
		setCanceledAt(OffsetDateTime.now());
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

	public boolean isReady() {
		return OrderStatus.READY.equals(status());
	}

	public boolean isCanceled() {
		return OrderStatus.CANCELED.equals(status());
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

	public Billing billing() {
		return billing;
	}

	public Shipping shipping() {
		return shipping;
	}

	public OrderStatus status() {
		return status;
	}

	public PaymentMethod paymentMethod() {
		return paymentMethod;
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
		if (shipping() == null) {
			shippingCost = BigDecimal.ZERO;
		} else {
			shippingCost = shipping().cost().value();
		}

		BigDecimal totalAmountRecalculated = totalItemsAmount.add(shippingCost);

		setTotalAmount(new Money(totalAmountRecalculated));
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

	private void verifyIfChangeable() {
		if (!isDraft()) {
			throw new OrderCannotBeEditedException(id(), status());
		}
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

	private void setBilling(Billing billing) {
		this.billing = billing;
	}

	private void setShipping(Shipping shipping) {
		this.shipping = shipping;
	}

	private void setStatus(OrderStatus status) {
		Objects.requireNonNull(status);
		this.status = status;
	}

	private void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
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
