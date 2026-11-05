package com.comercial.domain.billing;

import java.math.BigDecimal;
import java.util.Objects;

public record TaxTotal(BigDecimal amount) {
    public TaxTotal {
        Objects.requireNonNull(amount, "amount");
    }
}
