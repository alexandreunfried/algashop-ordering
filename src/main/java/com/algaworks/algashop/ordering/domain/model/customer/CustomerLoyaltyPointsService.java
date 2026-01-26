package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;

import java.util.Objects;

@DomainService
public class CustomerLoyaltyPointsService {

	private static final LoyaltyPoints BASE_POINTS = new LoyaltyPoints(5);

	private static final Money EXPECTED_AMOUNT_TO_GIVE_POINTS = new Money("1000");

	public void addPoints(Customer customer, Order order) {

		Objects.requireNonNull(customer);
		Objects.requireNonNull(order);

		if (!customer.id().equals(order.customerId())) {
			throw new OrderNotBelongsToCustomerException();
		}

		if (!order.isReady()) {
			throw new CantAddLoyaltyPointOrderIsNotReady();
		}

		customer.addLoyaltyPoints(calculatePoints(order));
	}

	private LoyaltyPoints calculatePoints(Order order) {
		if (shouldGivePointsByAmount(order.totalAmount())) {
			Money result = order.totalAmount().divide(EXPECTED_AMOUNT_TO_GIVE_POINTS);
			return new LoyaltyPoints(result.value().intValue() * BASE_POINTS.value());
		}

		return LoyaltyPoints.ZERO;
	}

	private boolean shouldGivePointsByAmount(Money amount) {
		return amount.compareTo(EXPECTED_AMOUNT_TO_GIVE_POINTS) >= 0;
	}

}
