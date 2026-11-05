package com.comercial.domain.billing;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record Invoice(
        String id,
        LocalDate issueDate,
        String currencyCode,
        Party supplier,
        Party customer,
        List<InvoiceLine> lines,
        TaxTotal taxTotal,
        MonetaryTotal monetaryTotal) {

    public Invoice {
        requireText(id, "id");
        Objects.requireNonNull(issueDate, "issueDate");
        requireText(currencyCode, "currencyCode");
        Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(customer, "customer");
        lines = List.copyOf(Objects.requireNonNull(lines, "lines"));
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Invoice must contain at least one line");
        }
        Objects.requireNonNull(monetaryTotal, "monetaryTotal");
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
