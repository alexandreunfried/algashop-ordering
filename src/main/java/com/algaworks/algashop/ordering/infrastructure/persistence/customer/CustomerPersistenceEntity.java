package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString(of = "id")
@Table(name = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CustomerPersistenceEntity extends AbstractAggregateRoot<CustomerPersistenceEntity> {

	@Id
	@EqualsAndHashCode.Include
	private UUID id;

	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private String email;
	private String phone;
	private String document;
	private Boolean promotionNotificationsAllowed;
	private Boolean archived;
	private OffsetDateTime registeredAt;
	private OffsetDateTime archivedAt;
	private Integer loyaltyPoints;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "address.street", column = @Column(name = "customer_address_street")),
			@AttributeOverride(name = "address.number", column = @Column(name = "customer_address_number")),
			@AttributeOverride(name = "address.complement", column = @Column(name = "customer_address_complement")),
			@AttributeOverride(name = "address.neighborhood", column = @Column(name = "customer_address_neighborhood")),
			@AttributeOverride(name = "address.city", column = @Column(name = "customer_address_city")),
			@AttributeOverride(name = "address.state", column = @Column(name = "customer_address_state")),
			@AttributeOverride(name = "address.zipCode", column = @Column(name = "customer_address_zip_code"))
	})
	private AddressEmbeddable address;

	@CreatedBy
	private UUID createdByUserId;

	@LastModifiedBy
	private UUID lastModifiedByUserId;

	@LastModifiedDate
	private OffsetDateTime lastModifiedAt;

	@Version
	private Long version;

	public Collection<Object> getEvents() {
		return super.domainEvents();
	}

	public void addEvents(Collection<Object> events) {
		if (events != null) {
			for (Object event : events) {
				registerEvent(event);
			}
		}
	}
}
