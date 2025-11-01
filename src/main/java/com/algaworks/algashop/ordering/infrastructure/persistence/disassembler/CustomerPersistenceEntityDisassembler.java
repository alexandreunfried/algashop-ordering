package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityDisassembler {

	public Customer toDomainEntity(CustomerPersistenceEntity persistenceEntity) {
		return Customer.existing()
				.id(new CustomerId(persistenceEntity.getId()))
				.fullName(new FullName(persistenceEntity.getFirstName(), persistenceEntity.getLastName()))
				.birthDate(new BirthDate(persistenceEntity.getBirthDate()))
				.email(new Email(persistenceEntity.getEmail()))
				.phone(new Phone(persistenceEntity.getPhone()))
				.document(new Document(persistenceEntity.getDocument()))
				.promotionNotificationsAllowed(persistenceEntity.getPromotionNotificationsAllowed())
				.archived(persistenceEntity.getArchived())
				.registeredAt(persistenceEntity.getRegisteredAt())
				.archivedAt(persistenceEntity.getArchivedAt())
				.loyaltyPoints(new LoyaltyPoints(persistenceEntity.getLoyaltyPoints()))
				.address(AddressEmbeddableDisassembler.toAddress(persistenceEntity.getAddress()))
				.version(persistenceEntity.getVersion())
				.build();
	}

}
