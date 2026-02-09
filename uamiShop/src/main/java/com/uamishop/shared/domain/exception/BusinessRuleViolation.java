package com.uamishop.shared.domain.exception;

/**
 * Exception thrown when a business rule is violated.
 * More specific than DomainException, used for business logic validations.
 */
public class BusinessRuleViolation extends DomainException {

    private final String ruleName;

    public BusinessRuleViolation(String ruleName, String message) {
        super(message);
        this.ruleName = ruleName;
    }

    public BusinessRuleViolation(String message) {
        super(message);
        this.ruleName = "UNKNOWN";
    }

    public String getRuleName() {
        return ruleName;
    }
}
