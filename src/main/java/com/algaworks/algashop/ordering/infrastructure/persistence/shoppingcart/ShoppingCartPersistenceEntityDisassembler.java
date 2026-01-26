package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShoppingCartPersistenceEntityDisassembler {

	public ShoppingCart toDomainEntity(ShoppingCartPersistenceEntity persistenceEntity) {
		return ShoppingCart.existing()
				.id(new ShoppingCartId(persistenceEntity.getId()))
				.customerId(new CustomerId(persistenceEntity.getCustomerId()))
				.totalAmount(new Money(persistenceEntity.getTotalAmount()))
				.totalItems(new Quantity(persistenceEntity.getTotalItems()))
				.createdAt(persistenceEntity.getCreatedAt())
				.version(persistenceEntity.getVersion())
				.items(toDomainEntity(persistenceEntity.getItems()))
				.build();
	}

	private Set<ShoppingCartItem> toDomainEntity(Set<ShoppingCartItemPersistenceEntity> items) {
		return items.stream()
				.map(this::toDomainEntity)
				.collect(Collectors.toSet());
	}

	private ShoppingCartItem toDomainEntity(ShoppingCartItemPersistenceEntity persistenceEntity) {
		return ShoppingCartItem.existing()
				.id(new ShoppingCartItemId(persistenceEntity.getId()))
				.shoppingCartId(new ShoppingCartId(persistenceEntity.getShoppingCartId()))
				.productId(new ProductId(persistenceEntity.getProductId()))
				.productName(new ProductName(persistenceEntity.getProductName()))
				.price(new Money(persistenceEntity.getPrice()))
				.quantity(new Quantity(persistenceEntity.getQuantity()))
				.available(persistenceEntity.getAvailable())
				.totalAmount(new Money(persistenceEntity.getTotalAmount()))
				.build();
	}

}
