package br.com.tigelah.ledgerservice.infrastructure.persistence.entities;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class SpendCounterId implements Serializable {
    public String scopeType;
    public String scopeKey;
    public String periodType;
    public Instant periodStart;

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpendCounterId other)) return false;
        return Objects.equals(scopeType, other.scopeType)
                && Objects.equals(scopeKey, other.scopeKey)
                && Objects.equals(periodType, other.periodType)
                && Objects.equals(periodStart, other.periodStart);
    }

    @Override public int hashCode() {
        return Objects.hash(scopeType, scopeKey, periodType, periodStart);
    }
}
