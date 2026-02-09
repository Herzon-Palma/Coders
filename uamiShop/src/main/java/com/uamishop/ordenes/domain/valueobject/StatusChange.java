package com.uamishop.ordenes.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object representing a status change in the order history.
 */
public final class StatusChange {

    private final OrdenStatus from;
    private final OrdenStatus to;
    private final LocalDateTime changedAt;
    private final String note;

    public StatusChange(OrdenStatus from, OrdenStatus to, String note) {
        this.from = from;
        this.to = to;
        this.changedAt = LocalDateTime.now();
        this.note = note != null ? note : "";
    }

    public OrdenStatus getFrom() {
        return from;
    }

    public OrdenStatus getTo() {
        return to;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StatusChange that = (StatusChange) o;
        return from == that.from && to == that.to && changedAt.equals(that.changedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, changedAt);
    }

    @Override
    public String toString() {
        return from + " -> " + to + " at " + changedAt + (note.isEmpty() ? "" : " (" + note + ")");
    }
}
