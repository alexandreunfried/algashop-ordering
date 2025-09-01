package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.validator.FieldValidations;
import com.algaworks.algashop.ordering.domain.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.LoyaltyPoints;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.*;

public class Customer {

	private CustomerId id;
	private FullName fullName;
	private LocalDate birthDate;
	private String email;
	private String phone;
	private String document;
	private Boolean promotionNotificationsAllowed;
	private Boolean archived;
	private OffsetDateTime registeredAt;
	private OffsetDateTime archivedAt;
	private LoyaltyPoints loyaltyPoints;

	public Customer(CustomerId id, FullName fullName, LocalDate birthDate, String email,
					String phone, String document, Boolean promotionNotificationsAllowed,
					OffsetDateTime registeredAt) {
		setId(id);
		setFullName(fullName);
		setBirthDate(birthDate);
		setEmail(email);
		setPhone(phone);
		setDocument(document);
		setPromotionNotificationsAllowed(promotionNotificationsAllowed);
		setRegisteredAt(registeredAt);
		setArchived(false);
		setLoyaltyPoints(LoyaltyPoints.ZERO);
	}

	public Customer(CustomerId id, FullName fullName, LocalDate birthDate, String email, String phone,
					String document, Boolean promotionNotificationsAllowed, Boolean archived,
					OffsetDateTime registeredAt, OffsetDateTime archivedAt, LoyaltyPoints loyaltyPoints) {
		setId(id);
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
	}

	public void addLoyaltyPoints(LoyaltyPoints loyaltyPointsAdded) {
		verifyIfChangeable();
		setLoyaltyPoints(loyaltyPoints().add(loyaltyPointsAdded));
	}

	public void archive() {
		verifyIfChangeable();
		setArchived(true);
		setArchivedAt(OffsetDateTime.now());
		setFullName(new FullName("Anonymous", "Anonymous"));
		setPhone("000-000-0000");
		setDocument("000-00-0000");
		setEmail(UUID.randomUUID() + "@anonymous.com");
		setBirthDate(null);
		setPromotionNotificationsAllowed(false);
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

	public void changeEmail(String email) {
		verifyIfChangeable();
		setEmail(email);
	}

	public void changePhone(String phone) {
		verifyIfChangeable();
		setPhone(phone);
	}

	public CustomerId id() {
		return id;
	}

	public FullName fullName() {
		return fullName;
	}

	public LocalDate birthDate() {
		return birthDate;
	}

	public String email() {
		return email;
	}

	public String phone() {
		return phone;
	}

	public String document() {
		return document;
	}

	public Boolean isPromotionNotificationsAllowed() {
		return promotionNotificationsAllowed;
	}

	public Boolean isArchived() {
		return archived;
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

	private void setId(CustomerId id) {
		this.id = id;
	}

	private void setFullName(FullName fullName) {
		Objects.requireNonNull(fullName, VALIDATION_ERROR_FULLNAME_IS_NULL);
		this.fullName = fullName;
	}

	private void setBirthDate(LocalDate birthDate) {
		if (birthDate == null) {
			this.birthDate = null;
			return;
		}
		if (birthDate.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException(VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
		}
		this.birthDate = birthDate;
	}

	private void setEmail(String email) {
		FieldValidations.requiresValidEmail(email, VALIDATION_ERROR_EMAIL_IS_INVALID);
		this.email = email;
	}

	private void setPhone(String phone) {
		Objects.requireNonNull(phone);
		this.phone = phone;
	}

	private void setDocument(String document) {
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

	private void verifyIfChangeable() {
		if (isArchived()) {
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
