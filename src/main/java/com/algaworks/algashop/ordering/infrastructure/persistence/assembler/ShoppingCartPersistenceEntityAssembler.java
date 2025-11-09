package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ShoppingCartPersistenceEntityAssembler {

	private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

	public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
		return merge(new ShoppingCartPersistenceEntity(), shoppingCart);
	}

	public ShoppingCartPersistenceEntity merge(
			ShoppingCartPersistenceEntity persistenceEntity,
			ShoppingCart shoppingCart
	) {
		persistenceEntity.setId(shoppingCart.id().value());
		persistenceEntity.setCustomer(customerPersistenceEntityRepository.getReferenceById(shoppingCart.customerId().value()));
		persistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
		persistenceEntity.setTotalItems(shoppingCart.totalItems().value());
		persistenceEntity.setCreatedAt(shoppingCart.createdAt());
		persistenceEntity.replaceItems(toOrderItemsEntities(shoppingCart.items()));
		return persistenceEntity;
	}

	private Set<ShoppingCartItemPersistenceEntity> toOrderItemsEntities(Set<ShoppingCartItem> source) {
		return source.stream()
				.map(i -> this.mergeItem(new ShoppingCartItemPersistenceEntity(), i))
				.collect(Collectors.toSet());
	}

	private ShoppingCartItemPersistenceEntity mergeItem(
			ShoppingCartItemPersistenceEntity persistenceEntity,
			ShoppingCartItem shoppingCartItem
	) {
		persistenceEntity.setId(shoppingCartItem.id().value());
		persistenceEntity.setProductId(shoppingCartItem.productId().value());
		persistenceEntity.setProductName(shoppingCartItem.productName().value());
		persistenceEntity.setPrice(shoppingCartItem.price().value());
		persistenceEntity.setQuantity(shoppingCartItem.quantity().value());
		persistenceEntity.setAvailable(shoppingCartItem.isAvailable());
		persistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());
		return persistenceEntity;
	}

}
