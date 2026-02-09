package com.uamishop.checkout.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;

/**
 * Value Object representing a shipping address.
 */
public final class DireccionEnvio {

    private final String recipientName;
    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String phone;

    public DireccionEnvio(String recipientName, String street, String city,
            String state, String zipCode, String phone) {
        validate(recipientName, street, city, state, zipCode, phone);
        this.recipientName = recipientName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.phone = phone;
    }

    private void validate(String recipientName, String street, String city,
            String state, String zipCode, String phone) {
        if (recipientName == null || recipientName.isBlank()) {
            throw new BusinessRuleViolation("INVALID_ADDRESS", "Recipient name is required");
        }
        if (street == null || street.isBlank()) {
            throw new BusinessRuleViolation("INVALID_ADDRESS", "Street is required");
        }
        if (city == null || city.isBlank()) {
            throw new BusinessRuleViolation("INVALID_ADDRESS", "City is required");
        }
        if (state == null || state.isBlank()) {
            throw new BusinessRuleViolation("INVALID_ADDRESS", "State is required");
        }
        if (zipCode == null || zipCode.isBlank()) {
            throw new BusinessRuleViolation("INVALID_ADDRESS", "Zip code is required");
        }
        if (phone == null || phone.isBlank()) {
            throw new BusinessRuleViolation("INVALID_ADDRESS", "Phone is required");
        }
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DireccionEnvio that = (DireccionEnvio) o;
        return recipientName.equals(that.recipientName) &&
                street.equals(that.street) &&
                city.equals(that.city) &&
                state.equals(that.state) &&
                zipCode.equals(that.zipCode) &&
                phone.equals(that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipientName, street, city, state, zipCode, phone);
    }

    @Override
    public String toString() {
        return recipientName + ", " + street + ", " + city + ", " + state + " " + zipCode;
    }
}
