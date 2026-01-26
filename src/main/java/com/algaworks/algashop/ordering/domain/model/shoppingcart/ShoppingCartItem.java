package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import lombok.Builder;

import java.util.Objects;

public class ShoppingCartItem {

	private ShoppingCartItemId id;
	private ShoppingCartId shoppingCartId;
	private ProductId productId;
	private ProductName productName;
	private Money price;
	private Quantity quantity;
	private Boolean available;
	private Money totalAmount;

	@Builder(builderClassName = "ExistingShoppingCartItem", builderMethodName = "existing")
	public ShoppingCartItem(ShoppingCartItemId id, ShoppingCartId shoppingCartId, ProductId productId, ProductName productName,
							Money price, Quantity quantity, Boolean available, Money totalAmount) {
		setId(id);
		setShoppingCartId(shoppingCartId);
		setProductId(productId);
		setProductName(productName);
		setPrice(price);
		setQuantity(quantity);
		setAvailable(available);
		setTotalAmount(totalAmount);
	}

	@Builder(builderClassName = "BrandNewShoppingCartItem", builderMethodName = "brandNew")
	public ShoppingCartItem(ShoppingCartId shoppingCartId,
							ProductId productId, ProductName productName, Money price,
							Quantity quantity, Boolean available) {
		this(new ShoppingCartItemId(), shoppingCartId, productId, productName, price, quantity, available, Money.ZERO);
		recalculateTotals();
	}

	void refresh(Product product) {
		Objects.requireNonNull(product);
		Objects.requireNonNull(product.id());

		if (!product.id().equals(productId())) {
			throw new ShoppingCartItemIncompatibleProductException(id(), productId());
		}

		setPrice(product.price());
		setAvailable(product.inStock());
		setProductName(product.name());
		recalculateTotals();
	}

	void changeQuantity(Quantity quantity) {
		setQuantity(quantity);
		recalculateTotals();
	}

	private void recalculateTotals() {
		setTotalAmount(price.multiply(quantity));
	}

	public ShoppingCartItemId id() {
		return id;
	}

	public ShoppingCartId shoppingCartId() {
		return shoppingCartId;
	}

	public ProductId productId() {
		return productId;
	}

	public ProductName productName() {
		return productName;
	}

	public Money price() {
		return price;
	}

	public Quantity quantity() {
		return quantity;
	}

	public Boolean isAvailable() {
		return available;
	}

	public Money totalAmount() {
		return totalAmount;
	}

	private void setId(ShoppingCartItemId id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	private void setShoppingCartId(ShoppingCartId shoppingCartId) {
		Objects.requireNonNull(shoppingCartId);
		this.shoppingCartId = shoppingCartId;
	}

	private void setProductId(ProductId productId) {
		Objects.requireNonNull(productId);
		this.productId = productId;
	}

	private void setProductName(ProductName productName) {
		Objects.requireNonNull(productName);
		this.productName = productName;
	}

	private void setPrice(Money price) {
		Objects.requireNonNull(price);
		this.price = price;
	}

	private void setQuantity(Quantity quantity) {
		Objects.requireNonNull(quantity);
		if (quantity.equals(Quantity.ZERO)) {
			throw new IllegalArgumentException();
		}
		this.quantity = quantity;
	}

	private void setAvailable(Boolean available) {
		Objects.requireNonNull(available);
		this.available = available;
	}

	private void setTotalAmount(Money totalAmount) {
		Objects.requireNonNull(totalAmount);
		this.totalAmount = totalAmount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShoppingCartItem that = (ShoppingCartItem) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
