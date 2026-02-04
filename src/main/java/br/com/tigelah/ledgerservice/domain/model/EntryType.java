package br.com.tigelah.ledgerservice.domain.model;

public enum EntryType {
    HOLD,
    RELEASE_HOLD,
    CAPTURE_PRINCIPAL,
    CAPTURE_INTEREST,
    REFUND_PRINCIPAL,
    REFUND_INTEREST,
    SETTLEMENT_NET,
    SETTLEMENT_FEE
}
