package br.com.tigelah.ledgerservice.application.usecase;

public record Result(boolean recorded, boolean idempotent) {}
