package br.com.tigelah.ledgerservice.infrastructure.messaging;

public final class Topics {
    private Topics() {}

    public static final String PAYMENT_AUTHORIZED = "payment.authorized";
    public static final String PAYMENT_CAPTURED = "payment.captured";
    public static final String REFUND_ISSUED = "refund.issued";

    public static final String LEDGER_ENTRY_RECORDED = "ledger.entry.recorded";
}