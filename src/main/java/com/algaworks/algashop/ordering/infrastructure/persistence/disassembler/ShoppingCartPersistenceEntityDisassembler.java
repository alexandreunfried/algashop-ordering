package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
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
