package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

	private final CustomerNotificationApplicationService customerNotificationService;
	private final CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

	@EventListener
	public void listen(CustomerRegisteredEvent event) {
		log.info("CustomerRegisteredEvent listen 1");
		customerNotificationService.notifyNewRegistration(new CustomerNotificationApplicationService.NotifyNewRegistrationInput(
				event.customerId().value(),
				event.fullName().firstName(),
				event.email().value()
		));
	}

	@EventListener
	public void listen(CustomerArchivedEvent event) {
		log.info("CustomerArchivedEvent listen 1");
	}

	@EventListener
	public void listen(OrderReadyEvent event) {
		customerLoyaltyPointsApplicationService.addLoyaltyPoints(event.customerId().value(),
				event.orderId().toString());
	}

}
