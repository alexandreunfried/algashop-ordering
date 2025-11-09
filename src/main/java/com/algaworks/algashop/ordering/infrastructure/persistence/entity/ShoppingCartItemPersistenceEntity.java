package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "shopping_cart_item")
@Getter
@Setter
@ToString(of = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartItemPersistenceEntity {

	@Id
	@EqualsAndHashCode.Include
	private UUID id;

	@JoinColumn
	@ManyToOne(optional = false)
	private ShoppingCartPersistenceEntity shoppingCart;

	private UUID productId;
	private String productName;
	private BigDecimal price;
	private Integer quantity;
	private BigDecimal totalAmount;
	private Boolean available;


	public UUID getShoppingCartId() {

		if (getShoppingCart() == null) {
			return null;
		}

		return getShoppingCart().getId();
	}

}
