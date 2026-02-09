package com.uamishop.ordenes.domain.valueobject;

import com.uamishop.shared.domain.exception.BusinessRuleViolation;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object representing shipment information for an order.
 */
public final class ShipmentInfo {

    private final String trackingNumber;
    private final String carrier;
    private final LocalDateTime shippedAt;

    public ShipmentInfo(String trackingNumber, String carrier) {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new BusinessRuleViolation("INVALID_SHIPMENT", "Tracking number is required");
        }
        if (carrier == null || carrier.isBlank()) {
            throw new BusinessRuleViolation("INVALID_SHIPMENT", "Carrier is required");
        }
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.shippedAt = LocalDateTime.now();
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ShipmentInfo that = (ShipmentInfo) o;
        return trackingNumber.equals(that.trackingNumber) && carrier.equals(that.carrier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackingNumber, carrier);
    }

    @Override
    public String toString() {
        return carrier + ": " + trackingNumber;
    }
}
