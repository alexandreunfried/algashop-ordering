package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class BillingData {
	private String firstName;
	private String lastName;
	private String document;
	private String email;
	private String phone;
	private AddressData address;
}
