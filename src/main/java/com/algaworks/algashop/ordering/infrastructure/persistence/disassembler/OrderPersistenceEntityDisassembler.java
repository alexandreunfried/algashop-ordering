package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

	public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
		return Order.existing()
				.id(new OrderId(persistenceEntity.getId()))
				.customerId(new CustomerId(persistenceEntity.getCustomerId()))
				.totalAmount(new Money(persistenceEntity.getTotalAmount()))
				.totalItems(new Quantity(persistenceEntity.getTotalItems()))
				.status(OrderStatus.valueOf(persistenceEntity.getStatus()))
				.paymentMethod(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
				.placedAt(persistenceEntity.getPlacedAt())
				.paidAt(persistenceEntity.getPaidAt())
				.canceledAt(persistenceEntity.getCanceledAt())
				.readyAt(persistenceEntity.getReadyAt())
				.billing(toBillingValueObject(persistenceEntity.getBilling()))
				.shipping(toShippingValueObject(persistenceEntity.getShipping()))
				.items(toDomainEntity(persistenceEntity.getItems()))
				.version(persistenceEntity.getVersion())
				.build();
	}

	private Set<OrderItem> toDomainEntity(Set<OrderItemPersistenceEntity> items) {
		return items.stream()
				.map(this::toDomainEntity)
				.collect(Collectors.toSet());
	}

	private OrderItem toDomainEntity(OrderItemPersistenceEntity persistenceEntity) {
		return OrderItem.existing()
				.id(new OrderItemId(persistenceEntity.getId()))
				.orderId(new OrderId(persistenceEntity.getOrderId()))
				.productId(new ProductId(persistenceEntity.getProductId()))
				.productName(new ProductName(persistenceEntity.getProductName()))
				.price(new Money(persistenceEntity.getPrice()))
				.quantity(new Quantity(persistenceEntity.getQuantity()))
				.totalAmount(new Money(persistenceEntity.getTotalAmount()))
				.build();
	}

	private Shipping toShippingValueObject(ShippingEmbeddable shippingEmbeddable) {

		if (shippingEmbeddable == null) {
			return null;
		}

		RecipientEmbeddable recipientEmbeddable = shippingEmbeddable.getRecipient();
		return Shipping.builder()
				.cost(new Money(shippingEmbeddable.getCost()))
				.expectedDate(shippingEmbeddable.getExpectedDate())
				.recipient(
						Recipient.builder()
								.fullName(new FullName(recipientEmbeddable.getFirstName(), recipientEmbeddable.getLastName()))
								.document(new Document(recipientEmbeddable.getDocument()))
								.phone(new Phone(recipientEmbeddable.getPhone()))
								.build()
				)
				.address(AddressEmbeddableDisassembler.toAddress(shippingEmbeddable.getAddress()))
				.build();
	}

	private Billing toBillingValueObject(BillingEmbeddable billingEmbeddable) {

		if (billingEmbeddable == null) {
			return null;
		}

		return Billing.builder()
				.fullName(new FullName(billingEmbeddable.getFirstName(), billingEmbeddable.getLastName()))
				.document(new Document(billingEmbeddable.getDocument()))
				.phone(new Phone(billingEmbeddable.getPhone()))
				.email(new Email(billingEmbeddable.getEmail()))
				.address(AddressEmbeddableDisassembler.toAddress(billingEmbeddable.getAddress()))
				.build();
	}

}
