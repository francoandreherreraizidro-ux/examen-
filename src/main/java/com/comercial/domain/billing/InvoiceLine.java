package com.comercial.domain.billing;

import java.math.BigDecimal;
import java.util.Objects;

public record InvoiceLine(
        String id,
        BigDecimal quantity,
        String itemName,
        BigDecimal unitPrice,
        BigDecimal lineExtensionAmount) {

    public InvoiceLine {
        requireText(id, "id");
        Objects.requireNonNull(quantity, "quantity");
        requireText(itemName, "itemName");
        Objects.requireNonNull(unitPrice, "unitPrice");
        Objects.requireNonNull(lineExtensionAmount, "lineExtensionAmount");
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
