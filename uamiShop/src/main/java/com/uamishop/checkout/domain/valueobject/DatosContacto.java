package com.uamishop.checkout.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.util.Objects;

/**
 * Value Object representing contact information.
 */
public final class DatosContacto {

    private final String email;
    private final String phone;

    public DatosContacto(String email, String phone) {
        validate(email, phone);
        this.email = email;
        this.phone = phone;
    }

    private void validate(String email, String phone) {
        if (email == null || email.isBlank()) {
            throw new BusinessRuleViolation("INVALID_CONTACT", "Email is required");
        }
        if (!email.contains("@")) {
            throw new BusinessRuleViolation("INVALID_CONTACT", "Invalid email format");
        }
        if (phone == null || phone.isBlank()) {
            throw new BusinessRuleViolation("INVALID_CONTACT", "Phone is required");
        }
    }

    public String getEmail() {
        return email;
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
        DatosContacto that = (DatosContacto) o;
        return email.equals(that.email) && phone.equals(that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phone);
    }

    @Override
    public String toString() {
        return email + " / " + phone;
    }
}
