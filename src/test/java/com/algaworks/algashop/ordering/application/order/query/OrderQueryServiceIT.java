package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderQueryServiceIT {

	@Autowired
	private OrderQueryService queryService;

	@Autowired
	private Orders orders;

	@Autowired
	private Customers customers;

	@Test
	void shouldFindById() {
		Customer customer = CustomerTestDataBuilder.existingCustomer().build();
		customers.add(customer);

		Order order = OrderTestDataBuilder.anOrder().customerId(customer.id()).build();
		orders.add(order);

		OrderDetailOutput output = queryService.findById(order.id().toString());

		Assertions.assertThat(output)
				.extracting(
						OrderDetailOutput::getId,
						OrderDetailOutput::getTotalAmount
				)
				.containsExactly(
						order.id().toString(),
						order.totalAmount().value()
				);
	}

	@Test
	void shouldFilterByPage() {
		Customer customer = CustomerTestDataBuilder.existingCustomer().build();
		customers.add(customer);

		Order order1 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.DRAFT)
				.withItems(false)
				.customerId(customer.id())
				.build();

		orders.add(order1);

		Order order2 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PLACED)
				.customerId(customer.id())
				.build();

		orders.add(order2);

		Order order3 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PAID)
				.customerId(customer.id())
				.build();

		orders.add(order3);

		Order order4 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.READY)
				.customerId(customer.id())
				.build();

		orders.add(order4);

		Order order5 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.CANCELED)
				.customerId(customer.id())
				.build();

		orders.add(order5);

		Page<OrderSummaryOutput> page = queryService.filter(new OrderFilter(3, 0));

		Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
		Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
		Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
	}

	@Test
	void shouldFilterByCustomerId() {
		Customer customer1 = CustomerTestDataBuilder.existingCustomer().build();
		customers.add(customer1);

		Order order1 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.DRAFT)
				.withItems(false)
				.customerId(customer1.id())
				.build();

		orders.add(order1);

		Order order2 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PLACED)
				.customerId(customer1.id())
				.build();

		orders.add(order2);

		Customer customer2 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
		customers.add(customer2);
		Order order3 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PAID)
				.customerId(customer2.id())
				.build();

		orders.add(order3);

		Order order4 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.READY)
				.customerId(customer2.id())
				.build();

		orders.add(order4);

		Order order5 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.CANCELED)
				.customerId(customer2.id())
				.build();

		orders.add(order5);

		OrderFilter filter = new OrderFilter();
		filter.setCustomerId(customer1.id().value());
		Page<OrderSummaryOutput> page = queryService.filter(filter);

		Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
		Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
		Assertions.assertThat(page.getNumberOfElements()).isEqualTo(2);
	}

	@Test
	void shouldFilterByMultipleParams() {
		Customer customer1 = CustomerTestDataBuilder.existingCustomer().build();
		customers.add(customer1);

		Order order1 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.DRAFT)
				.withItems(false)
				.customerId(customer1.id())
				.build();

		orders.add(order1);

		Order order2 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PLACED)
				.customerId(customer1.id())
				.build();

		orders.add(order2);

		Customer customer2 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
		customers.add(customer2);
		Order order3 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PAID)
				.customerId(customer2.id())
				.build();

		orders.add(order3);

		Order order4 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.READY)
				.customerId(customer2.id())
				.build();

		orders.add(order4);

		Order order5 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.CANCELED)
				.customerId(customer2.id())
				.build();

		orders.add(order5);

		OrderFilter filter = new OrderFilter();
		filter.setCustomerId(customer1.id().value());
		filter.setStatus(OrderStatus.PLACED.toString().toLowerCase());
		filter.setTotalAmountFrom(order1.totalAmount().value());

		Page<OrderSummaryOutput> page = queryService.filter(filter);

		Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
		Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
		Assertions.assertThat(page.getNumberOfElements()).isEqualTo(1);
	}

	@Test
	void givenInvalidOrderId_whenFilter_shouldReturnEmptyPage() {
		Customer customer1 = CustomerTestDataBuilder.existingCustomer().build();
		customers.add(customer1);

		Order order1 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.DRAFT)
				.withItems(false)
				.customerId(customer1.id())
				.build();

		orders.add(order1);

		Order order2 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PLACED)
				.customerId(customer1.id())
				.build();

		orders.add(order2);

		Customer customer2 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
		customers.add(customer2);
		Order order3 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PAID)
				.customerId(customer2.id())
				.build();

		orders.add(order3);

		Order order4 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.READY)
				.customerId(customer2.id())
				.build();

		orders.add(order4);

		Order order5 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.CANCELED)
				.customerId(customer2.id())
				.build();

		orders.add(order5);

		OrderFilter filter = new OrderFilter();
		filter.setOrderId("ABC");

		Page<OrderSummaryOutput> page = queryService.filter(filter);

		Assertions.assertThat(page.getTotalPages()).isZero();
		Assertions.assertThat(page.getTotalElements()).isZero();
		Assertions.assertThat(page.getNumberOfElements()).isZero();
	}

	@Test
	void shouldOrderByStatus() {
		Customer customer1 = CustomerTestDataBuilder.existingCustomer().build();
		customers.add(customer1);

		Order order1 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.DRAFT)
				.withItems(false)
				.customerId(customer1.id())
				.build();

		orders.add(order1);

		Order order2 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PLACED)
				.customerId(customer1.id())
				.build();

		orders.add(order2);

		Customer customer2 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
		customers.add(customer2);
		Order order3 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.PAID)
				.customerId(customer2.id())
				.build();

		orders.add(order3);

		Order order4 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.READY)
				.customerId(customer2.id())
				.build();

		orders.add(order4);

		Order order5 = OrderTestDataBuilder.anOrder()
				.status(OrderStatus.CANCELED)
				.customerId(customer2.id())
				.build();

		orders.add(order5);

		OrderFilter filter = new OrderFilter();
		filter.setSortByProperty(OrderFilter.SortType.STATUS);
		filter.setSortDirection(Sort.Direction.ASC);

		Page<OrderSummaryOutput> page = queryService.filter(filter);

		Assertions.assertThat(page.getContent().getFirst().getStatus()).isEqualTo(OrderStatus.CANCELED.toString());
	}

}