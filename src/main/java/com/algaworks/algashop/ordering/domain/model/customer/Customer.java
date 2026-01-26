package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.AggregateRoot;
import com.algaworks.algashop.ordering.domain.model.commons.*;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.VALIDATION_ERROR_FULLNAME_IS_NULL;

public class Customer implements AggregateRoot<CustomerId> {

	private CustomerId id;
	private FullName fullName;
	private BirthDate birthDate;
	private Email email;
	private Phone phone;
	private Document document;
	private Boolean promotionNotificationsAllowed;
	private Boolean archived;
	private OffsetDateTime registeredAt;
	private OffsetDateTime archivedAt;
	private LoyaltyPoints loyaltyPoints;
	private Address address;
	private Long version;

	@Builder(builderClassName = "BrandNewCustomerBuild", builderMethodName = "brandNew")
	private static Customer createBrandNew(
			FullName fullName,
			BirthDate birthDate,
			Email email,
			Phone phone,
			Document document,
			Boolean promotionNotificationsAllowed,
			Address address
	) {
		return new Customer(
				new CustomerId(),
				null,
				fullName,
				birthDate,
				email,
				phone,
				document,
				promotionNotificationsAllowed,
				false,
				OffsetDateTime.now(),
				null,
				LoyaltyPoints.ZERO,
				address
		);
	}

	@Builder(builderClassName = "ExistingCustomerBuild", builderMethodName = "existing")
	private Customer(CustomerId id, Long version, FullName fullName, BirthDate birthDate, Email email, Phone phone,
					 Document document, Boolean promotionNotificationsAllowed, Boolean archived,
					 OffsetDateTime registeredAt, OffsetDateTime archivedAt, LoyaltyPoints loyaltyPoints, Address address) {
		setId(id);
		setVersion(version);
		setFullName(fullName);
		setBirthDate(birthDate);
		setEmail(email);
		setPhone(phone);
		setDocument(document);
		setPromotionNotificationsAllowed(promotionNotificationsAllowed);
		setArchived(archived);
		setRegisteredAt(registeredAt);
		setArchivedAt(archivedAt);
		setLoyaltyPoints(loyaltyPoints);
		setAddress(address);
	}

	public void addLoyaltyPoints(LoyaltyPoints loyaltyPointsAdded) {
		verifyIfChangeable();

		if (loyaltyPointsAdded.equals(LoyaltyPoints.ZERO)) {
			return;
		}

		setLoyaltyPoints(loyaltyPoints().add(loyaltyPointsAdded));
	}

	public void archive() {
		verifyIfChangeable();
		setArchived(true);
		setArchivedAt(OffsetDateTime.now());
		setFullName(new FullName("Anonymous", "Anonymous"));
		setPhone(new Phone("000-000-0000"));
		setDocument(new Document("000-00-0000"));
		setEmail(new Email(UUID.randomUUID() + "@anonymous.com"));
		setBirthDate(null);
		setPromotionNotificationsAllowed(false);
		setAddress(address().toBuilder().number("Anonymized").complement(null).build());
	}

	public void enablePromotionNotifications() {
		verifyIfChangeable();
		setPromotionNotificationsAllowed(true);
	}

	public void disablePromotionNotifications() {
		verifyIfChangeable();
		setPromotionNotificationsAllowed(false);
	}

	public void changeName(FullName fullName) {
		verifyIfChangeable();
		setFullName(fullName);
	}

	public void changeEmail(Email email) {
		verifyIfChangeable();
		setEmail(email);
	}

	public void changePhone(Phone phone) {
		verifyIfChangeable();
		setPhone(phone);
	}

	public void changeAddress(Address address) {
		verifyIfChangeable();
		setAddress(address);
	}

	public Long version() {
		return version;
	}

	private void setVersion(Long version) {
		this.version = version;
	}

	public CustomerId id() {
		return id;
	}

	public FullName fullName() {
		return fullName;
	}

	public BirthDate birthDate() {
		return birthDate;
	}

	public Email email() {
		return email;
	}

	public Phone phone() {
		return phone;
	}

	public Document document() {
		return document;
	}

	public Boolean isPromotionNotificationsAllowed() {
		return promotionNotificationsAllowed;
	}

	public boolean isArchived() {
		return Boolean.TRUE.equals(archived);
	}

	public OffsetDateTime registeredAt() {
		return registeredAt;
	}

	public OffsetDateTime archivedAt() {
		return archivedAt;
	}

	public LoyaltyPoints loyaltyPoints() {
		return loyaltyPoints;
	}

	public Address address() {
		return address;
	}

	private void setId(CustomerId id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	private void setFullName(FullName fullName) {
		Objects.requireNonNull(fullName, VALIDATION_ERROR_FULLNAME_IS_NULL);
		this.fullName = fullName;
	}

	private void setBirthDate(BirthDate birthDate) {
		if (birthDate == null) {
			this.birthDate = null;
			return;
		}
		this.birthDate = birthDate;
	}

	private void setEmail(Email email) {
		Objects.requireNonNull(email);
		this.email = email;
	}

	private void setPhone(Phone phone) {
		Objects.requireNonNull(phone);
		this.phone = phone;
	}

	private void setDocument(Document document) {
		Objects.requireNonNull(document);
		this.document = document;
	}

	private void setPromotionNotificationsAllowed(Boolean promotionNotificationsAllowed) {
		Objects.requireNonNull(promotionNotificationsAllowed);
		this.promotionNotificationsAllowed = promotionNotificationsAllowed;
	}

	private void setArchived(Boolean archived) {
		Objects.requireNonNull(archived);
		this.archived = archived;
	}

	private void setRegisteredAt(OffsetDateTime registeredAt) {
		Objects.requireNonNull(registeredAt);
		this.registeredAt = registeredAt;
	}

	private void setArchivedAt(OffsetDateTime archivedAt) {
		this.archivedAt = archivedAt;
	}

	private void setLoyaltyPoints(LoyaltyPoints loyaltyPoints) {
		Objects.requireNonNull(loyaltyPoints);
		this.loyaltyPoints = loyaltyPoints;
	}

	private void setAddress(Address address) {
		Objects.requireNonNull(address);
		this.address = address;
	}

	private void verifyIfChangeable() {
		if (this.isArchived()) {
			throw new CustomerArchivedException();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Customer customer = (Customer) o;
		return Objects.equals(id, customer.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}