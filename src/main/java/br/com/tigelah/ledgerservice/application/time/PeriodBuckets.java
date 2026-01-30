package br.com.tigelah.ledgerservice.application.time;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class PeriodBuckets {
    private PeriodBuckets() {}

    public static Instant dayStartUtc(Instant instant) {
        var z = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
        return z.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public static Instant monthStartUtc(Instant instant) {
        var z = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
        return z.withDayOfMonth(1).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
